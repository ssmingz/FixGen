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
        public String dataPath;

        @Override
        public String toString() {
            return "extractPattern {\n" +
                    "\t\tdataPath= " + dataPath + '\n' +
                    "\t}\n";
        }
    }

    public static class defectFaults {
        public String projectPath;

        @Override
        public String toString() {
            return "defectFaults {\n" +
                    "\t\tprojectPath= " + projectPath + '\n' +
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
