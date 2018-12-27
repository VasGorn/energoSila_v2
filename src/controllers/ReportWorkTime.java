package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Employee;
import model.Order;
import model.ServerDate;
import model.WorkList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;

public class ReportWorkTime {
    @FXML
    private ComboBox<Employee> cbManager;

    @FXML
    private ComboBox<String> cbMonth;

    @FXML
    private ComboBox<Order> cbOrder;

    @FXML
    private TextArea txtDescribe;

    @FXML
    private TextArea txtAddress;

    @FXML
    private TextField txtFileName;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private VBox vBox;

    @FXML
    private Button btnCreate;

    @FXML
    private void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                activateProgressIndicator(true);

                // 1 is the manager
                String jsonStr = HttpHandler.getEmployeesWithPosition("1");

                System.out.println(jsonStr);

                if(jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {
                        JSONArray employeesJSON = jsonObj.getJSONArray("employees");

                        ArrayList<Employee> employeesList = getArrayListOfEmployee(employeesJSON);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                cbManager.getItems().clear();

                                for(Employee e: employeesList)
                                    cbManager.getItems().add(e);

                                cbManager.setPromptText("...");
                            }
                        });

                    } else {
                        Massage.showDataNotFound();
                    }
                } else {
                    Massage.showNetworkError();
                }

                activateProgressIndicator(false);
            }
        }).start();

        setItemsToComboBoxMonth();

        cbManager.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setActiveOrders();
            }
        });

        cbMonth.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setActiveOrders();
            }
        });

        cbOrder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setOrderInformation();
                setReportName();
            }
        });

        btnCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int numMonth = getNumMonth();
                int orderID = cbOrder.getValue().getId();
                new Thread(new GetWorkDataToReport(orderID, numMonth)).start();
            }
        });

    }

    //--------------------------------------------------------------------------
    //-----------------------------PRIVATE FUNCTION-----------------------------
    private void setItemsToComboBoxMonth(){
        cbMonth.getItems().add("Январь");
        cbMonth.getItems().add("Февраль");
        cbMonth.getItems().add("Март");
        cbMonth.getItems().add("Апрель");
        cbMonth.getItems().add("Май");
        cbMonth.getItems().add("Июнь");
        cbMonth.getItems().add("Июль");
        cbMonth.getItems().add("Август");
        cbMonth.getItems().add("Сентябрь");
        cbMonth.getItems().add("Окрябрь");
        cbMonth.getItems().add("Ноябрь");
        cbMonth.getItems().add("Декабрь");
    }

    //--------------------------------------------------------------------------
    private void activateProgressIndicator(boolean isActive){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(isActive){
                    vBox.setDisable(true);
                    progressIndicator.setVisible(true);
                }else{
                    vBox.setDisable(false);
                    progressIndicator.setVisible(false);
                }
            }
        });
    }

    //--------------------------------------------------------------------------
    private ArrayList<Employee> getArrayListOfEmployee(JSONArray employeeInJSON){
        ArrayList<Employee> employeesList = new ArrayList<>();
        for (int i = 0; i < employeeInJSON.length(); i++) {
            JSONObject o = employeeInJSON.getJSONObject(i);

            int userID = o.getInt(Const.EMPLOYEE_ID);
            String lastName = o.getString(Const.EMPLOYEE_LASTNAME);
            String firstName = o.getString(Const.EMPLOYEE_FIRSTNAME);
            String middleName = o.getString(Const.EMPLOYEE_MIDDLENAME);

            Employee employee = new Employee(lastName,firstName,middleName,userID);
            employeesList.add(employee);
        }
        return employeesList;
    }

    //--------------------------------------------------------------------------
    private int getNumMonth(){
        if(cbMonth.getValue() == null){
            return 0;
        }

        String sMonth = cbMonth.getValue();

        if(sMonth.equals("Январь"))
            return 1;
        else if(sMonth.equals("Февраль"))
            return 2;
        else if(sMonth.equals("Март"))
            return 3;
        else if(sMonth.equals("Апрель"))
            return 4;
        else if(sMonth.equals("Май"))
            return 5;
        else if(sMonth.equals("Июнь"))
            return 6;
        else if(sMonth.equals("Июль"))
            return 7;
        else if(sMonth.equals("Август"))
            return 8;
        else if(sMonth.equals("Сентябрь"))
            return 9;
        else if(sMonth.equals("Окрябрь"))
            return 10;
        else if(sMonth.equals("Ноябрь"))
            return 11;
        else if(sMonth.equals("Декабрь"))
            return 12;
        else
            return 0;
    }

    //--------------------------------------------------------------------------
    private void setActiveOrders(){
        Employee employee = cbManager.getValue();
        int numMonth = getNumMonth();

        if(employee != null && numMonth != 0){
            int managerID = employee.getID();
            new Thread(new GetActiveOrders(managerID, numMonth)).start();
        }

    }

    //--------------------------------------------------------------------------
    private void setOrderInformation(){
        Order order = cbOrder.getValue();

        if(order != null){
            String describe = order.getDescription();
            String address = order.getAddress();

            txtDescribe.setText(describe);
            txtAddress.setText(address);
        }
    }

    //--------------------------------------------------------------------------
    private void setReportName(){
        txtFileName.clear();

        Order order = cbOrder.getValue();
        Employee employee = cbManager.getValue();

        if(order != null && employee != null) {

            String orderName = order.getNameOrder();
            String managerName = employee.getLastName();
            String monthName = cbMonth.getValue();

            String reportName = "отчёт_" + managerName + "_" + orderName + "_" + monthName;

            txtFileName.setText(reportName);
        }
    }


    //--------------------------------------------------------------------------
    //---------------------------PRIVATE CLASSES--------------------------------
    private class GetActiveOrders implements Runnable{
        private String managerID;
        private String numMonth;

        private GetActiveOrders(int managerID, int numMonth){
            this.managerID = String.valueOf(managerID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            //String managerID = String.valueOf(User.getId());
            String jsonStr = HttpHandler.getActiveOrdersForManager(managerID, numMonth);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    JSONArray ordersJSON = jsonObj.getJSONArray("orders");
                    ArrayList<Order> ordersList = getOrdersFromJson(ordersJSON);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Order allOrder = new Order(0,"ВСЁ",
                                    "*******","*******",
                                    "*******", 0);

                            cbOrder.getItems().clear();

                            cbOrder.getItems().add(allOrder);

                            for(Order o: ordersList)
                                cbOrder.getItems().add(o);

                            cbOrder.setPromptText("...");
                        }
                    });

                } else {
                    Massage.showDataNotFound();
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }

        private ArrayList<Order> getOrdersFromJson(JSONArray ordersJSON){
            ArrayList<Order> ordersList = new ArrayList<>();
            for (int i = 0; i < ordersJSON.length(); i++) {
                JSONObject o = ordersJSON.getJSONObject(i);

                int orderID = o.getInt(Const.ORDER_ID);
                String nameOrder = o.getString(Const.ORDER_NAME);
                String adress = o.getString(Const.ORDER_ADRESS);
                String description = o.getString(Const.ORDER_DERSCRIPTION);
                int maxHours = o.getInt(Const.ORDER_MAX_HOURS);

                Employee manager = cbManager.getValue();

                Order order = new Order(orderID, nameOrder,adress, description,
                        manager.getName(), maxHours);
                ordersList.add(order);
            }
            return  ordersList;
        }
    }

    private class GetWorkDataToReport implements Runnable{
        private String orderID;
        private String numMonth;

        private GetWorkDataToReport(int orderID, int numMonth){
            this.orderID = String.valueOf(orderID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            //String managerID = String.valueOf(User.getId());
            String jsonStr = HttpHandler.getDataToReportWork(orderID, numMonth);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    JSONArray workListJSON = jsonObj.getJSONArray("workByType");

                    Order order = cbOrder.getValue();
                    WorkList workList = new WorkList(order, workListJSON);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(workList.getOrder().toString());
                            ArrayList<WorkList.WorkTypeWithEmployees> workTypeList;

                            workTypeList = workList.getWorkTypeList();

                            for(WorkList.WorkTypeWithEmployees w: workTypeList){
                                System.out.println(w.getWorkType().toString());
                                ArrayList<WorkList.WorkTypeWithEmployees.EmployeeWork> employeeList;
                                employeeList = w.getEmployeeList();
                                for(WorkList.WorkTypeWithEmployees.EmployeeWork e: employeeList){
                                    System.out.println(e.getEmployee().toString());
                                }
                            }

                            createExcel(workList);

                        }
                    });

                } else {
                    Massage.showDataNotFound();
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }

        private void createExcel(WorkList workList){
            Order order = cbOrder.getValue();

            // create workbook
            Workbook workbook = new HSSFWorkbook();

            /* CreationHelper helps us create instances of various things like DataFormat,
            Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
            CreationHelper createHelper = workbook.getCreationHelper();

            // create sheet
            Sheet sheet = workbook.createSheet(order.getNameOrder());

            // create a GREEN Font for styling header cells
            Font headerFontGreen = workbook.createFont();
            headerFontGreen.setBold(true);
            headerFontGreen.setFontHeightInPoints((short) 12);
            headerFontGreen.setColor(IndexedColors.GREEN.getIndex());

            // create a Black Font for styling header cells
            Font headerFontBlack = workbook.createFont();
            headerFontBlack.setBold(true);
            headerFontBlack.setFontHeightInPoints((short) 12);
            headerFontBlack.setColor(IndexedColors.BLACK.getIndex());

            // create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFontGreen);

            //-----------------------------------------------------------------
            // Style for center alignment
            CellStyle centerCellStyle =  workbook.createCellStyle();
            centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            centerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setAllBorder(centerCellStyle, BorderStyle.THIN);
            centerCellStyle.setFont(headerFontBlack);

            //-----------------------------------------------------------------
            // Style for center alignment, but usual text
            CellStyle uCenterCellStyle =  workbook.createCellStyle();
            uCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
            uCenterCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setAllBorder(uCenterCellStyle, BorderStyle.THIN);

            //-----------------------------------------------------------------
            // Style for left alignment and bold text
            CellStyle boldText =  workbook.createCellStyle();
            boldText.setAlignment(HorizontalAlignment.LEFT);
            boldText.setVerticalAlignment(VerticalAlignment.CENTER);
            boldText.setWrapText(true);
            setAllBorder(boldText, BorderStyle.THIN);
            boldText.setFont(headerFontBlack);

            sheet.setColumnWidth(0, 10000);
            //-----------------------------------------------------------------
            // ORDER INFORMATION
            createOrderInformation(sheet, order, headerCellStyle);

            //-----------------------------------------------------------------
            // TABLE HEADER
            createTableHeader(sheet, centerCellStyle);

            //-----------------------------------------------------------------
            // DATA TABLE
            CellStyle cellColorGreen = workbook.createCellStyle();
            cellColorGreen.setAlignment(HorizontalAlignment.CENTER);
            cellColorGreen.setVerticalAlignment(VerticalAlignment.CENTER);
            cellColorGreen.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            cellColorGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setAllBorder(cellColorGreen, BorderStyle.THIN);

            CellStyle cellColorGreenBold = workbook.createCellStyle();
            cellColorGreenBold.setAlignment(HorizontalAlignment.CENTER);
            cellColorGreenBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellColorGreenBold.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            cellColorGreenBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellColorGreenBold.setFont(headerFontBlack);
            setAllBorder(cellColorGreenBold, BorderStyle.THIN);

            ArrayList<WorkList.WorkTypeWithEmployees> workTypeList;

            workTypeList = workList.getWorkTypeList();

            // iterate through work type list
            int rowNum = 7;
            for(WorkList.WorkTypeWithEmployees w: workTypeList){
                Row rowWorkType = sheet.createRow(rowNum);

                // work type name
                createCell(w.getWorkType().toString(),rowWorkType, 0, boldText);

                // sum hours on work type
                if(w.getIsSumApprove()) {
                    createCell(w.getSumWorkType(), rowWorkType, 1, cellColorGreenBold);
                    createCell(w.getSumOverWork(), rowWorkType, 2, cellColorGreenBold);
                } else {
                    createCell(w.getSumWorkType(), rowWorkType, 1, centerCellStyle);
                    createCell(w.getSumOverWork(), rowWorkType, 2, centerCellStyle);
                }


                ArrayList<int[]> sumTypeList;
                sumTypeList = w.getHoursTypeList();

                // work type hours
                for(int[] array: sumTypeList){
                    int numDay = array[WorkList.NUM_DAY];
                    int cellDay = numDay * 2 + 1;
                    int workApprove = array[WorkList.APPROV];

                    if(workApprove > 0) {
                        createCell(array[WorkList.WORK_TIME], rowWorkType, cellDay, cellColorGreen);
                        createCell(array[WorkList.OVER_WORK], rowWorkType, cellDay+1, cellColorGreen);
                    } else {
                        createCell(array[WorkList.WORK_TIME], rowWorkType, cellDay, uCenterCellStyle);
                        createCell(array[WorkList.OVER_WORK], rowWorkType, cellDay+1, uCenterCellStyle);
                    }
                }


                ArrayList<WorkList.WorkTypeWithEmployees.EmployeeWork> employeeList = w.getEmployeeList();

                // iterate through employees
                rowNum++;
                for(WorkList.WorkTypeWithEmployees.EmployeeWork e: employeeList){
                    int groupStart = rowNum;
                    Row rowEmployee = sheet.createRow(rowNum);
                    rowEmployee.createCell(0).setCellValue(e.getEmployee().toString());

                    // sum hours over month for employee
                    if(e.isSumAppove()) {
                        createCell(e.getSumWorkEmployee(), rowEmployee, 1, cellColorGreen);
                        createCell(e.getSumOverWorkEmployee(), rowEmployee, 2, cellColorGreen);
                    } else {
                        createCell(e.getSumWorkEmployee(), rowEmployee, 1, uCenterCellStyle);
                        createCell(e.getSumOverWorkEmployee(), rowEmployee, 2, uCenterCellStyle);
                    }

                    ArrayList<int[]> workSchedule;
                    workSchedule = e.getWork();

                    for(int[] work_array: workSchedule){
                        int numDay = work_array[WorkList.NUM_DAY];
                        int cellDay = numDay * 2 + 1;
                        int workApprove = work_array[WorkList.APPROV];

                        if(workApprove > 0) {
                            createCell(work_array[WorkList.WORK_TIME], rowEmployee, cellDay, cellColorGreen);
                            createCell(work_array[WorkList.OVER_WORK], rowEmployee, cellDay+1, cellColorGreen);
                        } else {
                            createCell(work_array[WorkList.WORK_TIME], rowEmployee, cellDay, uCenterCellStyle);
                            createCell(work_array[WorkList.OVER_WORK], rowEmployee, cellDay+1, uCenterCellStyle);
                        }

                    }
                    sheet.groupRow(groupStart, rowNum);
                    rowNum++;
                }
            }


            // write the output to a file
            try {
                FileOutputStream fileOut = new FileOutputStream("example.xls");
                workbook.write(fileOut);
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        private void createOrderInformation(Sheet sheet, Order order, CellStyle orderInfoStyle){
            String[] headerOrder = {"Заказ:", "Адресс:", "Описание:"};

            int rowNum = 0;
            for(String s: headerOrder){
                Row row = sheet.createRow(++rowNum);


                Cell headerCell = row.createCell(0);
                headerCell.setCellStyle(orderInfoStyle);
                headerCell.setCellValue(s);

                switch (rowNum){
                    case 1: row.createCell(1).setCellValue(order.getNameOrder()); break;
                    case 2: row.createCell(1).setCellValue(order.getAddress()); break;
                    case 3: row.createCell(1).setCellValue(order.getDescription()); break;
                    default: break;
                }

            }
        }

        private void createTableHeader(Sheet sheet, CellStyle tableHeaderStyle){
            String[] tableHeader = new String[3];
            tableHeader[0] = "Ф.И.О."; tableHeader[1] = "Всего";
            tableHeader[2] = cbMonth.getValue().toUpperCase();

            Row tableHeaderRow = sheet.createRow(5);

            int maxDaysInMonth = getMaxDaysInMonth(getNumMonth());

            setStyleInRow(tableHeaderRow, (3 + 2 * maxDaysInMonth - 1), tableHeaderStyle);

            createCell(tableHeader[0], tableHeaderRow, 0, tableHeaderStyle);
            createCell(tableHeader[1], tableHeaderRow, 1, tableHeaderStyle);
            createCell(tableHeader[2], tableHeaderRow, 3, tableHeaderStyle);

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

            setStyleInRow(tableNumDaysRow, (3 + 2 * maxDaysInMonth - 1), tableHeaderStyle);

            int cellCount = 3;
            for(int i = 0; i < maxDaysInMonth; ++i){
                Cell cell1 = tableNumDaysRow.createCell(cellCount);
                cell1.setCellValue(i + 1);
                cell1.setCellStyle(tableHeaderStyle);

                Cell cell2 = tableHeaderRow.createCell(cellCount+1);
                cell2.setCellStyle(tableHeaderStyle);

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

            createCell("Раб-та, ч.", tableNumDaysRow, 1, tableHeaderStyle);
            createCell("Перераб, ч.", tableNumDaysRow, 2, tableHeaderStyle);
            sheet.setColumnWidth(1, 3300);
            sheet.setColumnWidth(2, 3500);
        }

        private void createCell( String value, Row row, int column, CellStyle cellStyle) {
            Cell cell = row.createCell(column);
            cell.setCellValue(value);
            cell.setCellStyle(cellStyle);
        }

        private void createCell( int value, Row row, int column, CellStyle cellStyle) {
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

        private int getMaxDaysInMonth(int numMonth){
            YearMonth yearMonth = YearMonth.of(ServerDate.getNumberOfYear(), numMonth);
            return yearMonth.lengthOfMonth();
        }

    }

    //--------------------------------------------------------------------------
    //private
}
