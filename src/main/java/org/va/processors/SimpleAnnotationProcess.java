package org.va.processors;

import org.va.annotations.IsNumber;
import org.va.model.SampleClass;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * A class that demonstrates annotation processing to validate that a field contains only numeric strings.
 * Implements {@link Runnable} to allow execution in a separate thread.
 */
public class SimpleAnnotationProcess implements Runnable{

    /**
     * A pattern to match numeric strings (both integer and floating-point numbers).
     */
    private final String testData;

    /**
     * The test data to be validated.
     */
    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public SimpleAnnotationProcess(String testData) {
        this.testData = testData;
    }

    /**
     * Executes the annotation processing in a separate thread.
     * This method creates a {@link SampleClass} object and validates its fields.
     * If a field annotated with {@link IsNumber} does not contain a valid numeric string,
     * an error message is printed.
     */
    @Override
    public void run() {
        SampleClass sampleClass = new SampleClass(testData);
        try {
            validate(sampleClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates an object by inspecting its fields for the {@link IsNumber} annotation.
     * If a field is annotated with {@code @IsNumber} and its value is not numeric or is {@code null},
     * a validation error message is printed.
     *
     * @param obj the object to be validated.
     * @throws IllegalAccessException if a field is not accessible via reflection.
     */
    public void validate(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass(); // Get the class of the object
        for (Field field : clazz.getDeclaredFields()) { // Iterate over fields
            if (field.isAnnotationPresent(IsNumber.class)) { // Check if @NotNull is present
                field.setAccessible(true); // Allow access to private fields
                Object value = field.get(obj); // Get the field value
                if (value == null || !isNumeric((String) value) ) {
                    IsNumber annotation = field.getAnnotation(IsNumber.class);
                    System.out.println("Validation error with Field "+ field.getName()+ ": "+ annotation.message());
                }

            }
        }
    }

    /**
     * Checks if a given string represents a valid numeric value.
     * This method uses a regular expression to validate both integer and floating-point numbers.
     *
     * @param strNum the string to be validated.
     * @return {@code true} if the string is numeric, {@code false} otherwise.
     */
    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
