/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api;

import java.util.List;

import com.gome.test.api.model.ClientEnum;
import com.gome.test.plugin.GlobalSettings;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelSheetDataSource {

    public ClientEnum parse(Sheet sheet, Workbook workbook, List<AtomicCase> testCases, GlobalSettings settings);
}
