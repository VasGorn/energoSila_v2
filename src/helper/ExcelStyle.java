package helper;

import org.apache.poi.ss.usermodel.*;

public class Excel {
    private Workbook workbook;
    public static Font headerFontGreen;
    public static Font headerFontBlack;

    public static CellStyle headerCellStyle;
    public static CellStyle centerCellStyle;
    public static CellStyle uCenterCellStyle;
    public static CellStyle boldText;

    public static CellStyle cellColorGreen;
    public static CellStyle cellColorGreenBold;


    public Excel(Workbook workbook) {
        this.workbook = workbook;

        // create a GREEN Font for styling header cells
        headerFontGreen = workbook.createFont();
        headerFontGreen.setBold(true);
        headerFontGreen.setFontHeightInPoints((short) 12);
        headerFontGreen.setColor(IndexedColors.GREEN.getIndex());

        // create a Black Font for styling header cells
        headerFontBlack = workbook.createFont();
        headerFontBlack.setBold(true);
        headerFontBlack.setFontHeightInPoints((short) 12);
        headerFontBlack.setColor(IndexedColors.BLACK.getIndex());

        // create a CellStyle with the font
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFontGreen);

        // Style for center alignment
        centerCellStyle =  workbook.createCellStyle();
        centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        centerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setAllBorder(centerCellStyle, BorderStyle.THIN);
        centerCellStyle.setFont(headerFontBlack);

        // Style for center alignment, but usual text
        uCenterCellStyle =  workbook.createCellStyle();
        uCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
        uCenterCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setAllBorder(uCenterCellStyle, BorderStyle.THIN);

        // Style for left alignment and bold text
        boldText =  workbook.createCellStyle();
        boldText.setAlignment(HorizontalAlignment.LEFT);
        boldText.setVerticalAlignment(VerticalAlignment.CENTER);
        boldText.setWrapText(true);
        setAllBorder(boldText, BorderStyle.THIN);
        boldText.setFont(headerFontBlack);

        cellColorGreen = workbook.createCellStyle();
        cellColorGreen.setAlignment(HorizontalAlignment.CENTER);
        cellColorGreen.setVerticalAlignment(VerticalAlignment.CENTER);
        cellColorGreen.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        cellColorGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setAllBorder(cellColorGreen, BorderStyle.THIN);

        cellColorGreenBold = workbook.createCellStyle();
        cellColorGreenBold.setAlignment(HorizontalAlignment.CENTER);
        cellColorGreenBold.setVerticalAlignment(VerticalAlignment.CENTER);
        cellColorGreenBold.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        cellColorGreenBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellColorGreenBold.setFont(headerFontBlack);
        setAllBorder(cellColorGreenBold, BorderStyle.THIN);
    }

    public void createCell(String value, Row row, int column, CellStyle cellStyle) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    public void createCell( int value, Row row, int column, CellStyle cellStyle) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private void setAllBorder(CellStyle cellStyle, BorderStyle borderStyle){
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        //return  cellStyle;
    }

    private void setStyleInRow(Row row, int maxNumCell,CellStyle cellStyle){
        for(int m = 0; m <= maxNumCell; ++m){
            Cell cell = row.createCell(m);
            cell.setCellStyle(cellStyle);
        }
    }

}
