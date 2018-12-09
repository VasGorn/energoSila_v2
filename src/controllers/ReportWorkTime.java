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
    private ProgressIndicator progressIndicator;

    @FXML
    private VBox vBox;

    @FXML
    private void initialize(){
        activateProgressIndicator(true);
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

                        ArrayList<Employee> employeesList = getArrayListOfEmployee(employeesJSON);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                cbManager.getItems().clear();

                                for(Employee e: employeesList)
                                    cbManager.getItems().add(e);

                                cbManager.setPromptText("...");

                                activateProgressIndicator(false);
                            }
                        });

                    } else {
                        Massage.showDataNotFound();
                    }
                } else {
                    Massage.showNetworkError();
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

    //--------------------------------------------------------------------------
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
        if(isActive){
            vBox.setDisable(true);
            progressIndicator.setVisible(true);
        }else{
            vBox.setDisable(false);
            progressIndicator.setVisible(false);
        }

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
}
