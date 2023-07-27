class PlaceHold{
public static void saveGlobalProjectData() {
    globalProjectData = new ProjectData();
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
    File dataFile = CoverageDataFileHandler.getDefaultDataFile();
    FileLocker fileLocker = new FileLocker(dataFile);
    if (fileLocker.lock()) {
        if (datafileProjectData == null) {
            datafileProjectData = projectDataToSave;
        } else {
            datafileProjectData.merge(projectDataToSave);
        }
        CoverageDataFileHandler.saveCoverageData(datafileProjectData, dataFile);
    }
    fileLocker.release();
    globalProjectDataLock.lock();
    try {
        projectDataToSave = ;
         = ;
    } finally {
        .unlock();
    }
    ProjectData  = null;
}
}