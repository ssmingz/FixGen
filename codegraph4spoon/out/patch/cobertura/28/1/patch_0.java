class PlaceHold{
public void merge(CoverageData coverageData) {
    ProjectData projectData = ((ProjectData) (coverageData));
    getBothLocks(projectData);
    try {
        super.merge(coverageData);
        for (Iterator iter = projectData.classes.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            if (!this.classes.containsKey(key)) {
                this.classes.put(key, projectData.classes.get(key));
            }
            if ( == null) {
                return;
            }
        }
    } finally {
        lock.unlock();
        projectData.lock.unlock();
    }
}
}