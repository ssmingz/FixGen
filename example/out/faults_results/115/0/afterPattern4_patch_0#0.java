public File newFolder(String folderName) {
    File file = new File(createSuiteRequest(), folderName);
    file.mkdir();
    return file;
}