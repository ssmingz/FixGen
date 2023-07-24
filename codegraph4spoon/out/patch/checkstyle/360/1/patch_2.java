private void checkTag(int lineNo, String[] comment, String tag, Pattern tagRE, Pattern formatRE, String format) {
    if (tagRE == null) {
        return;
    }
    int tagCount = 0;
    for (int i = 0; i < comment.length; i++) {
        final String s = comment[i];
        final Matcher matcher = tagRE.matcher(s);
        if (matcher.find()) {
            tagCount += 1;
            final int contentStart = matcher.start(1);
            final String content = s.substring(contentStart);
            if ((formatRE != null) && (!formatRE.matcher(content).find())) {
                log((lineNo + i) - comment.length, tag, format, MISSING_TAG);
            } else {
                logTag((lineNo + i) - comment.length, tag, content);
            }
        }
    }
    if (tagCount == 0) {
        log(lineNo, tag);
    }
}