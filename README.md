# metrics_calculator

This is a project that calculates code metrics on Java Source Code (```.java``` files).

## Citing MetricsCalculator
If you use ```MetricsCalculator``` in your research or project, please consider citing it. Proper citation helps acknowledge the authors' contributions and allows others to locate the tool easily. You can cite MetricsCalculator as follows:

### Example Citation in LaTeX

```bibtex
@software{MetricsCalculator,
  author = {Dimitrios Zisis},
  title = {MetricsCalculator: A Tool for Calculating Software Metrics for Java Source Code},
  year = {2024},
  url = {https://github.com/dimizisis/metrics_calculator},
  version = {v0.0.1},
  license = {MIT},
  note = {Available at: https://github.com/dimizisis/metrics_calculator}
}
```

### Plain Text Citation
Alternatively, you can use this plain text citation format:

```
Zisis, Dimitrios.
MetricsCalculator: A Tool for Calculating Software Metrics for Java Source Code (v0.0.1).
Available at: https://github.com/dimizisis/metrics_calculator. 2024.
Licensed under MIT.
```

## Requirements
- [Apache Maven](https://maven.apache.org/) project management and comprehension tool.  

## Build
This is a maven project. To compile the project execute the following command:  
  ```mvn package```

## Run

### 1. Instructions for use via Command Line

Open a cmd window in the directory where ```MetricsCalculator.jar``` is located and run the following command:

```
java -jar MetricsCalculator.jar <project_root_absolute_path> <outfilename>.csv
```

where ```project_root_absolute_path``` and ```outfilename``` are the full path to the root folder of the project you want to parse (ie before the src folders) and the name of the CSV file in which the metric results will be stored, respectively (see also the screenshot below).

Alternatively, instead of specifying ```<outfilename>.csv```, you can use the keyword ```str``` to print the metrics directly to the console:

```
java -jar MetricsCalculator.jar <project_root_absolute_path> str
```

![alt_text](https://i.imgur.com/D4dIlmf.png)

If you want the results file to be saved in a specific folder, then you also enter the complete path to that folder and then ```<outfilename> .csv``` (eg ```C:/Users/results.csv```)

Once the process is complete, the results file should be created.

### 2. Instructions for use via GUI

Double-clicking on ```MetricsCalculator.jar``` should bring up the following window:

![alt_text](https://i.imgur.com/OOVfE6T.png)

where in the upper textbox should be placed (either with copy-paste, or with the Select button) the full path to the root folder of the project you want to analyze (before the ```src``` folders that is), while in the lower textbox the full path of the ```CSV``` file in which the metric results will be stored (the ```CSV``` filename is auto-generated: ```analysis_xxx.csv```).

Once the process is complete, the program will output a completion message (see screenshot below) and the results file should be created in the selected folder.

![alt_text](https://i.imgur.com/iDKLpCz.png)

#### Note 1: The UI function has not been completely tested yet, errors may occur.

## Calculated metrics
The generated comma separated values (```.csv```) file contains 27 (+1 extra) metrics from four different metrics suites. The order of the extracted metrics along with the metrics suite that they belong to is presented in the following table.  

| Field | Metric Suite        | Metric | Description                                                  |
|-------|---------------------|--------|--------------------------------------------------------------|
| 1     | Chidamber & Kemerer | WMC    | Weighted methods per class (Number of methods)               |
| 2     | Chidamber & Kemerer | DIT    | Depth of inheritance tree                                    |
| 3     | Chidamber & Kemerer | NOCC   | Number of Class Children                                     |
| 4     | Chidamber & Kemerer | CBO    | Coupling between object classes (Coupling between every user-defined class, except its inner & nested classes). Coupling: Method invocation, inheritance, exception handling, method parameters, class field access,                               |
| 5     | Chidamber & Kemerer | RFC    | Response for class (WMC + Size of Response Set). The Response set for a class is defined by C&K as 'a set of methods that can potentially be executed in response to a message received by an object of that class               |
| 6     | Chidamber & Kemerer | LCOM   | Lack of cohesion in methods (LCOM1)                          |
| 7     | Li & Henry          | WMC*   | Weighted methods complexity (Avg Cyclomatic Complexity of Class (#SelectionStructures / #Methods))          |
| 8    | Li & Henry          | NOM    | Number of methods                                            |
| 9    | Li & Henry          | MPC    | Message-passing couple (Total Number of Methods Called)      |
| 10    | Li & Henry          | DAC    | Data abstraction coupling (Number of user-defined classes as class properties)                                   |
| 11    | Li & Henry          | SIZE1  | Lines of code (LOC)                                          |
| 12    | Li & Henry          | SIZE2  | Number of properties                                         |
| 13    | Bansyia             | DSC    | Design size in classes (Number of classes in the design)     |
| 14    | Bansyia             | NOH    | Number of hierarchies (if NOCC > 0 && ANA == 0, NOH = 1)     |
| 15    | Bansyia             | ANA    | Average number of ancestors                                  |
| 16    | Bansyia             | DAM    | Data access metric ((#Total_Attributes - #Public_Attributes) / #Total_Attributes)                                           |
| 17    | Bansyia             | DCC    | Direct class coupling (Same as CBO)                          |
| 18    | Bansyia             | CAMC   | Cohesion among methods in class (The summation of number of different types of method parameters in every method divided by a multiplication of number of different method parameter types in whole class and number of methods)                              |
| 19    | Bansyia             | MOA    | Measure of aggregation (Count of the number of class fields whose types are user-defined classes)  |
| 20    | Bansyia             | MFA    | Measure of functional abstraction (The ratio of the number of methods inherited by a class to the total number of methods accessible by members in the class)                            |
| 21    | Bansyia             | NOP    | Number of polymorphic methods (Number of abstract methods)   |
| 22    | Bansyia             | CIS    | Class interface size (Number of Public Methods)              |
| 23    | Bansyia             | NPM    | Number of Public Methods (Same as CIS metric)                |
| 24    | QMOOD               | -      | Reusability (-0.25\*CBO + 0.25\*CAMC + 0.5\*NPM + 0.5\*DSC)        |
| 25    | QMOOD               | -      | Flexibility (+0.25\*DAN - 0.25\*CBO + 0.5\*MOA + 0.5\*NOP)                                                 |
| 26    | QMOOD               | -      | Understandability (-0.33\*ANA + 0.33\*DAM + 0.33\*CAMC - 0.33\*DCC - 0.33\*NOP - 0.33\*NOM - 0.33\*DSC)                           |
| 27    | QMOOD               | -      | Functionality (0.12\*CAMC + 0.22\*NOP + 0.22\*NPM + 0.22\*DSC + 0.22\*NOH)  |
| 28    | QMOOD               | -      | Extendability  (0.5\*ANA - 0.5\*DCC + 0.5\*MFA + 0.5\*NOP)      |
| 29    | QMOOD               | -      | Effectiveness (0.2\*ANA + 0.2\*DAM + 0.2\*MOA + 0.2\*MFA + 0.2\*NOP)                                               |
| 30    | Other               | FanIn  | Afferent coupling (referred as Ca  in the C&K metrics suite) |
