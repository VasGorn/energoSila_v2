package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Employee;
import model.UserTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class UserWindow {
    @FXML
    private ComboBox<Employee> cbAllEmployee;

    @FXML
    private CheckBox cbMaster;

    @FXML
    private CheckBox cbEngineer;

    @FXML
    private CheckBox cbManager;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private TextField tfNick;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private PasswordField tfRepeatPassword;

    @FXML
    private TableView<UserTable> tvUserTable;

    @FXML
    private TableColumn<UserTable, Employee> colEmployee;

    @FXML
    private TableColumn<UserTable, String> colUser;

    @FXML
    private TableColumn<UserTable, CheckBox> colIsMaster;

    @FXML
    private TableColumn<UserTable, CheckBox> colIsEngineer;

    @FXML
    private TableColumn<UserTable, CheckBox> colIsManager;

    private ObservableList<UserTable> list = FXCollections.observableArrayList();
    //private DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    private void initialize(){
        // get all employees
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = HttpHandler.getAllEmployees();

                System.out.println(jsonStr);

                if(jsonStr != null) {
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
                                for(Employee e: employeesList)
                                    cbAllEmployee.getItems().add(e);
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

        //------------------------------------------------------------------------------------
        // get all users
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = HttpHandler.getUsers();

                System.out.println(jsonStr);

                if(jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {
                        JSONArray employeesJSON = jsonObj.getJSONArray("users");
                        for (int i = 0; i < employeesJSON.length(); i++) {
                            JSONObject o = employeesJSON.getJSONObject(i);

                            int employeeID = o.getInt(Const.EMPLOYEE_ID);
                            String lastName = o.getString(Const.EMPLOYEE_LASTNAME);
                            String firstName = o.getString(Const.EMPLOYEE_FIRSTNAME);
                            String middleName = o.getString(Const.EMPLOYEE_MIDDLENAME);

                            Employee employee = new Employee(lastName,firstName,middleName,employeeID);

                            String username = o.getString(Const.USER_NAME);
                            int userID = o.getInt(Const.USER_ID);
                            int positionID = o.getInt(Const.POSITION_ID);

                            CheckBox ch = new CheckBox();
                            ch.setSelected(true);
                            ch.setDisable(true);
                            ch.setOpacity(1);
                            UserTable newUT = new UserTable(employee, userID, username);

                            switch (positionID){
                                case 1: newUT.setCheckBoxIsManager(ch); break;
                                case 2: newUT.setCheckBoxIsEngineer(ch); break;
                                case 3: newUT.setCheckBoxIsMaster(ch); break;
                            }

                            list.add(newUT);

                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                colEmployee.setCellValueFactory(new PropertyValueFactory<UserTable, Employee>("employee"));
                                colUser.setCellValueFactory(new PropertyValueFactory<UserTable, String>("nameUser"));
                                colIsMaster.setCellValueFactory(new PropertyValueFactory<UserTable, CheckBox>("checkBoxIsMaster"));
                                colIsEngineer.setCellValueFactory(new PropertyValueFactory<UserTable, CheckBox>("checkBoxIsEngineer"));
                                colIsManager.setCellValueFactory(new PropertyValueFactory<UserTable, CheckBox>("checkBoxIsManager"));


                                tvUserTable.setItems(list);
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

        //------------------------------------------------------------------------------------

        btnEdit.setDisable(true);

    }

    @FXML
    private void btnAddClicked(){
        String nick = tfNick.getText().trim();
        String password = tfPassword.getText().trim();
        String repeatPassword = tfRepeatPassword.getText().trim();

        Employee selected = cbAllEmployee.getValue();
        ObservableList<UserTable> obsUsers = tvUserTable.getItems();
        if(selected != null) {
            for (UserTable e : obsUsers) {
                int userIDinList = e.getEmployee().getID();
                if (selected.getID() == userIDinList) return;
                if (nick.equals(e.getNameUser())) return;
            }
        }

        if(nick.equals("") || password.equals("") || repeatPassword.equals("")){
            Massage.show("Ошибка","Некоторые данные отсутствуют");
        } else if(!password.equals(repeatPassword)) {
            Massage.show("Ошибка","Пароли не индентичны");
        } else if(cbAllEmployee.getValue() == null){
            Massage.show("Ошибка","Укажите сотрудника");
        } else if(!cbEngineer.isSelected() && !cbManager.isSelected() && !cbMaster.isSelected()){
            Massage.show("Ошибка","Укажите должность");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Employee employee = cbAllEmployee.getValue();
                    String employeeID = String.valueOf(employee.getID());
                    String jsonStr = HttpHandler.insertUser(nick,employeeID,password);

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            int lastUserID = jsonObj.getInt(Const.USER_ID);

                            int positionID;

                            if(cbMaster.isSelected()){
                                positionID = 3;
                            } else if (cbEngineer.isSelected()){
                                positionID = 2;
                            } else if (cbManager.isSelected()){
                                positionID = 1;
                            } else {
                                positionID = 0;
                            }

                            CheckBox ch = new CheckBox();
                            ch.setSelected(true);
                            ch.setDisable(true);
                            ch.setOpacity(1);
                            UserTable newUT = new UserTable(employee, lastUserID, nick);

                            switch (positionID){
                                case 1: newUT.setCheckBoxIsManager(ch); break;
                                case 2: newUT.setCheckBoxIsEngineer(ch); break;
                                case 3: newUT.setCheckBoxIsMaster(ch); break;
                            }

                            list.add(newUT);

                            //add position to employee
                            new Thread(new InsertPosition(employeeID,String.valueOf(positionID))).start();

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    tvUserTable.refresh();
                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Massage.show("Что-то пошло не так",
                                            "Возможно данный сотрудник" + '\n' + "есть в базе данных");
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
        }

    }

    @FXML
    private void btnDeleteClicked(){
        UserTable selected = (UserTable) tvUserTable.getSelectionModel().getSelectedItem();
        if(selected==null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userID = String.valueOf(selected.getUserID());

                String employeeID = String.valueOf(selected.getEmployee().getID());

                int positionID = selected.getPositionID();

                String jsonStr = HttpHandler.deleteUser(userID);

                System.out.println(jsonStr);

                if(jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {


                        //delete position from employee
                        new Thread(new DeletePosition(employeeID,String.valueOf(positionID))).start();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Iterator<UserTable> it = list.iterator();
                                while(it.hasNext()){
                                    UserTable user = it.next();
                                    if( user == selected){
                                        it.remove();
                                    }
                                }

                                tvUserTable.refresh();
                            }
                        });

                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Massage.show("Что-то пошло не так",
                                        "Возможно данный сотрудник" + '\n' + "есть в базе данных");
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
    }

    @FXML
    private void btnEditClicked(){
        UserTable selected = (UserTable) tvUserTable.getSelectionModel().getSelectedItem();
        if(selected==null) {
            return;
        }

        String nick = tfNick.getText().trim();
        String password = tfPassword.getText().trim();
        String repeatPassword = tfRepeatPassword.getText().trim();

        if(nick.equals("") || password.equals("") || repeatPassword.equals("")){
            Massage.show("Ошибка","Некоторые данные отсутствуют");
            return;
        } else if(!password.equals(repeatPassword)) {
            Massage.show("Ошибка","Пароли не индентичны");
            return;
        } else if(!cbEngineer.isSelected() && !cbManager.isSelected() && !cbMaster.isSelected()){
            Massage.show("Ошибка","Укажите должность");
            return;
        }

        int newPositionID;

        if(cbMaster.isSelected()){
            newPositionID = 3;
        } else if (cbEngineer.isSelected()){
            newPositionID = 2;
        } else if (cbManager.isSelected()){
            newPositionID = 1;
        } else {
            newPositionID = 0;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String employeeID = String.valueOf(selected.getEmployee().getID());
                String userID = String.valueOf(selected.getUserID());

                int oldPositionID = selected.getPositionID();

                String jsonStr = HttpHandler.updateUser(userID, nick, password);

                System.out.println(jsonStr);

                if(jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {

                        //delete position from employee
                        new Thread(new UpdatePosition(employeeID, String.valueOf(oldPositionID),
                                String.valueOf(newPositionID))).start();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                selected.setNameUser(nick);

                                selected.setCheckBoxIsMaster(null);
                                selected.setCheckBoxIsEngineer(null);
                                selected.setCheckBoxIsManager(null);

                                CheckBox ch = new CheckBox();
                                ch.setSelected(true);
                                ch.setDisable(true);
                                ch.setOpacity(1);

                                switch (newPositionID){
                                    case 1: selected.setCheckBoxIsManager(ch);
                                    case 2: selected.setCheckBoxIsEngineer(ch);
                                    case 3: selected.setCheckBoxIsMaster(ch);
                                }

                                tvUserTable.refresh();
                            }
                        });

                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Massage.show("Что-то пошло не так",
                                        "Возможно данный сотрудник" + '\n' + "есть в базе данных");
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

    }


    @FXML
    private void btnTableRightClicked(){
        UserTable selected = tvUserTable.getSelectionModel().getSelectedItem();

        tfPassword.clear();
        tfRepeatPassword.clear();

        if(!(selected==null)){
            String nick = selected.getNameUser();
            Employee employee = selected.getEmployee();

            cbAllEmployee.setValue(employee);
            cbAllEmployee.setDisable(true);

            tfNick.setText(nick);

            int positionID = selected.getPositionID();

            cbMaster.setSelected(false);
            cbEngineer.setSelected(false);
            cbManager.setSelected(false);

            switch (positionID){
                case 3: cbMaster.setSelected(true); break;
                case 2: cbEngineer.setSelected(true); break;
                case 1: cbManager.setSelected(true); break;
            }

            btnEdit.setDisable(false);
        }
    }

    @FXML
    private void btnTableLeftClicked(){
        btnEdit.setDisable(true);
        cbAllEmployee.setDisable(false);
    }


    @FXML
    private void cbMasterClicked(){
        if(cbMaster.isSelected()){
            cbEngineer.setSelected(false);
            cbManager.setSelected(false);
        }
    }

    @FXML
    private void cbEngineerClicked(){
        if(cbEngineer.isSelected()){
            cbMaster.setSelected(false);
            cbManager.setSelected(false);
        }
    }

    @FXML
    private void cbManagerClicked(){
        if(cbManager.isSelected()){
            cbMaster.setSelected(false);
            cbEngineer.setSelected(false);
        }
    }

    private class InsertPosition implements Runnable{
        private String employeeID;
        private String positionID;

        private InsertPosition(String employeeID, String positionID){
            this.employeeID = employeeID;
            this.positionID = positionID;
        }

        @Override
        public void run() {

            String jsonStr = HttpHandler.insertPosition(employeeID, positionID);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.iShow("Успех",
                                    "Пользователь добавлен!");
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Возможно данный сотрудник" + '\n' + "есть в базе данных");
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

    private class DeletePosition implements Runnable{
        private String employeeID;
        private String positionID;

        private DeletePosition(String employeeID, String positionID){
            this.employeeID = employeeID;
            this.positionID = positionID;
        }

        @Override
        public void run() {

            String jsonStr = HttpHandler.deletePositionFromEmployee(employeeID, positionID);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.iShow("Успех",
                                    "Пользователь удален!");
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Возможно данный сотрудник" + '\n' + "есть в базе данных");
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


    private class UpdatePosition implements Runnable{
        private String employeeID;
        private String oldPositionID;
        private String newPositionID;

        private UpdatePosition(String employeeID, String oldPositionID, String newPositionID){
            this.employeeID = employeeID;
            this.oldPositionID = oldPositionID;
            this.newPositionID = newPositionID;
        }

        @Override
        public void run() {
            String jsonStr = HttpHandler.updateEmployeeWithPosition(employeeID, oldPositionID, newPositionID);

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.iShow("Успех",
                                    "Данные обновлены!");
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Massage.show("Что-то пошло не так",
                                    "Возможно данный сотрудник" + '\n' + "есть в базе данных");
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
