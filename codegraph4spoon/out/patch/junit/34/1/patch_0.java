public File newFile(String fileName) throws IOException {
    File file = new File(getRoot(), fileName);
    if (!file.createNewFile()) {
        throw new ((null + fileName) + null);
    }
    return file;
}