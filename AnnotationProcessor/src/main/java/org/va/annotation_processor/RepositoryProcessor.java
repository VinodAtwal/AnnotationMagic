package org.va.annotation_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.va.annotations.Id;
import org.va.annotations.Repository;
import org.va.sqlhelper.SqlUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;



@SupportedAnnotationTypes("org.va.annotations.Repository")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class RepositoryProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Repository.class)) {
            String className = element.getSimpleName() + "Dao";
            String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
            String entityClassName = element.getSimpleName().toString();
            createDaoClass(element, entityClassName, packageName, className);
        }
        return true;
    }

    /**
     * Currently supports only simple class filed we can extend this code for more complex Table Structure
     * */
    private void createDaoClass(Element element, String entityClassName, String packageName, String className) {
        System.out.println("calling create Dao element");

        String idType = element.getEnclosedElements().stream().filter(e -> e.getKind().isField()).filter(ele -> ele.getAnnotation(Id.class) != null).map(ele -> ele.asType().toString()).findFirst().orElse("String");
        Class idClass = mapJavaTypeToClass(idType);
        long fieldCount = element.getEnclosedElements().stream().filter(e -> e.getKind().isField()).count();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addException(ClassName.get("java.sql", "SQLException"))
                .addParameter(ClassName.get("java.sql", "Connection"), "connection")
                .addStatement("this.connection = connection")
                .addStatement("createTableIfNotExists()")
                .build();

        MethodSpec.Builder builder = MethodSpec.methodBuilder("create" + entityClassName)
                .addModifiers(Modifier.PUBLIC)
                .addException(ClassName.get("java.sql", "SQLException"))
                .addParameter(ClassName.get(packageName, entityClassName), "entity")
                .addStatement("$T sql = $S", String.class, "INSERT INTO " + entityClassName + " VALUES (" + LongStream.range(0, fieldCount)
                        .mapToObj(i -> "?")
                        .collect(Collectors.joining(",")) + ")")
                .beginControlFlow("try ($T stmt = connection.prepareStatement(sql))",
                        ClassName.get("java.sql", "PreparedStatement"));
        AtomicInteger cnt= new AtomicInteger();
        element.getEnclosedElements().stream()
                .filter(e -> e.getKind().isExecutable())
                .filter(e->e.getSimpleName().toString().startsWith("get"))
                .forEach(e->builder.addStatement("stmt.setObject("
                        + cnt.incrementAndGet() +", entity."+e.getSimpleName().toString()+"())"));
        MethodSpec createUser =
                builder.addStatement("stmt.executeUpdate()")
        .endControlFlow()
                .build();
                // Generate createTableIfNotExists method
        MethodSpec createTableIfNotExists = MethodSpec.methodBuilder("createTableIfNotExists")
                .addModifiers(Modifier.PRIVATE)
                .addException(ClassName.get("java.sql", "SQLException"))
                .addCode(SqlUtil.generateCreateTableSQL(entityClassName, element))
                .beginControlFlow("try ($T stmt = connection.createStatement())", ClassName.get("java.sql", "Statement"))
                .addStatement("stmt.execute(sql)")
                .endControlFlow()
                .build();

        Function<String, String> convert = idType1 -> {
            String[] split = idType1.split("\\.");
            String end = split[split.length - 1];
            return end.equalsIgnoreCase("Integer") ? "Int" :
                    Character.toUpperCase(end.charAt(0)) + end.substring(1);
        };
        // Generate getEntity method
        MethodSpec getEntity = MethodSpec.methodBuilder("get" + entityClassName)
                .addModifiers(Modifier.PUBLIC)
                .addException(ClassName.get("java.sql", "SQLException"))
                .addParameter(idClass, "id")
                .returns(ClassName.get(packageName, entityClassName))
                .addStatement("$T sql = $S", String.class, "SELECT * FROM " + entityClassName + " WHERE id = ?")
                .addStatement("$T entity = null", ClassName.get(packageName, entityClassName))
                .beginControlFlow("try ($T stmt = connection.prepareStatement(sql))", ClassName.get("java.sql", "PreparedStatement"))
                .addStatement("stmt.set"+
                       convert.apply(idType)

        +"(1, id)")
                .beginControlFlow("try ($T rs = stmt.executeQuery())", ClassName.get("java.sql", "ResultSet"))
                .beginControlFlow("if (rs.next())")
                .addStatement("entity = new $T()", ClassName.get(packageName, entityClassName))
                .addCode(generateFieldSetters(element))
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .addStatement("return entity")
                .build();


        TypeSpec daoClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(ClassName.get("java.sql", "Connection"), "connection", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(constructor)
                .addMethod(createUser)
                .addMethod(createTableIfNotExists)
                .addMethod(getEntity)
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, daoClass).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new RuntimeException("Unable to process Annotation");
        }
    }

    private CodeBlock generateFieldSetters(Element element) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField()) {
                String fieldName = field.getSimpleName().toString();
                String fieldType = field.asType().toString();
                String resultSetGetter = mapSQLTypeToResultSetGetter(fieldType);

                builder.addStatement("entity.set$L(rs.$L($S))",
                        fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), resultSetGetter, fieldName);
            }
        }
        return builder.build();
    }

    private String mapSQLTypeToResultSetGetter(String javaType) {
        return switch (javaType) {
            case "int", "java.lang.Integer" -> "getInt";
            case "long", "java.lang.Long" -> "getLong";
            case "java.lang.String" -> "getString";
            case "boolean", "java.lang.Boolean" -> "getBoolean";
            case "java.util.Date", "java.time.LocalDate", "java.time.LocalDateTime" -> "getTimestamp";
            default -> throw new IllegalArgumentException("Unsupported field type: " + javaType);
        };
    }

        private Class mapJavaTypeToClass(String javaType) {
            return switch (javaType) {
                case "int", "java.lang.Integer" -> Integer.class;
                case "long", "java.lang.Long" -> Long.class;
                case "java.lang.String" -> String.class;
                case "boolean", "java.lang.Boolean" -> Boolean.class;
                case "java.util.Date", "java.time.LocalDate", "java.time.LocalDateTime" -> Date.class;
                default -> throw new IllegalArgumentException("Unsupported field type: " + javaType);
            };
    }


}
