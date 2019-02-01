package helper;

import model.FinalWorkReport;
import model.ServerDate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public class SheetTotalReport extends ExcelStyle {
    private Sheet sheet;
    //private String nameMonth;

    public SheetTotalReport(Workbook workbook, FinalWorkReport fWorkReport,
                            String nameMonth, int numMonth){
        super(workbook);
        this.sheet = workbook.createSheet("ИТОГ");

        createTableHeader(nameMonth, numMonth);
        createData(fWorkReport);
    }

    private void createTableHeader(String nameMonth, int numMonth){
        String[] tableHeader = new String[3];
        tableHeader[0] = "Ф.И.О."; tableHeader[1] = "Всего";
        tableHeader[2] = nameMonth;

        Row tableHeaderRow = sheet.createRow(5);

        sheet.setColumnWidth(0, 10000);

        int maxDaysInMonth = ServerDate.getMaxDaysInMonth(numMonth);

        setStyleInRow(tableHeaderRow, (3 + 2 * maxDaysInMonth - 1), centerCellStyle);

        createCell(tableHeader[0], tableHeaderRow, 0, centerCellStyle);
        createCell(tableHeader[1], tableHeaderRow, 1, centerCellStyle);
        createCell(tableHeader[2], tableHeaderRow, 3, centerCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                5,
                6,
                0,
                0
        ));

        sheet.addMergedRegion(new CellRangeAddress(
                5,
                5,
                1,
                2
        ));

        sheet.addMergedRegion(new CellRangeAddress(
                5,
                5,
                3,
                3 + 2 * maxDaysInMonth - 1
        ));

        // create row with number of days
        Row tableNumDaysRow = sheet.createRow(6);

        super.setStyleInRow(tableNumDaysRow, (3 + 2 * maxDaysInMonth - 1), centerCellStyle);

        int cellCount = 3;
        for(int i = 0; i < maxDaysInMonth; ++i){
            Cell cell1 = tableNumDaysRow.createCell(cellCount);
            cell1.setCellValue(i + 1);
            cell1.setCellStyle(centerCellStyle);

            Cell cell2 = tableHeaderRow.createCell(cellCount+1);
            cell2.setCellStyle(centerCellStyle);

            sheet.setColumnWidth(cellCount, 1000);
            sheet.setColumnWidth(cellCount + 1,1000);

            sheet.addMergedRegion(new CellRangeAddress(
                    6,
                    6,
                    cellCount,
                    cellCount + 1
            ));

            cellCount += 2;

        }

        createCell("Раб-та, ч.", tableNumDaysRow, 1, centerCellStyle);
        createCell("Перераб, ч.", tableNumDaysRow, 2, centerCellStyle);
        sheet.setColumnWidth(1, 3300);
        sheet.setColumnWidth(2, 3500);
    }

    private void createData(FinalWorkReport fWorkReport){
        List<FinalWorkReport.EmployeeWithWorkType> employeeList;

        employeeList = fWorkReport.getEmployeeList();

        // iterate through employee list
        int rowNum = 7;
        for(FinalWorkReport.EmployeeWithWorkType w: employeeList){
            Row rowEmployee = sheet.createRow(rowNum);

            // employee name
            createCell(w.getEmployee().toString(),rowEmployee, 0, boldText);

            // sum hours on employee
            createCell(w.getSumWorkEmployee()[FinalWorkReport.WORK_TIME], rowEmployee, 1, centerCellStyle);
            createCell(w.getSumWorkEmployee()[FinalWorkReport.OVER_WORK], rowEmployee, 2, centerCellStyle);

            List<int[]> sumOnDayList;
            sumOnDayList = w.getSumWorkOnDayList();

            // sum hours on day
            for(int[] array: sumOnDayList){
                int numDay = array[FinalWorkReport.NUM_DAY];
                int cellDay = numDay * 2 + 1;

                createCell(array[FinalWorkReport.WORK_TIME], rowEmployee, cellDay, uCenterCellStyle);
                createCell(array[FinalWorkReport.OVER_WORK], rowEmployee, cellDay+1, uCenterCellStyle);
            }

            List<FinalWorkReport.EmployeeWithWorkType.WorkTypeData> workTypeList = w.getWorkTypeDataList();

            // iterate through work type
            rowNum++;
            for(FinalWorkReport.EmployeeWithWorkType.WorkTypeData e: workTypeList){
                int groupStart = rowNum;
                Row rowWorkType = sheet.createRow(rowNum);
                rowWorkType.createCell(0).setCellValue(e.getWorkType().toString());

                // sum hours over month for employee
                createCell(e.getSumWorkType()[FinalWorkReport.WORK_TIME], rowWorkType, 1, uCenterCellStyle);
                createCell(e.getSumWorkType()[FinalWorkReport.OVER_WORK], rowWorkType, 2, uCenterCellStyle);

                List<int[]> workSchedule;
                workSchedule = e.getData();

                for(int[] work_array: workSchedule){
                    int numDay = work_array[FinalWorkReport.NUM_DAY];
                    int cellDay = numDay * 2 + 1;

                    createCell(work_array[FinalWorkReport.WORK_TIME], rowWorkType, cellDay, uCenterCellStyle);
                    createCell(work_array[FinalWorkReport.OVER_WORK], rowWorkType, cellDay+1, uCenterCellStyle);

                }
                sheet.groupRow(groupStart, rowNum);
                rowNum++;
            }
        }
    }

}
