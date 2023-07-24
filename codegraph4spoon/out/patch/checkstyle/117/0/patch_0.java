@Test
public void testProperFileExtension() throws Exception {
    final DefaultConfiguration checkConfig = createCheckConfig(ConstantNameCheck.class);
    final String content = "public class Main { public static final int k = 5 + 4; }";
    final File file = temporaryFolder.newFile("file.java");
    final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    writer.write(content);
    final String[] expected1 = new String[]{ "1:45: Name 'k' must match pattern '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'." };
    verify(checkConfig, file.getPath(), expected1);
    try (  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.StandardCharsets.UTF_8))) {
        writer.write(content);
    }
}