package org.va;

import org.va.processors.SimpleAnnotationProcess;

public class Main {
    public static void main(String[] args) {
        new SimpleAnnotationProcess("abc").run();
        new SimpleAnnotationProcess("123").run();
    }
}