class PlaceHold{
public static void savePreferences() throws IOException {
    FileOutputStream fos = new FileOutputStream(getPreferencesFile());
    try {
        getPreferences().(fos, null);
    } finally {
        fos.close();
    }
}
}