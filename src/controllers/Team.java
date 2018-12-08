package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Employee;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Team {
    private ObservableList<Employee> obsListAllEmployee;
    private ObservableList<Employee> obsListTeamEmployee;

    @FXML
    private ComboBox<Employee> cbMasters;

    @FXML
    private TableView tableAllEmployee;

    @FXML
    private TableColumn<Employee, String> colAllNameEmployee;

    @FXML
    private TableColumn<Employee, Integer> colAllEmployeeID;

    //------------------------------------------------------------------

    @FXML
    private TableView tableTeamEmployee;

    @FXML
    private TableColumn<Employee, String> colTeamNameEmployee;

    @FXML
    private TableColumn<Employee, Integer> colTeamEmployeeID;

    @FXML
    private void initialize(){
        //get masters
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 3 is the masters
                String jsonStr = HttpHandler.getEmployeesWithPosition("3");

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
                                //ComboBox - masters
                                for(Employee e: employeesList)
                                    cbMasters.getItems().add(e);

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


        // if checkBox of masters changes
        cbMasters.setOnAction(event -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Employee master = cbMasters.getValue();
                    String jsonStr = HttpHandler.getWorkersOnMaster(String.valueOf(master.getID()));

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            JSONArray employeesJSON = jsonObj.getJSONArray("team");
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
                                    obsListTeamEmployee = FXCollections.observableArrayList(employeesList);
                                    tableTeamEmployee.setItems(obsListTeamEmployee);

                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    obsListTeamEmployee.clear();
                                    Massage.show("Что-то пошло не так",
                                            "Возможно у мастера нет бригады");
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

        //----------------------------------------------------------------------------------------
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
                                obsListAllEmployee = FXCollections.observableArrayList(employeesList);

                                //property for "All" columns
                                colAllNameEmployee.setCellValueFactory(new PropertyValueFactory<>("name"));
                                colAllEmployeeID.setCellValueFactory(new PropertyValueFactory<>("ID"));

                                //assign list to "All" table
                                tableAllEmployee.setItems(obsListAllEmployee);
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



        //----------------------------------------------------------------------------------------
        //property for "team" columns
        colTeamNameEmployee.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeamEmployeeID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        //assign list to "team" table
        //tableTeamEmployee.setItems(obsListTeamEmployee);
    }

    public void btnAddClicked(){
        Object selected = tableAllEmployee.getSelectionModel().getSelectedItem();
        ObservableList<Employee> obsListTeam = tableTeamEmployee.getItems();
        if(selected != null) {
            for (Employee e : obsListTeam) {
                if (e.equals(selected))
                    return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String masterID = String.valueOf(cbMasters.getValue().getID());
                String workerID = String.valueOf(((Employee)selected).getID());
                String jsonStr = HttpHandler.insertWorkerToMaster(masterID, workerID);

                System.out.println(jsonStr);

                if(jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int success = jsonObj.getInt("success");

                    if (success == 1) {

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                obsListTeamEmployee.add((Employee)selected);
                            }
                        });

                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Massage.show("Что-то пошло не так",
                                        "Возможно данный сотрудник" + '\n' + "есть в бригаде");
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

    public void btnDeleteClicked(){
        Object selected = tableTeamEmployee.getSelectionModel().getSelectedItem();
        ObservableList<Employee> obsListTeam = tableTeamEmployee.getItems();
        if(selected != null){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Iterator<Employee> it = obsListTeam.iterator();
                    while(it.hasNext()){
                        Employee sel = it.next();
                        if(sel.equals(selected)){
                            String masterID = String.valueOf(cbMasters.getValue().getID());
                            String workerID = String.valueOf(((Employee)selected).getID());

                            String jsonStr = HttpHandler.deleteWorkerFromMaster(masterID, workerID);

                            System.out.println(jsonStr);

                            if(jsonStr != null) {
                                JSONObject jsonObj = new JSONObject(jsonStr);
                                int success = jsonObj.getInt("success");

                                if (success == 1) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            //allList = tableEmployee.getItems();
                                            it.remove();

                                        }
                                    });
                                    break;

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
                    }

            }).start();




        }
    }

}
