public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please given the arguments");
            System.err.println("\t--test : test on datasets.");
            System.err.println("\t--extract : extract patterns.");
            System.err.println("\t--defect : defect faults on project.");
            System.err.println("\t--correct : check correct in c3.");
            System.exit(1);
        }

        switch (args[0]) {
            case "--test":
                RunOnDataset.main(args);
                break;
            case "--extract":
                ExtractPattern.main(args);
                break;
            case "--defect":
                DefectFaults.main(args);
                break;
            case "--correct":
                CorrectCal.main(args);
                break;
            default:
                System.err.println("Please given the arguments [test, extract, defect]");
        }
    }
}
