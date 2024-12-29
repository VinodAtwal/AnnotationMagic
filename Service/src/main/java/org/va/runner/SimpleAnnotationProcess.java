package org.va.runner;

import org.va.annotations.IsNumber;
import org.va.model.SampleClass;

import java.lang.reflect.Field;
import java.util.regex.Pattern;


public class SimpleAnnotationProcess implements Runnable{

    private final String testData;

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public SimpleAnnotationProcess(String testData) {
        this.testData = testData;
    }

    @Override
    public void run() {
        SampleClass sampleClass = new SampleClass(testData);
        try {
            validate(sampleClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


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

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
