package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MoneyQout {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ComboBox<String> cbMonth;
    @FXML
    private ComboBox<Order> cbNameOrder;

    @FXML
    private TextField tfManager;
    @FXML
    private TextField tfMaxHours;
    @FXML
    private TextArea taDescription;
    @FXML
    private TextArea taAddress;

    @FXML
    private ComboBox<String> cbPosition;

    @FXML
    private ComboBox<Employee> cbWorkers;

    @FXML
    private TextField tfCurrentSum;
    @FXML
    private TextField tfSum;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    @FXML
    public void initialize(){
        setItemsToComboBoxMonth();

        updateOrders();

        cbMonth.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tfSum.clear();
                tfCurrentSum.clear();
                updateOrders();
            }
        });

        cbNameOrder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tfSum.clear();
                tfCurrentSum.clear();
                orderChanged();
            }
        });

        cbPosition.getItems().add("Инженер");
        cbPosition.getItems().add("Мастер");

        cbPosition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                positionChanged();
            }
        });

        cbWorkers.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                workerChanged();
            }
        });
    }


    @FXML
    private void btnAddClicked(){
        if(cbPosition.getValue() == null ||
                cbNameOrder.getValue() == null ||
                cbMonth.getValue() == null ||
                cbWorkers.getValue() == null) return;

        int orderID = cbNameOrder.getValue().getId();
        int employeeID = cbWorkers.getValue().getID();
        int numMonth = getNumMonth();

        String sum = tfSum.getText().trim();

        if(sum.length() < 1) return;

        int sumMoney = 0;

        try {
            sumMoney = Integer.parseInt(sum);
        } catch (NumberFormatException e){
            e.printStackTrace();
            Massage.show("Ошибка преобразованя",e.toString());
            return;
        }

        if(sumMoney > 0)
            new Thread(new InsertMoneyOnOrder(orderID, employeeID, numMonth, sumMoney)).start();

    }

    @FXML
    private void btnEditClicked(){
        if(cbPosition.getValue() == null ||
                cbNameOrder.getValue() == null ||
                cbMonth.getValue() == null ||
                cbWorkers.getValue() == null) return;

        int orderID = cbNameOrder.getValue().getId();
        int employeeID = cbWorkers.getValue().getID();
        int numMonth = getNumMonth();

        String sum = tfSum.getText().trim();

        if(sum.length() < 1) return;

        int sumMoney = 0;

        try {
            sumMoney = Integer.parseInt(sum);
        } catch (NumberFormatException e){
            e.printStackTrace();
            Massage.show("Ошибка преобразованя",e.toString());
            return;
        }

        if(sumMoney > 0)
            new Thread(new UpdateMoneyOnOrder(orderID, employeeID, numMonth, sumMoney)).start();


    }

    @FXML
    private void btnDeleteClicked(){
        if(cbPosition.getValue() == null ||
                cbNameOrder.getValue() == null ||
                cbMonth.getValue() == null ||
                cbWorkers.getValue() == null) return;

        int orderID = cbNameOrder.getValue().getId();
        int employeeID = cbWorkers.getValue().getID();
        int numMonth = getNumMonth();

        new Thread(new DeleteMoneyOnOrder(orderID, employeeID, numMonth)).start();

    }

    // =========================================================================
    // function
    private void activateProgressIndicator(boolean isActive){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(isActive){
                    anchorPane.setDisable(true);
                    progressIndicator.setVisible(true);
                }else{
                    anchorPane.setDisable(false);
                    progressIndicator.setVisible(false);
                }
            }
        });
    }

    private void setItemsToComboBoxMonth(){

        //add values to cbMonth
        for(int i = 1; i <= 12; ++i){
            String nameMonth = ServerDate.getNameOfMonth(i);
            cbMonth.getItems().add(nameMonth);
        }

        //set current month
        cbMonth.setValue(ServerDate.getCurrentNameOfMonth());



    }

    private void updateOrders(){
        int managerID = User.getId();
        int numMonth = getNumMonth();

        tfManager.clear();
        tfMaxHours.clear();
        taDescription.clear();
        taAddress.clear();

        if(managerID > 0 && numMonth != 0){
            cbNameOrder.getItems().clear();

            if(cbNameOrder.isDisable()){
                cbNameOrder.setDisable(false);
            }

            cbPosition.setDisable(true);

            cbNameOrder.setPromptText("...");

            new Thread(new GetOrdersWithQouts(managerID, numMonth)).start();
        }

    }

    private void orderChanged(){
        Order order = cbNameOrder.getValue();

        cbWorkers.getItems().clear();
        cbWorkers.setDisable(true);

        cbPosition.setValue(null);


        if(order != null){
            tfManager.setText(User.getNameEmployee());
            tfMaxHours.setText(order.getStringMaxHours());
            taAddress.setText(order.getAddress());
            taDescription.setText(order.getDescription());

            cbPosition.setDisable(false);
        }
    }

    private void positionChanged(){
        int positionID;

        if(cbPosition.getValue() == null) return;

        if(cbPosition.getValue().equals("Инженер")){
            positionID = 2;
        }else{
            positionID = 3;
        }

        int orderID = cbNameOrder.getValue().getId();
        int numMonth = getNumMonth();

        cbWorkers.setDisable(true);

        if(orderID > 0 && numMonth > 0){
            new Thread(new GetEmployeeOnQout(positionID, orderID, numMonth)).start();
        }
    }

    private void workerChanged(){
        if(cbPosition.getValue() == null ||
                cbNameOrder.getValue() == null ||
                cbMonth.getValue() == null ||
                cbWorkers.getValue() == null) return;

        tfSum.clear();

        int employeeID = cbWorkers.getValue().getID();
        int orderID = cbNameOrder.getValue().getId();
        int numMonth = getNumMonth();

        tfCurrentSum.clear();

        new Thread(new GetMoneyOnOrder(orderID, employeeID, numMonth)).start();

    }


    private int getNumMonth(){
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

    // =========================================================================
    // inner Classes
    private class GetOrdersWithQouts implements Runnable{
        private String managerID;
        private String numMonth;

        private GetOrdersWithQouts(int managerID, int numMonth){
            this.managerID = String.valueOf(managerID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.getOrdersWithQuots(managerID, numMonth);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    JSONArray ordersJSON = jsonObj.getJSONArray("orders");
                    ArrayList<Order> ordersList = new ArrayList<>();
                    for (int i = 0; i < ordersJSON.length(); i++) {
                        JSONObject o = ordersJSON.getJSONObject(i);

                        int orderID = o.getInt(Const.ORDER_ID);
                        String nameOrder = o.getString(Const.ORDER_NAME);
                        String adress = o.getString(Const.ORDER_ADRESS);
                        String description = o.getString(Const.ORDER_DERSCRIPTION);
                        int maxHours = o.getInt(Const.ORDER_MAX_HOURS);

                        Order order = new Order(orderID, nameOrder,adress, description,
                                User.getNameEmployee(),maxHours);
                        ordersList.add(order);
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(Order o: ordersList)
                                cbNameOrder.getItems().add(o);
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
    }

    private class GetEmployeeOnQout implements Runnable{
        private String positionID;
        private String orderID;
        private String numMonth;

        private GetEmployeeOnQout(int positionID, int orderID, int numMonth){
            this.positionID = String.valueOf(positionID);
            this.orderID = String.valueOf(orderID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.getEmployeesOnQout(positionID, orderID, numMonth);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    JSONArray employeesJSON = jsonObj.getJSONArray("employee");
                    ArrayList<Employee> employeesList = new ArrayList<>();
                    for (int i = 0; i < employeesJSON.length(); i++) {
                        JSONObject o = employeesJSON.getJSONObject(i);

                        int userID = o.getInt(Const.EMPLOYEE_ID);
                        String lastName = o.getString(Const.EMPLOYEE_LASTNAME);
                        String firstName = o.getString(Const.EMPLOYEE_FIRSTNAME);
                        String middleName = o.getString(Const.EMPLOYEE_MIDDLENAME);

                        Employee employee = new Employee(lastName,firstName,middleName,userID);
                        employeesList.add(employee);
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            cbWorkers.getItems().clear();

                            for(Employee e: employeesList)
                                cbWorkers.getItems().add(e);

                            cbWorkers.setDisable(false);
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
    }

    private class GetMoneyOnOrder implements Runnable{
        private String orderID;
        private String employeeID;
        private String numMonth;

        private GetMoneyOnOrder(int orderID, int employeeID, int numMonth){
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.getMoneyQout(orderID, employeeID, numMonth);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    int moneySum = jsonObj.getInt(Const.MONEY_QOUT_SUM);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnAdd.setDisable(true);
                            btnEdit.setDisable(false);
                            btnDelete.setDisable(false);
                            tfCurrentSum.setText(String.valueOf(moneySum));
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnAdd.setDisable(false);
                            btnEdit.setDisable(true);
                            btnDelete.setDisable(true);
                            tfCurrentSum.setText("нет данных");
                        }
                    });
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }
    }

    private class InsertMoneyOnOrder implements Runnable{
        private String orderID;
        private String employeeID;
        private String numMonth;
        private String sumMoney;

        private InsertMoneyOnOrder(int orderID, int employeeID, int numMonth, int sumMoney){
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
            this.sumMoney = String.valueOf(sumMoney);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.insertMoneyQout(orderID, employeeID, numMonth, sumMoney);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnAdd.setDisable(true);
                            btnEdit.setDisable(false);
                            btnDelete.setDisable(false);
                            tfCurrentSum.setText(String.valueOf(sumMoney));
                        }
                    });

                } else {
                    Massage.show("", "Ошибка добавления");
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }
    }

    private class UpdateMoneyOnOrder implements Runnable{
        private String orderID;
        private String employeeID;
        private String numMonth;
        private String sumMoney;

        private UpdateMoneyOnOrder(int orderID, int employeeID, int numMonth, int sumMoney){
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
            this.sumMoney = String.valueOf(sumMoney);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.updateMoneyQout(orderID, employeeID, numMonth, sumMoney);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            tfCurrentSum.setText(String.valueOf(sumMoney));
                        }
                    });

                } else {
                    Massage.show("", "Ошибка изменения");
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }
    }

    private class DeleteMoneyOnOrder implements Runnable{
        private String orderID;
        private String employeeID;
        private String numMonth;

        private DeleteMoneyOnOrder(int orderID, int employeeID, int numMonth){
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.deleteMoneyQout(orderID, employeeID, numMonth);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnAdd.setDisable(false);
                            btnEdit.setDisable(true);
                            btnDelete.setDisable(true);
                            tfCurrentSum.setText("");
                            tfSum.setText("");
                        }
                    });

                } else {
                    Massage.show("", "Ошибка удаления");

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnAdd.setDisable(true);
                            btnEdit.setDisable(false);
                            btnDelete.setDisable(false);
                            tfCurrentSum.setText("");
                            tfSum.setText("");
                        }
                    });
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }
    }
}
