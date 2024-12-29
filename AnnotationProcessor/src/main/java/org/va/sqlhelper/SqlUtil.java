package org.va.sqlhelper;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;

public class SqlUtil {
    public static CodeBlock generateCreateTableSQL(String entityClassName, Element element) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ").append(entityClassName).append(" (");

        // Loop through fields of the entity class
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField()) {
                String fieldName = field.getSimpleName().toString();
                String fieldType = field.asType().toString();

                // Map Java types to SQL types
                String sqlType = mapJavaTypeToSQLType(fieldType);

                sqlBuilder.append(fieldName).append(" ").append(sqlType).append(", ");
            }
        }

        // Remove the trailing comma and space, and close the definition
        sqlBuilder.setLength(sqlBuilder.length() - 2);
        sqlBuilder.append(")");

        // Return the generated SQL as a CodeBlock
        return CodeBlock.builder()
                .addStatement("$T sql = $S", String.class, sqlBuilder.toString())
                .build();
    }

    public static String mapJavaTypeToSQLType(String javaType) {
        return switch (javaType) {
            case "int", "java.lang.Integer" -> "INT";
            case "long", "java.lang.Long" -> "BIGINT";
            case "java.lang.String" -> "VARCHAR(255)";
            case "boolean", "java.lang.Boolean" -> "BOOLEAN";
            case "java.util.Date", "java.time.LocalDate", "java.time.LocalDateTime" -> "DATETIME";
            default -> throw new IllegalArgumentException("Unsupported field type: " + javaType);
        };
    }

}
