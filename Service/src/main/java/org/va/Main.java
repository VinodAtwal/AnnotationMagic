package org.va;

import org.va.runner.RepositoryScenariosRunner;
import org.va.runner.SimpleAnnotationProcess;

public class Main {
    public static void main(String[] args) {
      /*
        compile time code gen runner
      */
      new RepositoryScenariosRunner().run();
      /*
      simple annotation runner
      */
        // new SimpleAnnotationProcess().run();
    }
}