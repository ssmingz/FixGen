class PlaceHold{
public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if ((obj == null) || (!obj.getClass().equals(this.getClass()))) {
        return false;
    }
    PackageData packageData = ((PackageData) (obj));
    getBothLocks();
    try {
        return super.equals(obj) && this.name.equals(.name);
    } finally {
        lock.unlock();
        .lock.unlock();
    }
    return super.equals(obj) && this.name.equals(packageData.name);
}
}