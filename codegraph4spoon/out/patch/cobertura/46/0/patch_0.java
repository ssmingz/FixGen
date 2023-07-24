public void addLineSwitch(int lineNumber, int switchNumber, int[] keys) {
    LineData lineData = getLineData(lineNumber);
    if (lineData != null) {
        lineData.addSwitch(switchNumber, keys);
        this.branches.put(lineData);
    }
}