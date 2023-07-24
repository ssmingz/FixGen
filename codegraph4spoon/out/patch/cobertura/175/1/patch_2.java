public static void saveGlobalProjectData() {
    TouchCollector.applyTouchesOnProjectData(projectDataToSave);
    File dataFile = CoverageDataFileHandler.getDefaultDataFile();
    synchronized(dataFile.getPath().intern()) {
        FileLocker fileLocker = new FileLocker(dataFile);
        try {
            if (fileLocker.lock()) {
                if (datafileProjectData == null) {
                    datafileProjectData = projectDataToSave;
                } else {
                    datafileProjectData.merge(projectDataToSave);
                }
                CoverageDataFileHandler.saveCoverageData(datafileProjectData, dataFile);
            }
        } finally {
            fileLocker.release();
        }
    }
    try {
        projectDataToSave = ;
         = ;
    } finally {
        .unlock();
    }
    globalProjectDataLock.lock();
    ProjectData  = null;
}