# FixGen
## Supported Features
- generate pattern with a pair of buggy and fixed code
- apply pattern on buggy code
- use patterns to defect potential faults

## How to use
### Configuration
- `option.yml` is the configuration file for FixGen
- dataset should be organized as the following structure
- each before.java or after.java should be a single class 
```
├─Project
   ├─GroupId
      └─CaseId
        ├─before.java
        └─after.java
```
For example: 
```
├─Ant
   ├─0
   │  ├─0
   │  │  ├─before.java
   │  │  └─after.java
   │  ├─1
   │  │  ├─before.java
   │  │  └─after.java
   │  └─2
   ├─1
   │  ├─0
   │  └─1

```
- with the changed method(only one) to build pattern.

For example:

before.java
```java
class PlaceHolder {
    public void method() {
        // before code
    }
}
```
after.java
```java
class PlaceHolder {
    public void method() {
        // after code
    }
}
```
### deep model configuration

the python scripy file is `fix_graph_1/run.py`, and three path parameters related to the model
input/output files are needed to be set in `run.py`.

more information about how to build environment, you can find it in `fix_graph_1/readme.md`.

### Test on Datasets

The code change in dataset should be as similar as possible.
We use caseA to generate patterns and apply them on caseB in one group.

To run FixGen on dataset, please add `--test` as the only argument.

we package this project as a jar file named `fixgen-maven-java.jar` in artifacts folder, 
and all its dependencies are packaged into `libs` folder.
to run this, you can use the following command:
```
    java -jar fixgen-maven-java.jar --test
```
and the result will be saved in result folder as you configure in `option.yml`.

### Extract Patterns
same as test on datasets, please add `--extract` as the only argument.
```
    java -jar fixgen-maven-java.jar --extract
```
### Defect Potential Faults on Projects

To avoid incorrect modification, please use patterns extracted from the same project
to defect potential faults.

To detect potential faults on projects using FixGen, 
Please add `--extract` to extract patterns from historical modification and save it to a directory.

Then add `--defect` to detect potential faults on projects using the extracted patterns.
```
    java -jar fixgen-maven-java.jar --defect
```

you can control the threshold of similarity between patterns and code changes 
by setting `threshold` in `option.yml`.

All the configurable file paths are in `resources/option.yml` in subModule `codegraph4spoon`.

### Example
the output of the project is in `example` folder
and `patterns` folder is the pattern library.




