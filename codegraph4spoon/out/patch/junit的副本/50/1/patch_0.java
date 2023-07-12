public static void savePreferences() throws IOException {
    FileOutputStream fos = new FileOutputStream(getPreferencesFile());
    try {
        store();
    } finally {
        fos.close();
    }
}