package helper;

import model.FinalWorkReport;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExelTotalReport extends ExcelStyle {

    public ExelTotalReport(Workbook workbook, FinalWorkReport fWorkReport) {
        super(workbook);
        Sheet sheet = workbook.createSheet("Итог");
    }

    private void createTotalSheet(){

    }


}
