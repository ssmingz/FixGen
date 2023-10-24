package config.pojo;

public class Option {
    public String description;
    public String pythonCmd;
    public String modelWorkPath;
    public String modelPath;
    public String jsonBefore;
    public String jsonAfter;
    public String codeGraphPath;
    public String patternGraphPath;

    public testOnDataset testOnDataset;
    public extractPattern extractPattern;
    public defectFaults defectFaults;

    public static class testOnDataset {
        public String datasetName;
        public String datasetPath;
        public String patchPath;

        @Override
        public String toString() {
            return "datasetName {\n" +
                    "\t\tdatasetPath= " + datasetPath + '\n' +
                    "\t\tpatchPath= " + patchPath + '\n' +
                    "\t}\n";
        }
    }

    public static class extractPattern {
        public String datasetName;
        public String dataPath;
        public String patternPath;

        @Override
        public String toString() {
            return "extractPattern {\n" +
                    "\t\tdatasetName= " + datasetName + '\n' +
                    "\t\tdataPath= " + dataPath + '\n' +
                    "\t\tpatternPath= " + patternPath + '\n' +
                    "\t}\n";
        }
    }

    public static class defectFaults {
        public String patternPath;
        public double threshold;
        public String projectPath;
        public String resultsPath;

        @Override
        public String toString() {
            return "defectFaults {\n" +
                    "\t\tpatternPath= " + patternPath + '\n' +
                    "\t\tthreshold= " + threshold + '\n' +
                    "\t\tprojectPath= " + projectPath + '\n' +
                    "\t\tresultsPath= " + resultsPath + '\n' +
                    "\t}\n";
        }
    }

    @Override
    public String toString() {
        return "Option {\n" +
                "description=" + description + '\n' +
                "pythonCmd=" + pythonCmd + '\n' +
                "modelWorkPath=" + modelWorkPath + '\n' +
                "modelPath=" + modelPath + '\n' +
                "jsonBefore=" + jsonBefore + '\n' +
                "jsonAfter=" + jsonAfter + '\n' +
                "codeGraphPath=" + codeGraphPath + '\n' +
                "patternGraphPath=" + patternGraphPath + '\n' +
                "\ttestOnDataset=" + testOnDataset +
                "\textractPattern=" + extractPattern +
                "\tdefectFaults=" + defectFaults +
                '}';
    }




}
