@Override
public DetailNode[] getChildren() {
    if (children == null) {
        return null;
    } else {
        return Arrays.copyOf(children, length);
    }
}