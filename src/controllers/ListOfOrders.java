package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ListOfOrders {
    @FXML
    private Button btnAddOrder;

    @FXML
    private TextField tfNameOrder;

    @FXML
    private TextField tfMaxHours;

    @FXML
    private TextArea tfDescription;

    @FXML
    private TextArea tfAddress;

    @FXML
    private ListView lvOrders;


    //private ArrayList<Order> newListOrders =
    private ObservableList<Order> observListForWrightTable = FXCollections.observableArrayList();


    @FXML
    private void initialize() {

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
                                observListForWrightTable = FXCollections.observableArrayList(ordersList);
                                lvOrders.setItems(observListForWrightTable);
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

        tfDescription.setWrapText(true);
        tfAddress.setWrapText(true);

        tfMaxHours.setText("0");
    }

    public void btnAddOrderClicked(){

        try{
            String nameOrder = tfNameOrder.getText();
            String description = tfDescription.getText();
            String address = tfAddress.getText();

            for(Order o: observListForWrightTable){
                if(nameOrder.equals(o.getNameOrder()))
                    return;
            }

            int maxHours = Integer.parseInt(tfMaxHours.getText());

            if(nameOrder.equals("") || description.equals("") || address.equals("")){
                Massage.show("Ошибка","Некоторые данные отсутствуют");
                return;
            }else if(maxHours <=0 ){
                Massage.show("Ошибка","Макс. часы должны быть больше нуля!");
                return;
            }

            int managerID = User.getId();
            String nameManager = User.getNameEmployee();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    String jsonStr = HttpHandler.insertOrder(nameOrder, String.valueOf(managerID),
                            address, description, String.valueOf(maxHours));

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            int lastOrderID = jsonObj.getInt("last_id");

                            Order order = new Order(lastOrderID, nameOrder, address,
                                    description, nameManager, maxHours);


                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    observListForWrightTable.add(order);
                                }
                            });

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Massage.show("Что-то пошло не так",
                                            "Возможно по имени" + '\n' + "есть в базе данных");
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


        }catch (NumberFormatException ne){
            ne.printStackTrace();
            Massage.show("Ошибка","Проверьте макс. часы!");

        }

        // TODO UPDATE LIST OF ORDERS IN PREVIOUS WINDOW !!!!

    }

    public void btnEditClicked(){
        Order selected = (Order) lvOrders.getSelectionModel().getSelectedItem();
        if(!(selected==null)){
            try{
                String nameOrder = tfNameOrder.getText();
                String description = tfDescription.getText();
                String address = tfAddress.getText();

                int maxHours = Integer.parseInt(tfMaxHours.getText());


                int orderID =  selected.getId();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String jsonStr = HttpHandler.updateOrder(String.valueOf(orderID),
                                nameOrder, address, description, String.valueOf(maxHours));

                        System.out.println(jsonStr);

                        if(jsonStr != null) {
                            JSONObject jsonObj = new JSONObject(jsonStr);
                            int success = jsonObj.getInt("success");

                            if (success == 1) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        selected.setNameOrder(nameOrder);
                                        selected.setDescription(description);
                                        selected.setAddress(address);
                                        selected.setHoursMax(maxHours);
                                        lvOrders.refresh();
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
            }catch (NumberFormatException ne){
                ne.printStackTrace();
            }
        }
    }

    public void btnDeleteClicked(){
        Order selected = (Order) lvOrders.getSelectionModel().getSelectedItem();
        if(!(selected==null)){
            tfNameOrder.clear();
            tfDescription.clear();
            tfAddress.clear();
            tfMaxHours.clear();

            int orderID = selected.getId();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonStr = HttpHandler.deleteOrder(String.valueOf(orderID));

                    System.out.println(jsonStr);

                    if(jsonStr != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if (success == 1) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    //allList = tableEmployee.getItems();

                                    Iterator<Order> it = observListForWrightTable.iterator();
                                    while(it.hasNext()){
                                        Order em = it.next();
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

    public void listLeftBtnClicked(){
        Object selected = lvOrders.getSelectionModel().getSelectedItem();
        if(!(selected==null)){
            String nameOrder = ((Order) selected).getNameOrder();
            String description = ((Order) selected).getDescription();
            String address = ((Order) selected).getAddress();
            String maxHours = ((Order) selected).getStringMaxHours();

            tfNameOrder.setText(nameOrder);
            tfDescription.setText(description);
            tfAddress.setText(address);
            tfMaxHours.setText(maxHours);

        }
    }

}
