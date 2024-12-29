package org.va.model;

import org.va.annotations.IsNumber;

/**
 * A sample class demonstrating the use of the {@link IsNumber} annotation.
 * <p>
 * This class contains a single field, {@code sampleKey}, which is validated to
 * ensure it contains only numeric strings using the {@link IsNumber} annotation.
 * </p>
 */
public class SampleClass {
    @IsNumber
    private String sampleKey;

    public SampleClass(String sampleKey) {
        this.sampleKey = sampleKey;
    }

    public String getSampleKey() {
        return sampleKey;
    }
}
