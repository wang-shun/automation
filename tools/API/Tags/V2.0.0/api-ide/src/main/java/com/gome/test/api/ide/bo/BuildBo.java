package com.gome.test.api.ide.bo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

public class BuildBo {

    @Value(value = "${build.dir}")
    private String buildDir;

    private AtomicLong uid;

    public void initLoad() throws IOException {
        uid = new AtomicLong(0);
        FileUtils.deleteQuietly(new File(buildDir));
        new File(buildDir).mkdirs();
    }

    public String getConsoleLog(long buildId) throws Exception {
        return String.format("%s/%d/build.log", buildDir, buildId);
    }

    public String getNextBuildLog() {
        String nextBuildDir = getNextBuildDir();
        new File(nextBuildDir).mkdirs();
        return String.format("%s/build.log", nextBuildDir);
    }

    public String getNextBuildDir() {
        return String.format("%s/%d", buildDir, getNextBuildId());
    }

    private synchronized long getNextBuildId() {
        return uid.incrementAndGet();
    }
}
