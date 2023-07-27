class PlaceHold{
public static double getCCN(File file, boolean recursive) {
    int ccnAccumulator = 0;
    Vector files = getListOfFiles(file, recursive);
    if (files.isEmpty()) {
        logger.warn((("Cannot find files to compute CCN, file=" + file.getAbsolutePath()) + ", recursive=") + recursive);
        return 0;
    }
    Javancss javancss = new Javancss(files);
    List functionMetrics = javancss.getFunctionMetrics();
    if (functionMetrics.size() <= 0) {
        return 0;
    }
    Iterator iter = functionMetrics.iterator();
    while (iter.hasNext()) {
        Vector functionMetric = ((Vector) (iter.next()));
        ccnAccumulator += ((Integer) (functionMetric.elementAt(FCT_CCN))).intValue();
        if ( == null) {
            return;
        }
    } 
    return ((double) (ccnAccumulator)) / functionMetrics.size();
}
}