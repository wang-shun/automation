package com.gome.test.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.testng.xml.XmlSuite;

public class TestProjectGen {

	private final static String Globlexlsx = "Globle.xlsx";
	private final static String OrderCasexlsx = "OrderCase.xlsx";
	private Log logger;
 
	private String javaPath;
	private String resourcePath;
	private String testngPath;
	private String timeout;
	private GlobalSettings settings;
	private Set<String> skipPaths;
	private Map<String, String> classByMethod;
	private Map<String, String> descByMethod;
	private Map<String, List<String>> testSuitesByGroup;

	private static final String STATUS_SUCC = "SUCCESS";
	private static final String STATUS_FAIL = "FAILURE";

	private void genTestSource(File testCaseDir, String packageName,
			String methodPrefix) throws Exception {

		if (testCaseDir.isDirectory() && !testCaseDir.isHidden()) {
			for (File file : testCaseDir.listFiles()) {
				if (file.isHidden()
						|| skipPaths.contains(file.getAbsolutePath())) {
					continue;
				}

				String filename = file.getName();
				if (file.isDirectory()) {
					String newPackageName = String.format("%s.%s", packageName,
							filename);
					String newMethodPrefix = String.format("%s_%s",
							methodPrefix, filename);
					genTestSource(file, newPackageName, newMethodPrefix);
				} else if (filename.endsWith(".xlsx")) {
					filename = filename.substring(0, filename.lastIndexOf("."));
					if (!APIUtils.isClassFileName(filename)) {
						continue;
					}

					if (null != logger) {
						logger.info(String.format(
								"Generate test cases from %s",
								file.getAbsolutePath()));
					}

					// Generate test source
					String status = STATUS_SUCC;
					String className = APIUtils.getClassName(filename);
					String newMethodPrefix = String.format("%s_%s",
							methodPrefix, filename);
					try {
						AtomicCases cases = new AtomicCases(file, settings);
						TestSource source = new TestSource(javaPath,
								resourcePath, packageName, className,
								cases.getTestCases(), newMethodPrefix,
								testngPath, classByMethod, descByMethod,
								  timeout, testSuitesByGroup);
						source.generate();
					} catch (Exception ex) {
						status = STATUS_FAIL;
						throw ex;
					} finally {
						if (null != logger) {
							logger.info(String.format("Status: %s", status));
						}
					}
				}
			}
		}
	}

	private void updateCaseCategory(String caseCategoryPath) throws Exception {
		if (!new File(caseCategoryPath).exists()) {
			throw new Exception("No CaseCategroy.xlsx found in Lib");
		}

		InputStream is = null;
		Workbook workbook = null;
		Sheet sheet = null;
		Set<String> testCaseNames = new HashSet<String>();
		int id = 0;
		try {
			is = new FileInputStream(caseCategoryPath);
			workbook = new XSSFWorkbook(is);
			sheet = workbook.getSheet("BusinessName");
			id = sheet.getLastRowNum();
			for (int i = 2; i <= id; ++i) {
				testCaseNames.add(sheet.getRow(i).getCell(1)
						.getStringCellValue());
			}
		} finally {
			if (null != is) {
				is.close();
			}
		}

		OutputStream os = null;
		try {
			for (File f : new File(testngPath).listFiles()) {
				if (f.isFile() && f.getName().endsWith(".xml")) {
					String fname = f.getName();
					String cn = fname.substring("testng-".length(),
							fname.length() - ".xml".length());
					if (!testCaseNames.contains(cn)) {
						sheet.createRow(++id).createCell(1).setCellValue(cn);
					}
				}
			}
			os = new FileOutputStream(caseCategoryPath);
			workbook.write(os);
		} finally {
			if (null != os) {
				os.close();
			}
		}
	}

	public void genTestProject(String groupId, String artifactId,
			String version, File testProjectDir, File testCaseDir,
			String messageClient, String timeout, String caseCategoryPath,
			Log logger) throws Exception {
		this.logger = logger;
		this.testSuitesByGroup = new HashMap<String, List<String>>();
		this.timeout = timeout;
		this.classByMethod = new HashMap<String, String>();
		this.descByMethod = new HashMap<String, String>();
		TestProjectArch arch = new TestProjectArch(
				testProjectDir.getAbsolutePath());
 
		this.javaPath = arch.getJavaPath();
		this.resourcePath = arch.getResourcePath();
		this.testngPath = arch.getTestNGPath();
		this.skipPaths = new HashSet<String>();
		File globalSettingsExcel = new File(testCaseDir, Globlexlsx);
		if (globalSettingsExcel.isFile()) {
			this.skipPaths.add(globalSettingsExcel.getAbsolutePath());
		}
		File orderCaseExcel = new File(testCaseDir, OrderCasexlsx);
		if (orderCaseExcel.isFile()) {
			this.skipPaths.add(orderCaseExcel.getAbsolutePath());
		}
		/**
		 * STEP1: Generate test project architecture
		 */
		TestProject project = new TestProject(groupId, artifactId, version);
		project.generateTo(testProjectDir,logger);
		// STEP2: Load global settings' excel
		settings = new GlobalSettings();
		settings.loadFrom(globalSettingsExcel);
		// STEP3: dfs test case directory to generate test sources
		StringBuilder sb = new StringBuilder();
		sb.append(groupId);
		sb.append(".");
		sb.append(artifactId);
		String packageName = sb.toString();

		// Generate test sources
		TestSource.clearXml();
		genTestSource(testCaseDir, packageName, "test");
		TestSource.saveXmlFile(testngPath);
		// STEP4: Generate order case
		GenerateOrderCase( logger, orderCaseExcel);
		// STEP5: Update suiteXmlFiles in pom.xml
		UpdateSuiteXmlFilesInPom(arch);

		// STEP6: Update CaseCategory.xlsx
		if (null != caseCategoryPath) {
			updateCaseCategory(caseCategoryPath);
		}
	}

	private void GenerateOrderCase( Log logger,
			File orderCaseExcel) throws Exception {
		if (null != logger) {
			logger.info(String.format("Generate test cases from %s",
					orderCaseExcel.getAbsolutePath()));
		}

		String status = STATUS_SUCC;
		try {
			if (null != orderCaseExcel && orderCaseExcel.exists()) {
				OrderCases orderCases = new OrderCases(orderCaseExcel,
						testngPath, classByMethod, descByMethod,
						testSuitesByGroup);
				List<XmlSuite> xmlSuites = orderCases.getXmlSuites();
				for (int i = 0; i < xmlSuites.size(); ++i) {
					XmlSuite suite = xmlSuites.get(i);
					Writer writer = null;
					try {
						writer = new OutputStreamWriter(new FileOutputStream(
								suite.getFileName()), "UTF-8");
						writer.write(suite.toXml());
					} finally {
						if (null != writer) {
							writer.close();
						}
					}
				}
			}
		} catch (Exception ex) {
			status = STATUS_FAIL;
			throw ex;
		} finally {
			if (null != logger) {
				logger.info(String.format("Status: %s", status));
			}
		}
	}

	private void UpdateSuiteXmlFilesInPom(TestProjectArch arch)
			throws DocumentException, IOException {
		List<String> fileNames = new ArrayList<String>();
		for (File f : new File(testngPath).listFiles()) {
			if (f.isFile() && f.getName().endsWith(".xml")) {
				fileNames.add(String.format("testng/%s", f.getName()));
			}
		}
		String pomPath = arch.getPomPath();
		XmlDocument doc = new XmlDocument(pomPath);
		doc.updateSuiteXmlFiles(fileNames);
		doc.dumpTo(pomPath);
	}
}
