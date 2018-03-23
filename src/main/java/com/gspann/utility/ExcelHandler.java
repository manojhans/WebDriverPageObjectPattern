package com.gspann.utility;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import java.io.File;
import java.util.HashMap;

/**
 * @author Manoj Hans
 **/
public class ExcelHandler {

    private HashMap<String, Sheet> importedSheets = new HashMap<String, Sheet>();
    private Sheet selectedSheet = null;

    public ExcelHandler(File excelWorkbook) throws Exception {
        Workbook workbook = Workbook.getWorkbook(excelWorkbook);
        String[] sheetNames = workbook.getSheetNames();
        Sheet[] sheetData = workbook.getSheets();
        for (int sheetNumber = 0; sheetNumber < sheetData.length; sheetNumber++) {
            this.importedSheets.put(sheetNames[sheetNumber], sheetData[sheetNumber]);
        }
    }

    public void selectSheet(String sheetName) throws Exception {
        if (this.importedSheets.containsKey(sheetName)) {
            this.selectedSheet = importedSheets.get(sheetName);
        } else {
            throw new Exception("Sheet with name '" + sheetName + "' doesn't exist!");
        }
    }
   
    public HashMap<Integer, Cell> getColumn(int colNumber) throws Exception {
        return getColumn(colNumber, false);
    }

    public HashMap<Integer, Cell> getColumn(int colNumber, boolean skipFirstRow) throws Exception {
        if (this.selectedSheet.equals(null)) {
            throw new Exception("Please select a sheet before trying to get data!");
        } else if (colNumber > this.selectedSheet.getColumns()) {
            throw new Exception("There are only " + this.selectedSheet.getColumns() + " columns in this sheet.  please specify the correct column " + colNumber + "!");
        }
        HashMap<Integer, Cell> selectedColumn = new HashMap<Integer, Cell>();
        for (Cell currentCell : this.selectedSheet.getColumn(colNumber - 1)) {
            selectedColumn.put(selectedColumn.size() + 1, currentCell);
        }
        if (skipFirstRow) {
            selectedColumn.remove(1);
        }
        return selectedColumn;
    }
 
    public HashMap<Integer, Cell> getRow(int rowNumber) throws Exception {
        return getRow(rowNumber, false);
    }

    public HashMap<Integer, Cell> getRow(int rowNumber, boolean skipFirstColumn) throws Exception {
        if (this.selectedSheet.equals(null)) {
            throw new Exception("No sheet selected.  You must select a sheet before trying to get data!");
        } else if (rowNumber > this.selectedSheet.getRows()) {
            throw new Exception("There are only " + this.selectedSheet.getRows() + " rows in this sheet.  please specify the correct row " + rowNumber + "!");
        }
        HashMap<Integer, Cell> selectedRow = new HashMap<Integer, Cell>();
        for (Cell currentCell : this.selectedSheet.getRow(rowNumber - 1)) {
            selectedRow.put(selectedRow.size() + 1, currentCell);
        }
        if (skipFirstColumn) {
            selectedRow.remove(1);
        }
        return selectedRow;
    }
    
    public Cell getCellData(int column, int row) throws Exception {
        column--;
        row--;
        if (this.selectedSheet.equals(null)) {
            throw new Exception("Please select a sheet before trying to get data!");
        }
        return this.selectedSheet.getCell(column, row);
    }
}