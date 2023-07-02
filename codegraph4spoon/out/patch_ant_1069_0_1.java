protected void getFile(FTPClient ftp, String dir, String filename) throws IOException, BuildException {
    OutputStream outstream = null;
    try {
        File file = getProject().resolveFile(new File(dir, filename).getPath());
        if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
            return;
        }
        if (verbose) {
            log((("transferring " + filename) + " to ") + file.getAbsolutePath());
        }
        File pdir = file.getParentFile();
        if (!pdir.exists()) {
            pdir.mkdirs();
        }
        outstream = new BufferedOutputStream(new FileOutputStream(file));
        ftp.retrieveFile(resolveFile(filename), outstream);
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            String s = "could not get file: " + ftp.getReplyString();
            if (skipFailedTransfers) {
                log(s, MSG_WARN);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        } else {
            log((("File " + file.getAbsolutePath()) + " copied from ") + server, MSG_VERBOSE);
            transferred++;
            if (preserveLastModified) {
                outstream = null;
                FTPFile[] remote = ftp.listFiles(resolveFile(filename));
                if (remote.length > 0) {
                    FILE_UTILS.setFileLastModified(file, remote[0].getTimestamp().getTime().getTime());
                }
            }
        }
    } finally {
        FileUtils.close();
    }
}