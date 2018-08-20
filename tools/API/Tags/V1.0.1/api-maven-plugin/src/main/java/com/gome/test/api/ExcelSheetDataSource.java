/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author shan.tan
 */
public interface ExcelSheetDataSource {

    public List<AtomicCase> parse(Sheet sheet, Workbook workbook, List<AtomicCase> testCases, GlobalSettings settings);
}
