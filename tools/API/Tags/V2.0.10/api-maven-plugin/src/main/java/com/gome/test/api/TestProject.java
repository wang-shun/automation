package com.gome.test.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
import org.apache.maven.plugin.logging.Log;
import org.dom4j.DocumentException;

public class TestProject {

	private final String groupId;
	private final String artifactId;
	private final String version;

	private static final String parentGroupId="com.gome.test.api";
	private static final String parentArtifactId="api-helper-parent";
	private static final String parentVersion="RELEASE";
	
	public TestProject(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public void generateTo(File testProjectDir,Log logger) throws DocumentException,
			IOException {
		TestProjectArch arch = new TestProjectArch(
				testProjectDir.getAbsolutePath());
		// STEP1: Generate test project directory
		testProjectDir.delete();
		testProjectDir.mkdirs();
		// STEP2: Generate pom.xml
		String pomPath = arch.getPomPath();
		XmlDocument doc = null;
		if (new File(pomPath).exists()) {
			doc = new XmlDocument(pomPath);
		} else {
			doc = new XmlDocument(getClass()
					.getResourceAsStream("/pom.xml.tpl"));
		}
		// STEP2.1: Update groupId, artifactId and version in pom.xml
		Map<String, String> m = new HashMap<String, String>();
		m.put("/project/groupId", groupId);
		m.put("/project/artifactId", String.format("%sTests", artifactId));
		m.put("/project/version", version);
		doc.updateText(m);
		// STEP2.2: Update local jar dependencies in pom.xml
		doc.addParent(parentGroupId, parentArtifactId, parentVersion);
		doc.addDependency(groupId, artifactId, version);
		// STEP2.3: Dump to pom.xml
		doc.dumpTo(pomPath);
		// STEP3: Generate src/test/assembly/test-jar-with-dependencies
		new File(arch.getAssemblyPath()).mkdirs();
		doc = new XmlDocument(getClass().getResourceAsStream(
				"/test-jar-with-dependencies.xml"));
		doc.dumpTo(arch.getAssemblyFilePath());
		// STEP4: Generate src/test/java src/main/java
		new File(arch.getJavaPath()).mkdirs();
		new File(arch.getMainPath()).mkdirs();
		String main = String.format("%s/Main.java", arch.getMainPath());
        PluginUtils.copyResources(getClass(), "/Main.java.tpl", main);
		// STEP5: Generate src/test/resources/global.properties
		// src/test/resources/log4j.properties
		// src/test/resources/japi4etp.bat
		// src/test/resources/testng
		new File(arch.getResourcePath()).mkdirs();
		String testngPath = arch.getTestNGPath();
		PluginUtils.delete(new File(testngPath));
		new File(testngPath).mkdirs();

		// File globalPropertiesFile = new File(arch.getGlobalPropertiesPath());
		// if (!globalPropertiesFile.exists()) {
		// globalPropertiesFile.createNewFile();
		// }

		PluginUtils.copyResources(getClass(), "/log4j.properties", arch.getLog4jPropertiesPath());
		//copyResources("/japi4etp.bat", arch.getJapi4EtpBatPath());
	}
}
