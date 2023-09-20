# FixGen
## Supported Features
- generate pattern with a pair of buggy and fixed code
- apply pattern on buggy code
- use patterns to defect potential faults

## How to use
### Configuration
- `option.yml` is the configuration file for FixGen
- dataset should be organized as the following structure
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

### Test on Datasets

The code change in dataset should be as similar as possible.
We use caseA to generate patterns and apply them on caseB in one group.

### Defect Potential Faults on Projects

To avoid incorrect modification, please use patterns extracted from the same project
to defect potential faults.







