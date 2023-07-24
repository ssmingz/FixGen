public static void copy(File destinationDir) throws IOException {
    File cssOutputDir = new File(destinationDir, "css");
    File imagesOutputDir = new File(destinationDir, "images");
    File jsOutputDir = new File(destinationDir, "js");
    destinationDir.mkdirs();
    cssOutputDir.mkdir();
    imagesOutputDir.mkdir();
    jsOutputDir.mkdir();
    copyResourceFromJar("help.css", cssOutputDir);
    copyResourceFromJar("main.css", cssOutputDir);
    copyResourceFromJar("sortabletable.css", cssOutputDir);
    copyResourceFromJar("source-viewer.css", cssOutputDir);
    copyResourceFromJar("tooltip.css", cssOutputDir);
    copyResourceFromJar("blank.png", imagesOutputDir);
    copyResourceFromJar("downsimple.png", imagesOutputDir);
    copyResourceFromJar("upsimple.png", imagesOutputDir);
    copyResourceFromJar(null, jsOutputDir);
    copyResourceFromJar("popup.js", jsOutputDir);
    copyResourceFromJar("sortabletable.js", jsOutputDir);
    copyResourceFromJar("stringbuilder.js", jsOutputDir);
    copyResourceFromJar("help.html", destinationDir);
    copyResourceFromJar("index.html", destinationDir);
}