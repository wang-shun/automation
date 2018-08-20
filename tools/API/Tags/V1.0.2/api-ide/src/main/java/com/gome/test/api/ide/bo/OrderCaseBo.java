package com.gome.test.api.ide.bo;

import com.gome.test.api.ide.dao.OrderCases;
import com.gome.test.api.ide.dao.OrderUtils;
import com.gome.test.api.ide.model.OrderCase;
import com.gome.test.api.ide.model.OrderNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;


public class OrderCaseBo {

    @Value(value = "${tests.path}")
    private String testsPath;


    public String add(String node, OrderCase orderCase) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        OrderCases orderCases = new OrderCases(excelPath);
        orderCases.add(sheetName, orderCase);
        orderCases.dump();
        return String.format("%s#%s#%s", excel, sheetName, orderCase.getId());
    }
//
//    public void delete(String node) throws Exception {
//        String excel = node.split("#")[0];
//        String sheetName = node.split("#")[1];
//        String caseId = node.split("#")[2];
//        File excelPath = new File(String.format("%s/%s",
//                testsPath, excel));
//        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
//        workbook.delete(sheetName, caseId);
//        workbook.dump();
//    }

    public OrderCase query(String node) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        OrderCases orderCases = new OrderCases(excelPath);
        OrderCase orderCase = orderCases.query(sheetName, caseId);
        List<String> steps = orderCase.getSteps();
        ArrayList<OrderNode> orderNodes = new OrderUtils(testsPath).getOrderNodes();
        for (String step : steps) {
            for (OrderNode orderNode : orderNodes) {
                if (orderNode.getId().equals(step)) {
                    orderNode.setSelected("true");
                }

            }
        }
        orderCase.setOrderNodes(orderNodes);
        return orderCase;
    }

    public String update(String node, OrderCase orderCase) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        String newCaseId = orderCase.getId();
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        if (newCaseId.equals("")) {
            throw new Exception("用例编号不能为空");
        }
        orderCase.setId(caseId);
        OrderCases orderCases = new OrderCases(excelPath);
        String returnStr;
        if (caseId.equals(newCaseId)) {
            returnStr = String.format("%s#%s#%s", excel, sheetName, caseId);
            orderCases.update(sheetName, orderCase);
        } else {
            orderCases.update(sheetName, orderCase, newCaseId);
            returnStr = String.format("%s#%s#%s", excel, sheetName, newCaseId);
        }
        orderCases.dump();
        return returnStr;
    }

    public String updateCaseName(String node, OrderCase orderCase) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        orderCase.setId(caseId);
        OrderCases orderCases = new OrderCases(excelPath);
        orderCases.updateCaseName(sheetName, orderCase);
        orderCases.dump();
        return String.format("%s#%s#%s", excel, sheetName, orderCase.getId());
    }

    public void delete(String node) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        OrderCases orderCases = new OrderCases(excelPath);
        orderCases.delete(sheetName, caseId);
        orderCases.dump();
    }
}
