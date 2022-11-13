package pattern;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import model.CodeGraph;
import utils.FileIO;

public class ActionGraph {
    private CodeGraph _src;
    private CodeGraph _dst;
    private Diff _diff;

    public ActionGraph(CodeGraph src, CodeGraph dst) {
        String srcpath = src.getFilePath();
        String dstpath = dst.getFilePath();
        _diff = new AstComparator().compare(FileIO.readStringFromFile(srcpath), FileIO.readStringFromFile(dstpath));
    }

}
