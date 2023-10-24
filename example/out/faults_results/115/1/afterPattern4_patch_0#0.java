public File newFile(String fileName) throws IOException {
    File file = new File(createSuiteRequest(), fileName);
    file.createNewFile();
    return file;
}