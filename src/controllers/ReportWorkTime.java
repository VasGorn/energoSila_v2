package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Employee;
import model.Order;
import model.User;
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
    private void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1 is the manager
                String jsonStr = HttpHandler.getEmployeesWithPosition("1");

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

        setItemsToComboBoxMonth();

        cbMonth.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

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

}
