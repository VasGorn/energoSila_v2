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
import model.MoneyList;
import model.Order;
import model.ServerDate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportMoney {
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

    private String[] monthArray = {"Январь", "Февраль", "Март",
            "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь",
            "Окрябрь", "Ноябрь", "Декабрь"};

    @FXML
    private void initialize(){

        setUpMenegers();

        setItemsToComboBoxMonth();

        setCurrentMonth();

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
                if(cbOrder.getValue() != null) {
                    btnCreate.setDisable(false);
                }
            }
        });

        btnCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int numMonth = getNumMonth();
                List<Order> orderList;

                Order order = cbOrder.getValue();

                if(order.getId() == 0){
                    orderList = cbOrder.getItems();
                } else {
                    orderList = new ArrayList<>();
                    orderList.add(order);
                }

                new Thread(new GetMoneyDataToReport(orderList, numMonth)).start();
            }
        });

        btnCreate.setDisable(true);

    }

    private void setCurrentMonth(){
        int numMonth = ServerDate.getNumMonth();

        if(numMonth <= 12) cbMonth.setValue(monthArray[numMonth - 1]);

    }

    private void setUpMenegers(){
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
    }

    //--------------------------------------------------------------------------
    //-----------------------------PRIVATE FUNCTION-----------------------------
    private void setItemsToComboBoxMonth(){

        for(int i = 0; i < 12; ++i){
            cbMonth.getItems().add(monthArray[i]);
        }

    }

    //--------------------------------------------------------------------------
    private void setActiveOrders(){
        Employee employee = cbManager.getValue();
        int numMonth = getNumMonth();

        txtAddress.clear();
        txtDescribe.clear();

        btnCreate.setDisable(true);


        if(employee != null && numMonth != 0){
            int managerID = employee.getID();
            new Thread(new GetMoneyOrders(managerID, numMonth)).start();
        }

    }

    //--------------------------------------------------------------------------
    private int getNumMonth(){
        if(cbMonth.getValue() == null){
            return 0;
        }

        String sMonth = cbMonth.getValue();

        switch (sMonth) {
            case "Январь":
                return 1;
            case "Февраль":
                return 2;
            case "Март":
                return 3;
            case "Апрель":
                return 4;
            case "Май":
                return 5;
            case "Июнь":
                return 6;
            case "Июль":
                return 7;
            case "Август":
                return 8;
            case "Сентябрь":
                return 9;
            case "Окрябрь":
                return 10;
            case "Ноябрь":
                return 11;
            case "Декабрь":
                return 12;
            default:
                return 0;
        }
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

            String reportName = "отчёт_денеж.средства_" + managerName + "_" + orderName + "_" + monthName;

            txtFileName.setText(reportName);
        }
    }

    //--------------------------------------------------------------------------
    //---------------------------PRIVATE CLASSES--------------------------------
    private class GetMoneyOrders implements Runnable{
        private String managerID;
        private String numMonth;

        private GetMoneyOrders(int managerID, int numMonth){
            this.managerID = String.valueOf(managerID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            //String managerID = String.valueOf(User.getId());
            String jsonStr = HttpHandler.getMoneyOrdersForManager(managerID, numMonth);

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

    private class GetMoneyDataToReport implements Runnable {
        private List<Order> orderList;
        private String numMonth;

        private GetMoneyDataToReport(List<Order> orderList, int numMonth) {
            this.numMonth = String.valueOf(numMonth);
            this.orderList = orderList;
        }

        @Override
        public void run() {
            activateProgressIndicator(true);
            List<MoneyList> moneyList = new ArrayList<>();
            boolean flag = true;

            for (Order o : orderList) {
                if (o.getId() == 0) continue;

                String orderID = String.valueOf(o.getId());
                String jsonStr = HttpHandler.getDataToReportMoney(orderID, numMonth);

                if (jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {
                        JSONArray moneyListJSON = jsonObj.getJSONArray("moneyReport");

                        MoneyList work = new MoneyList(o, moneyListJSON);
                        moneyList.add(work);

                    } else {
                        Massage.showDataNotFound();
                        flag = false;
                        break;
                    }
                } else {
                    Massage.showNetworkError();
                    flag = false;
                    break;
                }
            }

            if (flag) {
                // test without excel
                for (MoneyList m: moneyList) {
                    System.out.println("order: " + m.getOrder().toString() +
                            "; all money: " + m.getSumMoneyForAllEmployee());
                    List<MoneyList.EmployeeWithMoneyType> e = m.getEmployeeList();

                    for(MoneyList.EmployeeWithMoneyType w: e){
                        System.out.println(w.getEmployee().toString() + "; money record: " +
                                w.getSumRecordEmployee() + "; money on order: " +
                                w.getSumOnEmployeeByOrder());
                        List<MoneyList.EmployeeWithMoneyType.MoneyTypeData> d = w.getMoneyTypeDataList();

                        for(MoneyList.EmployeeWithMoneyType.MoneyTypeData t: d){
                            System.out.println(t.getMoneyType().toString());
                            List<int[]> a = t.getData();

                            for(int[] b: a){
                                System.out.println("day: " + b[0] + "; sum: " + b[1] +
                                        "; approve: " + b[2]);
                            }
                        }

                    }

                }

                createExcel(moneyList);
            }


            activateProgressIndicator(false);

        }

        private void createExcel(List<MoneyList> moneyList) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    // create workbook
                    Workbook workbook = new HSSFWorkbook();

                    for (MoneyList w : moneyList) {
                        createSheet(w, workbook);
                    }

                    // write the output to a file
                    try {
                        Path currentPath = Paths.get("");
                        String absPath = currentPath.toAbsolutePath().toString();

                        absPath = absPath + "/REPORT/";

                        File fileDir = new File(absPath);

                        if (!fileDir.exists()) {
                            fileDir.mkdir();
                        }

                        String fileName = txtFileName.getText() + ".xls";

                        File file = new File(fileDir, fileName);

                        System.out.println(absPath);

                        FileOutputStream fileOut = new FileOutputStream(file);
                        workbook.write(fileOut);
                        fileOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Massage.show("Не удалось создать отчет", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        private void createSheet(MoneyList moneyList, Workbook workbook) {
            Order order = moneyList.getOrder();

            /* CreationHelper helps us create instances of various things like DataFormat,
            Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
            CreationHelper createHelper = workbook.getCreationHelper();

            // create sheet
            Sheet sheet = workbook.createSheet(order.getNameOrder());

            // create a GREEN Font for styling header cells
            Font headerFontGreen = workbook.createFont();
            headerFontGreen.setBold(true);
            headerFontGreen.setFontHeightInPoints((short) 12);
            headerFontGreen.setColor(IndexedColors.BLUE.getIndex());

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
            CellStyle centerCellStyle = workbook.createCellStyle();
            centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            centerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setAllBorder(centerCellStyle, BorderStyle.THIN);
            centerCellStyle.setFont(headerFontBlack);

            //-----------------------------------------------------------------
            // Style for center alignment, but usual text
            CellStyle uCenterCellStyle = workbook.createCellStyle();
            uCenterCellStyle.setAlignment(HorizontalAlignment.CENTER);
            uCenterCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setAllBorder(uCenterCellStyle, BorderStyle.THIN);

            //-----------------------------------------------------------------
            // Style for left alignment and bold text
            CellStyle boldText = workbook.createCellStyle();
            boldText.setAlignment(HorizontalAlignment.LEFT);
            boldText.setVerticalAlignment(VerticalAlignment.CENTER);
            boldText.setWrapText(true);
            setAllBorder(boldText, BorderStyle.THIN);
            boldText.setFont(headerFontBlack);

            sheet.setColumnWidth(0, 10000);
            //-----------------------------------------------------------------
            // ORDER INFORMATION
            createOrderInformation(sheet, order, moneyList, headerCellStyle);

            //-----------------------------------------------------------------
            // TABLE HEADER
            createTableHeader(sheet, centerCellStyle);

            //-----------------------------------------------------------------
            // DATA TABLE
            CellStyle cellColorGreen = workbook.createCellStyle();
            cellColorGreen.setAlignment(HorizontalAlignment.CENTER);
            cellColorGreen.setVerticalAlignment(VerticalAlignment.CENTER);
            cellColorGreen.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
            cellColorGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setAllBorder(cellColorGreen, BorderStyle.THIN);

            CellStyle cellColorGreenBold = workbook.createCellStyle();
            cellColorGreenBold.setAlignment(HorizontalAlignment.CENTER);
            cellColorGreenBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellColorGreenBold.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
            cellColorGreenBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellColorGreenBold.setFont(headerFontBlack);
            setAllBorder(cellColorGreenBold, BorderStyle.THIN);

            List<MoneyList.EmployeeWithMoneyType> employeeList;

            employeeList = moneyList.getEmployeeList();

            // iterate through employee list
            int rowNum = 10;
            for(MoneyList.EmployeeWithMoneyType e: employeeList){
                Row rowEmployee = sheet.createRow(rowNum);

                // employee name
                createCell(e.getEmployee().toString(),rowEmployee, 0, boldText);

                // sum money on employee
                int sumByOrder = e.getSumOnEmployeeByOrder();
                int sumSpent = e.getSumRecordEmployee();
                int sumLeft = sumByOrder - sumSpent;
                if(e.getIsSumApprove()) {
                    createCell(sumByOrder, rowEmployee, 1, cellColorGreenBold);
                    createCell(sumSpent, rowEmployee, 2, cellColorGreenBold);
                    createCell(sumLeft, rowEmployee, 3, cellColorGreenBold);
                } else {
                    createCell(sumByOrder, rowEmployee, 1, centerCellStyle);
                    createCell(sumSpent, rowEmployee, 2, centerCellStyle);
                    createCell(sumLeft, rowEmployee, 3, centerCellStyle);
                }


                List<int[]> sumEmployeeList;
                sumEmployeeList = e.getSumMoneyOnDayList();

                // employee sum money on day
                for(int[] array: sumEmployeeList){
                    int numDay = array[MoneyList.NUM_DAY];
                    int cellDay = numDay + 3;
                    int moneyApprove = array[MoneyList.APPROV];

                    if(moneyApprove > 0) {
                        createCell(array[MoneyList.SUM_MONEY], rowEmployee, cellDay, cellColorGreen);
                    } else {
                        createCell(array[MoneyList.SUM_MONEY], rowEmployee, cellDay, uCenterCellStyle);
                    }
                }


                List<MoneyList.EmployeeWithMoneyType.MoneyTypeData> moneyTypeList = e.getMoneyTypeDataList();

                // iterate through money type
                rowNum++;
                for(MoneyList.EmployeeWithMoneyType.MoneyTypeData m: moneyTypeList){
                    int groupStart = rowNum;
                    Row rowMoneyType= sheet.createRow(rowNum);
                    rowMoneyType.createCell(0).setCellValue(m.getMoneyType().toString());

                    // sum hours over month for employee
                    if(m.isSumAppove()) {
                        createCell(m.getSumMoneyType(), rowMoneyType, 2, cellColorGreen);
                    } else {
                        createCell(m.getSumMoneyType(), rowMoneyType, 2, uCenterCellStyle);
                    }

                    List<int[]> moneySchedule;
                    moneySchedule = m.getData();

                    for(int[] money_array: moneySchedule){
                        int numDay = money_array[MoneyList.NUM_DAY];
                        int cellDay = numDay + 3;
                        int moneyApprove = money_array[MoneyList.APPROV];

                        if(moneyApprove > 0) {
                            createCell(money_array[MoneyList.SUM_MONEY], rowMoneyType, cellDay, cellColorGreen);
                        } else {
                            createCell(money_array[MoneyList.SUM_MONEY], rowMoneyType, cellDay, uCenterCellStyle);
                        }

                    }
                    sheet.groupRow(groupStart, rowNum);
                    rowNum++;
                }
            }

        }

        private void createOrderInformation(Sheet sheet, Order order, MoneyList moneyList, CellStyle orderInfoStyle) {
            String[] headerOrder = {"Заказ:", "Адресс:", "Описание:", "Общая сумма по заказу:",
                                    "Потрачено:", "Остаток:"};

            int rowNum = 0;
            for (String s : headerOrder) {
                Row row = sheet.createRow(++rowNum);


                Cell headerCell = row.createCell(0);
                headerCell.setCellStyle(orderInfoStyle);
                headerCell.setCellValue(s);

                int allSum = moneyList.getSumMoneyForAllEmployee();
                int allSpent = moneyList.getSumMoneyAllSpent();
                int allLeft = allSum - allSpent;

                switch (rowNum) {
                    case 1:
                        row.createCell(1).setCellValue(order.getNameOrder());
                        break;
                    case 2:
                        row.createCell(1).setCellValue(order.getAddress());
                        break;
                    case 3:
                        row.createCell(1).setCellValue(order.getDescription());
                        break;
                    case 4:
                        row.createCell(1).setCellValue(allSum);
                        break;
                    case 5:
                        row.createCell(1).setCellValue(allSpent);
                        break;
                    case 6:
                        row.createCell(1).setCellValue(allLeft);
                        break;
                    default:
                        break;
                }

            }
        }

        private void createTableHeader(Sheet sheet, CellStyle tableHeaderStyle){
            String[] tableHeader = new String[3];
            tableHeader[0] = "Ф.И.О."; tableHeader[1] = "Всего";
            tableHeader[2] = cbMonth.getValue().toUpperCase();

            Row tableHeaderRow = sheet.createRow(8);

            int maxDaysInMonth = getMaxDaysInMonth(getNumMonth());

            setStyleInRow(tableHeaderRow, (4 + maxDaysInMonth - 1), tableHeaderStyle);

            createCell(tableHeader[0], tableHeaderRow, 0, tableHeaderStyle);
            createCell(tableHeader[1], tableHeaderRow, 1, tableHeaderStyle);
            createCell(tableHeader[2], tableHeaderRow, 4, tableHeaderStyle);

            //fio
            sheet.addMergedRegion(new CellRangeAddress(
                    8,
                    9,
                    0,
                    0
            ));

            //all
            sheet.addMergedRegion(new CellRangeAddress(
                    8,
                    8,
                    1,
                    3
            ));

            //month
            sheet.addMergedRegion(new CellRangeAddress(
                    8,
                    8,
                    4,
                    4 + maxDaysInMonth - 1
            ));

            // create row with number of days
            Row tableNumDaysRow = sheet.createRow(9);

            setStyleInRow(tableNumDaysRow, (4 + maxDaysInMonth - 1), tableHeaderStyle);

            int cellCount = 4;
            for(int i = 0; i < maxDaysInMonth; ++i){
                Cell cell1 = tableNumDaysRow.createCell(cellCount);
                cell1.setCellValue(i + 1);
                cell1.setCellStyle(tableHeaderStyle);

                sheet.setColumnWidth(cellCount, 2000);

                ++cellCount;

            }

            createCell("По заказу, грн.", tableNumDaysRow, 1, tableHeaderStyle);
            createCell("Потрачено, грн.", tableNumDaysRow, 2, tableHeaderStyle);
            createCell("Остаток, грн.", tableNumDaysRow, 3, tableHeaderStyle);

            sheet.setColumnWidth(1, 3800);
            sheet.setColumnWidth(2, 3800);
            sheet.setColumnWidth(3, 3500);

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
}
