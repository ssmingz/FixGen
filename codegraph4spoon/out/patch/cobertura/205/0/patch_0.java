public void execute() throws BuildException {
    Header.print(System.out);
    getJava().createArg().setValue("--format");
    getJava().createArg().setValue(format);
    if (dataFile != null) {
        getJava().createArg().setValue("--datafile");
        getJava().createArg().setValue(dataFile);
    }
    getJava().createArg().setValue("--destination");
    getJava().createArg().setValue(destDir.toString());
    getJava().createArg().setValue("--source");
    getJava().createArg().setValue(src.toString());
    if (getJava().executeJava() != 0) {
        throw new BuildException(null);
    }
}