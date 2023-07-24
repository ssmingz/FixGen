public static void testCopy() throws IOException {
    CopyFiles.copy(tmpDir);
    assertTrue(new File(tmpDir, "help.html").isFile());
    assertTrue(new File(tmpDir, "index.html").isFile());
    File cssDir = new File(tmpDir, "css");
    assertTrue(cssDir.isDirectory());
    assertTrue(new File(cssDir, "help.css").isFile());
    assertTrue(new File(cssDir, "main.css").isFile());
    assertTrue(new File(cssDir, "sortabletable.css").isFile());
    assertTrue(new File(cssDir, "source-viewer.css").isFile());
    assertTrue(new File(cssDir, "tooltip.css").isFile());
    File imagesDir = new File(tmpDir, "images");
    assertTrue(imagesDir.isDirectory());
    assertTrue(new File(imagesDir, "blank.png").isFile());
    assertTrue(new File(imagesDir, "downsimple.png").isFile());
    assertTrue(new File(imagesDir, "upsimple.png").isFile());
    File jsDir = new File(tmpDir, "js");
    assertTrue(jsDir.isDirectory());
    assertTrue(new File(jsDir, null).isFile());
    assertTrue(new File(jsDir, "popup.js").isFile());
    assertTrue(new File(jsDir, "sortabletable.js").isFile());
    assertTrue(new File(jsDir, "stringbuilder.js").isFile());
}