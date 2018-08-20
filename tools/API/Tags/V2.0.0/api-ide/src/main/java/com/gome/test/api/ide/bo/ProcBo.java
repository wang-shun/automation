package com.gome.test.api.ide.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.io.InputStream;

public class ProcBo {

    public int run(String workDir, String outPath, String cmd)
            throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(cmd);
        String[] cmds = new String[tokenizer.countTokens()];
        for (int i = 0; i < cmds.length; ++i) {
            cmds[i] = tokenizer.nextToken();
        }
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.redirectErrorStream(true);
        pb.directory(new File(workDir));
        Process p = pb.start();
        InputStream is = p.getInputStream();
        Thread copier = new StreamCopyThread(is, outPath);
        copier.start();
        return p.waitFor();
    }

    private static class StreamCopyThread extends Thread {

        private final InputStream is;
        private final String outPath;

        public StreamCopyThread(InputStream is, String outPath) {
            this.is = is;
            this.outPath = outPath;
        }

        @Override
        public void run() {
            try {
                OutputStream os = null;
                try {
                    os = new FileOutputStream(new File(outPath));
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) >= 0) {
                        os.write(buf, 0, len);
                        os.flush();
                    }
                } finally {
                    if (null != is) {
                        is.close();
                    }
                    if (null != os) {
                        os.close();
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
}
