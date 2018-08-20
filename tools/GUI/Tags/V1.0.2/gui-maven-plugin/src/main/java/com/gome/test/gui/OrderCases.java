package com.gome.test.gui;

import com.gome.test.gui.graph.Digraph;
import com.gome.test.gui.graph.KahnTopological;
import com.gome.test.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class OrderCases {

	private List<XmlSuite> xmlSuites;
    private List<OrderCase> orderCaseList;

	public List<XmlSuite> getXmlSuites() {
		return xmlSuites;
	}

    /*
    * 取得该文件的orderCase
    * */
    public List<OrderCase> getOrderCaseList(){
        return  orderCaseList;
    }

	public OrderCases(String orderCaseExcelPath, String testngPath,
					  Map<String, String> classByMethod,
					  Map<String, String> descByMethod,
					  Map<String, List<String>> testSuitesByGroup) throws IOException {
		this(new File(orderCaseExcelPath), testngPath, classByMethod,
				descByMethod, testSuitesByGroup);
	}

	public OrderCases(File orderCaseExcelFile, String testngPath,
					  Map<String, String> classByMethod,
					  Map<String, String> descByMethod,
					  Map<String, List<String>> testSuitesByGroup) throws IOException {
		this(new FileInputStream(orderCaseExcelFile), testngPath,
				classByMethod, descByMethod, testSuitesByGroup);
	}

	public OrderCases(InputStream is, String testngPath,
					  Map<String, String> classByMethod,
					  Map<String, String> descByMethod,
					  Map<String, List<String>> testSuitesByGroup) throws IOException {
		xmlSuites = new ArrayList<XmlSuite>();
        orderCaseList=new ArrayList<OrderCase>();
		try {
			parse(is, testngPath, classByMethod, descByMethod,
					testSuitesByGroup);
		} finally {
			if (null != is) {
				is.close();
			}
		}
	}



	private void parse(InputStream is, String testngPath,
			Map<String, String> classByMethod,
			Map<String, String> descByMethod,
			Map<String, List<String>> testSuitesByGroup) throws IOException {
		Map<String, OrderCase> orderCases = new HashMap<String, OrderCase>();
		Digraph graph = new Digraph();
		Set<String> s1 = new HashSet<String>();
		Set<String> s2 = new HashSet<String>();

		Workbook workbook = new XSSFWorkbook(is);
		int sheetNum = workbook.getNumberOfSheets();
		for (int i = 0; i < sheetNum; ++i) {
			Sheet sheet = workbook.getSheetAt(i);
			String sheetName = sheet.getSheetName();
			// skip non-OrderList sheet
			if (!sheetName.startsWith("OrderList")) {
				continue;
			}

			Iterator<Row> rowIter = sheet.iterator();
			// skip headers
			if (!rowIter.hasNext()) {
				continue;
			}
			Row header = rowIter.next();
			List<String> headers = ExcelUtils.getHeadersFrom(header);
			int caseNameId = headers.indexOf("用例名称");

			// Traversal each row
			while (rowIter.hasNext()) {
				Row row = rowIter.next();
				if (row.getLastCellNum() < 4) {
					continue;
				}

				int t = 0;
				// get case id
				Cell cell = row.getCell(t++);
				if (null == cell) {
					continue;
				}
				String id = ExcelUtils.getCellValue(cell).toString();
				if ("".equals(id)) {
					continue;
				}

				// get case name
				String caseName = "Unknown";
				if (caseNameId == t) {
					cell = row.getCell(t++);
					if (null == cell) {
						continue;
					}
					caseName = ExcelUtils.getCellValue(cell).toString();
					if ("".equals(caseName)) {
						caseName = "UnKnown";
					}
				}

				// continue or skip after failure
				cell = row.getCell(t++);
				boolean continueAfterFailure = true;
				if (null != cell) {
					continueAfterFailure = Boolean.valueOf(ExcelUtils
							.getCellValue(cell).toString());
				}

				// get owner
				cell = row.getCell(t++);
				String owner = "Unknown";
				if (null != cell) {
					owner = ExcelUtils.getCellValue(cell).toString();
				}

				if (classByMethod.containsKey(id)) {
					throw new IllegalArgumentException(
							String.format(
									"Duplicated case '%s' found in class %s and order cases",
									id, classByMethod.get(id)));
				}

				graph.addNode(id);
				if (s1.contains(id)) {
					throw new IllegalArgumentException(String.format(
							"Duplicated case '%s' found in order cases", id));
				}
				s1.add(id);

				// get groups
				Set<String> groups = new HashSet<String>();
				groups.add("ALL");
				groups.add("OrderCase");
				PluginUtils.addGroup(testSuitesByGroup, groups, id);

				// get test steps
				List<String> steps = new ArrayList<String>();
				for (int j = t; j < row.getLastCellNum(); ++j) {
					cell = row.getCell(j);
					if (null == cell) {
						continue;
					}
					String step = ExcelUtils.getCellValue(cell).toString();
					if (null == step) {
						continue;
					}
					step = step.trim();
					if (step.isEmpty()) {
						continue;
					}
					if (!classByMethod.containsKey(step)) {
						graph.addEdge(step, id);
						s2.add(step);
					}
					steps.add(step);
				}

				OrderCase orderCase = new OrderCase(id, caseName,
						continueAfterFailure, owner, steps);
				orderCases.put(id, orderCase);
                this.orderCaseList.add(orderCase);
			}
		}

		for (String s : s2) {
			if (!s1.contains(s)) {
				throw new IllegalArgumentException(String.format(
						"Step '%s' isn't a test case in order case", s));
			}
		}

		List<String> ids = KahnTopological.sort(graph);
		for (String id : ids) {
			OrderCase orderCase = orderCases.get(id);
			orderCase.expandSteps(orderCases, descByMethod);
		}

		for (String id : orderCases.keySet()) {
			OrderCase orderCase = orderCases.get(id);
			XmlSuite suite = orderCase.toXmlSuite(testngPath, classByMethod);
			xmlSuites.add(suite);
		}
	}
}
