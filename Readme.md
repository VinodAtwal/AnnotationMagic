# AnnotationMagic

Annotation Magic is a multi-module project designed to showcase the power of annotation processing. The project demonstrates multiple approaches to annotation processing, providing insights into the internal workings of tools like Lombok and Spring Boot.


## Features

- Annotation Processing CompileTime and Runtime
- Custom Code Generations
- Showcasing internal working of Standard Tool like lombok or springboot
- Developing Custom Java agent and its use cases.

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher

## Current Annotations
1. #### @Repository
   Implementation is based of compile time code generations with JavaPoet and demonstrating basic sql operation code gen.
2. #### @IsNumber
   This processing is done on Runtime processing of annotations used for Validations. 
3. #### @Id 
   This is just helper annotations.
4. #### @LogExecution
   DEV IN PROGRESS


## Installation

To install AnnotationMagic, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/VinodAtwal/AnnotationMagic.git
    ```
2. Navigate to the project directory:
    ```bash
    cd AnnotationMagic
    ```
3. Build the project using Maven:
    ```bash
    mvn clean install
    ```

## Usage

To start using AnnotationMagic, run the following command:

```bash
java -jar ./Service/target/Service-1.0-SNAPSHOT.jar