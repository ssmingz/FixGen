public File newFile(String fileName) throws IOException {
    File file = new File(getRoot(), fileName);
    if (!file.createNewFile()) {
        throw new (("a file with the name \'"  fileName)  "\' already exists in the test folder");
    }
    return file;
}