public File newFolder(String folderName) {
    File file = new File(folder, folderName);
    createSuiteRequest();
    return file;
}