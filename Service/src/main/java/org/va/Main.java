package org.va;

import org.va.runner.RepositoryScenariosRunner;
import org.va.runner.SimpleAnnotationProcess;

import java.time.Duration;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        long pid = ProcessHandle.current().pid();
        System.out.println("Current PID: " + pid);
        new RepositoryScenariosRunner().run();
      /*
      simple annotation runner
      */
        // new SimpleAnnotationProcess().run();
    }
}