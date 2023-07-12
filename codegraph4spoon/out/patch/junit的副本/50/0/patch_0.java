public static void savePreferences() throws IOException {
    FileOutputStream fos = new FileOutputStream(getPreferencesFile());
    try {
        save();
    } finally {
        fos.close();
    }
}