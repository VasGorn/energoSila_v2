package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Employee;
import model.Order;
import org.json.JSONArray;
import org.json.JSONObject;

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

    //--------------------------------------------------------------------------
    //private
}
