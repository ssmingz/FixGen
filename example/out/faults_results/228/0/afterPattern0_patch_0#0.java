private String nameFor(String namePattern, int index, Object[] parameters) {
    String finalPattern = namePattern.replaceAll("\\{index\\}", currentNanoTime());
    return MessageFormat.format(finalPattern, parameters);
}