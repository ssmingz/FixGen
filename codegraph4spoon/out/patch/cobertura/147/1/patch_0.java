private void dumpSourceFiles(PackageData packageData) {
    println("<classes>");
    increaseIndentation();
    Iterator it = iterator(getSourceFiles());
    while (it.hasNext()) {
        dumpClasses(((SourceFileData) (it.next())));
    } 
    decreaseIndentation();
    println("</classes>");
}