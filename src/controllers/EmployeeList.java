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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class EmployeeList {
    private ObservableList<Employee> obsListEmployee;

    @FXML
    private TextField tfLastName, tfFirstName, tfMiddleName;

    @FXML
    private Button btnAdd, btnEdit, btnDelete;

    @FXML
    private TableView tableEmployee;

    @FXML
    private TableColumn<Employee, String> colNameEmployee;

    @FXML
    private TableColumn<Employee, Integer> colEmployeeID;

    @FXML
    private void initialize(){
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
                                obsListEmployee = FXCollections.observableArrayList(employeesList);

                                //property for columns
                                colNameEmployee.setCellValueFactory(new PropertyValueFactory<>("name"));
                                colEmployeeID.setCellValueFactory(new PropertyValueFactory<>("ID"));

                                //assign list to table
                                tableEmployee.setItems(obsListEmployee);
                            }
                        });

                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showInfMassage("Что-то пошло не так",
                                        "Неизвестная ошибка");
                            }
                        });
                    }
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showInfMassage("Что-то пошло не так",
                                    "Проверьте соединение с сетью");
                        }
                    });
                }
            }
        }).start();

        btnEdit.setDisable(true);
    }

    @FXML
    private void btnAddClicked(){
        String lastName = tfLastName.getText().trim();
        String firstName = tfFirstName.getText().trim();
        String middleName = tfMiddleName.getText().trim();

        if(lastName.equals("") || firstName.equals("") || middleName.equals("")){
            return;
        }
        else{
            //int newID = dbHandler.addEmployee(lastName, firstName, middleName);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonStr = HttpHandler.insertEmployee(lastName,firstName,middleName);

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            int lastUserID = jsonObj.getInt(Const.EMPLOYEE_ID);


                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Employee newEmployee = new Employee(lastName, firstName, middleName, lastUserID);
                                    if(obsListEmployee != null) {
                                        obsListEmployee.add(newEmployee);
                                    }

                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showInfMassage("Что-то пошло не так",
                                            "Возможно данный сотрудник" + '\n' + "есть в базе данных");
                                }
                            });
                        }
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showInfMassage("Что-то пошло не так",
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
        Employee selected = (Employee) tableEmployee.getSelectionModel().getSelectedItem();
        if(!(selected==null)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonStr = HttpHandler.deleteEmployee(String.valueOf(selected.getID()));

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    //allList = tableEmployee.getItems();

                                    Iterator<Employee> it = obsListEmployee.iterator();
                                    while(it.hasNext()){
                                        Employee em = it.next();
                                        if( em == selected){
                                            it.remove();
                                        }
                                    }

                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showInfMassage("Что-то пошло не так",
                                            "Ошибка на сервере");
                                }
                            });
                        }
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showInfMassage("Что-то пошло не так",
                                        "Проверьте соединение с сетью");
                            }
                        });
                    }
                }
            }).start();
        }

    }

    @FXML
    private void btnEditClicked(){
        String lastName = tfLastName.getText().trim();
        String firstName = tfFirstName.getText().trim();
        String middleName = tfMiddleName.getText().trim();

        if(lastName.equals("") || firstName.equals("") || middleName.equals("")){
            return;
        }


        Employee selected = (Employee) tableEmployee.getSelectionModel().getSelectedItem();

        if(!(selected==null)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonStr = HttpHandler.updateEmployee(lastName,firstName, middleName, String.valueOf(selected.getID()));

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    selected.setLastName(lastName);
                                    selected.setFirstName(firstName);
                                    selected.setMiddleName(middleName);
                                    tableEmployee.refresh();
                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showInfMassage("Что-то пошло не так",
                                            "Ошибка на сервере");
                                }
                            });
                        }
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showInfMassage("Что-то пошло не так",
                                        "Проверьте соединение с сетью");
                            }
                        });
                    }
                }
            }).start();

        }
    }

    @FXML
    private void btnRightTableClicked(){
        Object selected = tableEmployee.getSelectionModel().getSelectedItem();
        if(!(selected==null)){
            String lastName = ((Employee) selected).getLastName();
            String firstName = ((Employee) selected).getFirstName();
            String middleName = ((Employee) selected).getMiddleName();

            tfLastName.setText(lastName);
            tfFirstName.setText(firstName);
            tfMiddleName.setText(middleName);

            btnEdit.setDisable(false);
        }
    }

    @FXML
    private void btnLeftTableClicked(){
        btnEdit.setDisable(true);
    }

    private void showInfMassage(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
