public static void savePreferences() throws IOException {
    FileOutputStream fos = new FileOutputStream(getPreferencesFile());
    try {
        getPreferences().store(fos, "");
    } finally {
        fos.close();
    }
}