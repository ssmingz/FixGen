public void addLineSwitch(int lineNumber, int switchNumber, int min, int max) {
    LineData lineData = getLineData(lineNumber);
    if (lineData != null) {
        lineData.addSwitch(switchNumber, min, max);
        this.branches.put(lineData);
    }
}