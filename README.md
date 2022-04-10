# JavaCodeMetricsCalculator

This is a project that calculates code metrics on Java Source Code (```.java``` files).

## Requirements
- [Apache Maven](https://maven.apache.org/) project management and comprehension tool.  

## Build
This is a maven project. To compile the project execute the following command:  
  ```mvn package```

## Run

### 1. Instructions for use via Command Line

Open a cmd window in the directory where MetricsCalculator.jar is located and run the following command:

```
java -jar MetricsCalculator.jar <project_root_absolute_path> <outputfile_absolute_path>
```

where project_root_absolute_path is the full path to the root folder of the project you want to parse (ie before the src folders)

Once the process is complete, the results should appear.

## Calculated metrics
The generated String contains 12 metrics from three different metrics suites (CSV format separated by tabs \t). The order of the extracted metrics along with the metrics suite that they belong to is presented in the following table. The quality metrics are calculated per Java Source file.

| Field | Metric Suite        | Metric | Description                                                  |
|-------|---------------------|--------|--------------------------------------------------------------|
| 1     | Chidamber & Kemerer | WMC    | Weighted methods per class (Number of methods)               |
| 2     | Chidamber & Kemerer | DIT    | Depth of inheritance tree                                    |
| 3     | Chidamber & Kemerer | NOCC   | Number of Class Children                                     |
| 4     | Chidamber & Kemerer | CBO    | Coupling between object classes (Coupling between every user-defined class, except its inner & nested classes). Coupling: Method invocation, inheritance, exception handling, method parameters, class field access,                               |
| 5     | Chidamber & Kemerer | RFC    | Response for class (WMC + Size of Response Set). The Response set for a class is defined by C&K as 'a set of methods that can potentially be executed in response to a message received by an object of that class               |
| 6     | Chidamber & Kemerer | LCOM   | Lack of cohesion in methods (LCOM1)                          |
| 7     | Li & Henry          | Complexity   | Weighted methods complexity (Avg Cyclomatic Complexity of Class (#SelectionStructures / #Methods))          |
| 8    | Li & Henry          | MPC    | Message-passing couple (Total Number of Methods Called)      |
| 9    | Li & Henry          | DAC    | Data abstraction coupling (Number of user-defined classes as class properties)                                   |
| 10    | Li & Henry          | SIZE1  | Lines of code (LOC)                                          |
| 11    | Li & Henry          | SIZE2  | Number of properties                                         |
| 12    | Bansyia             | Classes Number (DSC)    | Design size in classes (Number of classes in the design)     |
| 12    | -             | ClassNames    | The names of classes contained in the Java source file     |  
