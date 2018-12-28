package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Qout {
    @FXML
    private ComboBox<Order> cbNameOrder;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;


    @FXML
    private TextField tfManager;
    @FXML
    private TextField tfMaxHours;
    @FXML
    private TextField tfDescription;
    @FXML
    private TextField tfAddress;

    @FXML
    private ComboBox<String> cbPosition;
    @FXML
    private ComboBox<Employee> cbWorkers;
    @FXML
    private ComboBox<String> cbMonth;

    @FXML
    private ComboBox<WorkType> cbWorkType;

    @FXML
    private TextField tfHours;

    //--------------------------------------------------------------------------
    //table

    @FXML
    private TableView tvWorkTypeWithHours;

    @FXML
    private TableColumn<WorkTypeWithHours, String> colWorkType;

    @FXML
    private TableColumn<WorkTypeWithHours, Integer> colHoursOnWorkType;


    private ObservableList<WorkTypeWithHours> olistWorkTypeWithHours;


    @FXML
    private void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String managerID = String.valueOf(User.getId());
                String jsonStr = HttpHandler.getManagerOrders(managerID);

                System.out.println(jsonStr);

                if(jsonStr != null) {
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
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Massage.show("Что-то пошло не так",
                                        "Неизвестная ошибка");
                            }
                        });
                    }
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Проверьте соединение с сетью");
                        }
                    });
                }
            }
        }).start();

        cbNameOrder.setOnAction(event -> {
            tfManager.setText(cbNameOrder.getValue().getManagerName());
            tfMaxHours.setText(cbNameOrder.getValue().getStringMaxHours());
            tfDescription.setText(cbNameOrder.getValue().getDescription());
            tfAddress.setText(cbNameOrder.getValue().getAddress());

            updateTable();


        });



        cbPosition.getItems().add("Инженер");
        cbPosition.getItems().add("Мастер");

        cbPosition.setOnAction(event -> {
            ArrayList<Employee> listOfEmployee;

            if(olistWorkTypeWithHours != null) {
                olistWorkTypeWithHours.clear();
            }

            String positionID;
            if(cbPosition.getValue().equals("Инженер")){
                positionID = "2";
            }else{
                positionID = "3";
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 3 is the masters
                    String jsonStr = HttpHandler.getEmployeesWithPosition(positionID);

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            JSONArray employeesJSON = jsonObj.getJSONArray("employees");
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

                            new Thread(new GetWorkTypeByPosition(positionID)).start();

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                   cbWorkers.getItems().clear();

                                    for(Employee e: employeesList)
                                        cbWorkers.getItems().add(e);

                                    cbWorkers.setDisable(false);

                                    cbWorkers.setPromptText("выберите сотрудника");
                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Massage.show("Что-то пошло не так",
                                            "Неизвестная ошибка");
                                }
                            });
                        }
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Massage.show("Что-то пошло не так",
                                        "Проверьте соединение с сетью");
                            }
                        });
                    }
                }
            }).start();


        });

        //property for columns
        colHoursOnWorkType.setCellValueFactory(new PropertyValueFactory<>("hoursOnWorkType"));
        colWorkType.setCellValueFactory(new PropertyValueFactory<>("workType"));

        setItemsToComboBoxMonth();

        cbWorkers.setOnAction(event -> {
            updateTable();
        });

        cbMonth.setOnAction(event -> {
            updateTable();
        });

        btnAdd.setDisable(true);
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);

    }

    @FXML
    public void btnAddClicked(){
        String number = tfHours.getText();

        WorkType workType = cbWorkType.getValue();
        Employee employee = cbWorkers.getValue();

        if(workType == null || employee == null){
            Massage.show("", "Данные не выбраны");
            return;
        }

        if(olistWorkTypeWithHours != null){
            for(WorkTypeWithHours wtH: olistWorkTypeWithHours){
                if(wtH.getWorkType().getId() == workType.getId()){
                    Massage.iShow("", "Запись уже была произведена");
                    return;
                }
            }
        }

        try{
            int orderID = cbNameOrder.getValue().getId();
            int employeeID = employee.getID();
            int numMonth = getNumMonth();
            int workTypeID = workType.getId();
            int hours = Integer.parseInt(number);

            if(hours <= 0){
                Massage.show("", "Часы меньше нуля");
                return;
            }

            int sumHours = hours;
            if(olistWorkTypeWithHours != null){
                for(WorkTypeWithHours wtH: olistWorkTypeWithHours){
                    sumHours = sumHours + wtH.getHoursOnWorkType();
                }
            }

            new Thread(new InsertWorkTypeWithHours(orderID, employeeID, numMonth,
                    workTypeID, sumHours, hours)).start();

        }
        catch (NumberFormatException e){
            e.printStackTrace();
            Massage.show("Ошибка преобразованя",e.toString());
        }


    }

    public void btnDeleteClicked(){
        WorkTypeWithHours wtH = (WorkTypeWithHours) tvWorkTypeWithHours.getSelectionModel().getSelectedItem();

        if(wtH == null){
            Massage.show("","Нет данных для удаления");
            return;
        }

        WorkType workType = wtH.getWorkType();

        int orderID = cbNameOrder.getValue().getId();
        int employeeID = cbWorkers.getValue().getID();
        int numMonth = getNumMonth();
        int workTypeID = workType.getId();

        int oldHours = wtH.getHoursOnWorkType();

        int oldSumHours = 0;
        for(WorkTypeWithHours w: olistWorkTypeWithHours){
            oldSumHours = oldSumHours + w.getHoursOnWorkType();
        }

        new Thread(new DeleteWorkTypeWithHours(orderID,employeeID,numMonth,
                workTypeID, oldSumHours, oldHours)).start();


    }

    public void btnEditClicked(){
        String number = tfHours.getText();
        WorkTypeWithHours wtH = (WorkTypeWithHours) tvWorkTypeWithHours.getSelectionModel().getSelectedItem();

        if(wtH == null){
            Massage.show("","Нет данных для обновления");
            return;
        }

        WorkType workType = wtH.getWorkType();


        try{
            int orderID = cbNameOrder.getValue().getId();
            int employeeID = cbWorkers.getValue().getID();
            int numMonth = getNumMonth();
            int workTypeID = workType.getId();
            int newHours = Integer.parseInt(number);

            int oldHours = wtH.getHoursOnWorkType();

            int oldSumHours = 0;
            for(WorkTypeWithHours w: olistWorkTypeWithHours){
                oldSumHours = oldSumHours + w.getHoursOnWorkType();
            }

            new Thread(new UpdateWorkTypeWithHours(orderID, employeeID, numMonth,
                    workTypeID, oldSumHours, oldHours, newHours)).start();

        } catch (NumberFormatException e){
            e.printStackTrace();
        }

    }

    public void tableRightClick(){
        WorkTypeWithHours wtH = (WorkTypeWithHours) tvWorkTypeWithHours.getSelectionModel().getSelectedItem();
        if(wtH != null) {
            btnAdd.setDisable(true);
            btnDelete.setDisable(true);

            btnEdit.setDisable(false);

            cbNameOrder.setDisable(true);
            cbWorkers.setDisable(true);
            cbPosition.setDisable(true);
            cbMonth.setDisable(true);

            WorkType workType = wtH.getWorkType();
            cbWorkType.setValue(workType);
            cbWorkType.setDisable(true);

            tfHours.setText(String.valueOf(wtH.getHoursOnWorkType()));
        }

    }

    public void tableLeftClick(){

        WorkTypeWithHours wtH = (WorkTypeWithHours) tvWorkTypeWithHours.getSelectionModel().getSelectedItem();
        btnAdd.setDisable(false);
        cbNameOrder.setDisable(false);
        cbWorkers.setDisable(false);
        cbPosition.setDisable(false);
        cbMonth.setDisable(false);
        cbWorkType.setDisable(false);

        btnEdit.setDisable(true);

        if(wtH != null) {
            btnDelete.setDisable(false);


            WorkType workType = wtH.getWorkType();
            cbWorkType.setValue(workType);

            tfHours.setText(String.valueOf(wtH.getHoursOnWorkType()));
        }

    }


    public void btnListOrdersClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/ListOrders.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("ListOfOrders");
            workTimeStage.setScene(new Scene(root));
            //workTimeStage.initModality(Modality.APPLICATION_MODAL);
            //workTimeStage.initOwner(btnListOfOrders.getScene().getWindow());
            workTimeStage.show();
        }
        catch (IOException a){
            a.printStackTrace();
        }
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

    private void updateTable(){
        if(cbNameOrder.getValue() == null){
            return;
        } else if(cbWorkers.getValue() == null){
            return;
        }else if (cbMonth.getValue() == null){
            return;
        }

        Order order = cbNameOrder.getValue();
        Employee employee = cbWorkers.getValue();
        String numMonth = String.valueOf(getNumMonth());

        String orderID = String.valueOf(order.getId());
        String employeeID = String.valueOf(employee.getID());

        if(olistWorkTypeWithHours != null){
            olistWorkTypeWithHours.clear();
        }

        btnAdd.setDisable(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = HttpHandler.getWorkTypeWithHours(orderID,employeeID,numMonth);

                System.out.println(jsonStr);

                if(jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {
                        JSONArray workTypesJSON = jsonObj.getJSONArray("work_types_hour");
                        ArrayList<WorkTypeWithHours> workTypes = new ArrayList<>();
                        for (int i = 0; i < workTypesJSON.length(); i++) {
                            JSONObject o = workTypesJSON.getJSONObject(i);

                            int workTypeID = o.getInt(Const.WORK_TYPE_ID);
                            String workTypeName = o.getString(Const.WORK_TYPE_NAME);
                            int hours = o.getInt(Const.ORDER_HAS_WORKTYPE_HOURS);

                            WorkType workType = new WorkType(workTypeID, workTypeName);
                            WorkTypeWithHours wtH = new WorkTypeWithHours(workType, hours);
                            workTypes.add(wtH);
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                olistWorkTypeWithHours = FXCollections.observableArrayList(workTypes);

                                tvWorkTypeWithHours.setItems(olistWorkTypeWithHours);

                                btnAdd.setDisable(false);
                            }
                        });

                    } else {
                        Massage.iShow("Поиск успешен","Статус - новый заказ на часы");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                btnAdd.setDisable(false);
                            }
                        });
                    }

                } else {
                    Massage.showNetworkError();
                }
            }
        }).start();
    }


    private class GetWorkTypeByPosition implements Runnable{
        String positionID;

        GetWorkTypeByPosition(String positionID){
            this.positionID = positionID;
        }

        @Override
        public void run() {
            String jsonStr = HttpHandler.getWorkTypeByPosition(positionID);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    JSONArray workTypesJSON = jsonObj.getJSONArray("work_types");
                    ArrayList<WorkType> workTypes = new ArrayList<>();
                    for (int i = 0; i < workTypesJSON.length(); i++) {
                        JSONObject o = workTypesJSON.getJSONObject(i);

                        int workTypeID = o.getInt(Const.WORK_TYPE_ID);
                        String workTypeName = o.getString(Const.WORK_TYPE_NAME);

                        WorkType workType = new WorkType(workTypeID, workTypeName);
                        workTypes.add(workType);
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            cbWorkType.getItems().clear();

                            for(WorkType w: workTypes)
                                cbWorkType.getItems().add(w);

                            cbWorkType.setDisable(false);

                            cbWorkType.setPromptText("выберите вид");
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Ошибка на сервере");
                        }
                    });
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Massage.show("Что-то пошло не так",
                                "Проверьте соединение с сетью");
                    }
                });
            }
        }
    }


    private class InsertWorkTypeWithHours implements Runnable{
        String orderID;
        String employeeID;
        String numMonth;
        String workTypeID;
        String sumHours;
        String hours;

        private InsertWorkTypeWithHours(int orderID, int employeeID,
                                        int numMonth, int workTypeID,
                                        int sumHours, int hours){
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
            this.workTypeID = String.valueOf(workTypeID);
            this.sumHours = String.valueOf(sumHours);
            this.hours = String.valueOf(hours);
        }

        @Override
        public void run() {
            String jsonStr = HttpHandler.insertWorkTypeWithHours(orderID,employeeID,numMonth,
                    workTypeID, sumHours, hours);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    WorkType workType = cbWorkType.getValue();
                    int intHours = Integer.valueOf(hours);

                    WorkTypeWithHours newWorkTypeWithHours = new WorkTypeWithHours(workType, intHours);


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if(olistWorkTypeWithHours == null) {
                                List<WorkTypeWithHours> qouts = new ArrayList<>();
                                qouts.add(newWorkTypeWithHours);
                                olistWorkTypeWithHours = FXCollections.observableArrayList(qouts);
                                tvWorkTypeWithHours.setItems(olistWorkTypeWithHours);
                            } else{
                                olistWorkTypeWithHours.add(newWorkTypeWithHours);
                            }

                            tvWorkTypeWithHours.refresh();
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Не удалось записать");
                        }
                    });
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Massage.show("Что-то пошло не так",
                                "Проверьте соединение с сетью");
                    }
                });
            }
        }
    }

    private class UpdateWorkTypeWithHours implements Runnable{
        String orderID;
        String employeeID;
        String numMonth;
        String workTypeID;
        String sumHours;
        String hours;

        private UpdateWorkTypeWithHours(int orderID, int employeeID,
                                        int numMonth, int workTypeID,
                                        int oldSumHours, int oldHours, int newHours){
            int sumHours = oldSumHours - oldHours + newHours;
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
            this.workTypeID = String.valueOf(workTypeID);
            this.sumHours = String.valueOf(sumHours);
            this.hours = String.valueOf(newHours);
        }

        @Override
        public void run() {
            String jsonStr = HttpHandler.updateWorkTypeWithHours(orderID,employeeID,numMonth,
                    workTypeID, sumHours, hours);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    WorkType workType = cbWorkType.getValue();
                    int intHours = Integer.valueOf(hours);

                    WorkTypeWithHours newWorkTypeWithHours = new WorkTypeWithHours(workType, intHours);


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for(WorkTypeWithHours w: olistWorkTypeWithHours){
                                if(w.getWorkType().getId() == workType.getId()){
                                    w.setHoursOnWorkType(Integer.valueOf(hours));
                                }
                            }

                            tvWorkTypeWithHours.refresh();
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Не удалось записать");
                        }
                    });
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Massage.show("Что-то пошло не так",
                                "Проверьте соединение с сетью");
                    }
                });
            }
        }
    }

    private class DeleteWorkTypeWithHours implements Runnable{
        String orderID;
        String employeeID;
        String numMonth;
        String workTypeID;
        String sumHours;

        private DeleteWorkTypeWithHours(int orderID, int employeeID,
                                        int numMonth, int workTypeID,
                                        int oldSumHours, int hoursToDelete){
            int sumHours = oldSumHours - hoursToDelete;
            this.orderID = String.valueOf(orderID);
            this.employeeID = String.valueOf(employeeID);
            this.numMonth = String.valueOf(numMonth);
            this.workTypeID = String.valueOf(workTypeID);
            this.sumHours = String.valueOf(sumHours);
        }

        @Override
        public void run() {
            String jsonStr = HttpHandler.deleteWorkTypeWithHours(orderID,employeeID,numMonth,
                    workTypeID, sumHours);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            WorkTypeWithHours selected = (WorkTypeWithHours) tvWorkTypeWithHours.getSelectionModel().getSelectedItem();

                            Iterator<WorkTypeWithHours> it = olistWorkTypeWithHours.iterator();
                            while(it.hasNext()){
                                WorkTypeWithHours w = it.next();
                                if( w == selected){
                                    it.remove();
                                }
                            }
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Не удалось записать");
                        }
                    });
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Massage.show("Что-то пошло не так",
                                "Проверьте соединение с сетью");
                    }
                });
            }
        }
    }
}
