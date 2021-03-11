package com.claritybot.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Manoj Hans
 **/
public class ReadExcelData {

    private static final Logger logger = LogManager.getLogger(ReadExcelData.class.getName());
    private XSSFSheet excelSheet;

    public Object[][] getTableArray(String FilePath, String SheetName) {
        Object[][] tabArray = null;
        try {
            var excelFile = new FileInputStream(FilePath);
            // Access the required test data sheet
            XSSFWorkbook excelBook = new XSSFWorkbook(excelFile);
            excelSheet = excelBook.getSheet(SheetName);
            int startRow = 1;
            int startCol = 0;
            int ci, cj;
            var totalRows = excelSheet.getLastRowNum();
            var totalCols = excelSheet.getRow(1).getLastCellNum();
            tabArray = new String[totalRows][totalCols];
            ci = 0;
            for (int i = startRow; i <= totalRows; i++, ci++) {
                cj = 0;
                for (int j = startCol; j < totalCols; j++, cj++) {
                    try {
                        tabArray[ci][cj] = getCellData(i, j);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.info("Could not find the Excel sheet ", e);
        } catch (IOException e) {
            logger.info("Could not read the Excel sheet", e);
        }
        return tabArray;
    }

    public Object getCellData(int rowNum, int colNum) {
        Object cellData = "";
        var cell = excelSheet.getRow(rowNum).getCell(colNum);
        var dataType = cell.getCellType();
        if (dataType == CellType.BLANK) {
            return cellData;
        } else if (dataType == CellType.NUMERIC) {
            cellData = cell.getRawValue();
        } else {
            cellData = cell.getStringCellValue();
        }
        return cellData;
    }
}
