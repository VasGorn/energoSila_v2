package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Employee;
import model.WorkType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class WorkTypeWindow {
    @FXML
    private TextArea tfWorkType;

    @FXML
    private ListView lvWorkType;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private ComboBox<String> cbPosition;

    private ObservableList<WorkType> observListAllWorkType;

    public void initialize(){
        cbPosition.getItems().add("Инженер");
        cbPosition.getItems().add("Мастер");

        cbPosition.setOnAction(event -> {
            btnAdd.setDisable(false);

            int positionID;
            if(cbPosition.getValue().equals("Инженер")){
                positionID= 2;
            }else{
                positionID = 3;
            }

            new Thread(new GetWorkTypeByPosition(positionID)).start();

        });

        btnAdd.setDisable(true);
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void btnAddWorkTypeClicked(){
        String nameWorkType = tfWorkType.getText();
        if(!nameWorkType.equals("")){

            int positionID;
            if(cbPosition.getValue().equals("Инженер")){
                positionID= 2;
            }else{
                positionID = 3;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String strPositionID = String.valueOf(positionID);
                    String jsonStr = HttpHandler.insertWorkType(nameWorkType, strPositionID);

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            int lastID = jsonObj.getInt(Const.WORK_TYPE_ID);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    WorkType workType = new WorkType(lastID, nameWorkType);
                                    if(observListAllWorkType != null) {
                                        observListAllWorkType.add(workType);
                                    }
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

    public void btnEditWorkTypeClicked(){
        WorkType selected = (WorkType) lvWorkType.getSelectionModel().getSelectedItem();
        String nameWorkType = tfWorkType.getText();
        if(selected!=null && !nameWorkType.equals("")){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String workTypeID = String.valueOf(selected.getId());
                    String jsonStr = HttpHandler.updateWorkType(workTypeID, nameWorkType);

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    selected.setTypeName(nameWorkType);
                                    lvWorkType.refresh();
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
            }).start();

        }
    }

    public void btnDeleteTypeClicked(){
        WorkType selWorkType = (WorkType) lvWorkType.getSelectionModel().getSelectedItem();

        if(!(selWorkType==null)){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonStr = HttpHandler.deleteWorkType(String.valueOf(selWorkType.getId()));

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    Iterator<WorkType> it = observListAllWorkType.iterator();
                                    while(it.hasNext()){
                                        WorkType sel = it.next();
                                        if( sel == selWorkType){
                                            it.remove();
                                            tfWorkType.setText("");
                                        }
                                    }

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
            }).start();

        }
    }

    public void lvLeftClicked(){
        Object selected = lvWorkType.getSelectionModel().getSelectedItem();
        if(!(selected==null)){
            WorkType wt = ((WorkType) selected);

            tfWorkType.setText(wt.getTypeName());
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);
        }
    }

    private class GetWorkTypeByPosition implements Runnable{
        String positionID;

        GetWorkTypeByPosition(int positionID){
            this.positionID = String.valueOf(positionID);
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
                            observListAllWorkType = FXCollections.observableArrayList(workTypes);
                            lvWorkType.setItems(observListAllWorkType);

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

}
