public void execute() throws BuildException {
    initArgs();
    if (dataFile != null) {
        addArg("--datafile");
        addArg(dataFile);
    }
    if (toDir != null) {
        addArg("--destination");
        addArg(toDir.toString());
    }
    if (ignoreRegex != null) {
        addArg("--ignore");
        addArg(ignoreRegex.getRegex());
    }
    Set filenames = new HashSet();
    Iterator iter = fileSets.iterator();
    while (iter.hasNext()) {
        FileSet fileSet = ((FileSet) (iter.next()));
        addArg("--basedir");
        addArg(baseDir(fileSet));
        filenames.addAll(Arrays.asList(getFilenames(fileSet)));
    } 
    addFilenames(((String[]) (filenames.toArray(new String[filenames.size()]))));
    saveArgs();
    if (getJava().executeJava() != 0) {
        throw new BuildException(null);
    }
    unInitArgs();
}