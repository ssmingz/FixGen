public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please given the arguments");
            System.err.println("\ttest : test on datasets.");
            System.err.println("\textract : extract patterns.");
            System.err.println("\tdefect : defect faults on project.");
            System.exit(1);
        }

        switch (args[0]) {
            case "test":
                RunOnDataset.main(args);
                break;
            case "extract":
                ExtractPattern.main(args);
                break;
            case "defect":
                DefectFaults.main(args);
                break;
            default:
                System.err.println("Please given the arguments [test, extract, defect]");
        }
    }
}
