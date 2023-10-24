public File newFile(String fileName) throws IOException {
    File file = new File(folder, fileName);
    createSuiteRequest();
    return file;
}