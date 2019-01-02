package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.WorkType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoneyList {
    private ObservableList<WorkType> obsMoneyList;

    @FXML
    private TextField tfNameMoney;

    @FXML
    private Button btnAdd, btnEdit, btnDelete;

    @FXML
    private ListView<WorkType> lvListMoney;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private AnchorPane anchorePane;

    @FXML
    public void initialize(){
        new Thread(new GetAllMoneyType()).start();
    }


    @FXML
    private void btnAddClicked(){
        String moneyName = tfNameMoney.getText().trim();

        if(moneyName.length() > 0){
            new Thread(new InsertMoneyType(moneyName)).start();
        } else {
            Massage.iShow("", "Введите название!");
        }
    }

    @FXML
    private void btnEditClicked(){
        WorkType moneyType = lvListMoney.getSelectionModel().getSelectedItem();
        String moneyName = tfNameMoney.getText().trim();

        if(moneyType != null && moneyName.length() > 0){
            int moneyTypeID = moneyType.getId();

            new Thread(new UpdateMoneyType(moneyTypeID, moneyName)).start();
        } else {
            Massage.show("", "Данные не выбраны или нет названия вида");
        }
    }

    @FXML
    private void btnDeleteClicked(){
        WorkType moneyType = lvListMoney.getSelectionModel().getSelectedItem();
        if(moneyType != null){
            int moneyTypeID = moneyType.getId();
            new Thread(new DeleteMoneyType(moneyTypeID)).start();
        } else {
            Massage.show("", "Выберите данные для удаления");
        }

    }

    @FXML
    private void listViewClicked(){
        WorkType moneyType = lvListMoney.getSelectionModel().getSelectedItem();
        if(moneyType != null){
            tfNameMoney.setText(moneyType.toString());
        }
    }


    // =========================================================================
    // function
    private void activateProgressIndicator(boolean isActive){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(isActive){
                    anchorePane.setDisable(true);
                    progressIndicator.setVisible(true);
                }else{
                    anchorePane.setDisable(false);
                    progressIndicator.setVisible(false);
                }
            }
        });
    }





    // =========================================================================
    // inner Classes

    private class GetAllMoneyType implements Runnable {

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.getAllMoneyType();

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");

                if (success == 1) {
                    JSONArray moneyListJSON = jsonObj.getJSONArray("money");

                    List<WorkType> moneyTypeList = new ArrayList<>();

                    for(int i = 0; i < moneyListJSON.length(); i++){
                        JSONObject o = moneyListJSON.getJSONObject(i);

                        String moneyName = o.getString(Const.MONEY_TYPE_NAME);
                        int moneyTypeID = o.getInt(Const.MONEY_TYPE_ID);

                        WorkType moneyType = new WorkType(moneyTypeID, moneyName);

                        moneyTypeList.add(moneyType);
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            obsMoneyList = FXCollections.observableArrayList(moneyTypeList);
                            lvListMoney.setItems(obsMoneyList);
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

    private class InsertMoneyType implements Runnable{
        private String nameMoneyType;

        private InsertMoneyType (String nameMoneyType){
            this.nameMoneyType = nameMoneyType;
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.insertMoneyType(nameMoneyType);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");
                int moneyTypeID = jsonObj.getInt("id");
                if (success == 1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            WorkType moneyType = new WorkType(moneyTypeID,nameMoneyType);
                            if(obsMoneyList != null){
                                obsMoneyList.add(moneyType);
                            } else {
                                List<WorkType> newList = new ArrayList<>();
                                newList.add(moneyType);
                                obsMoneyList = FXCollections.observableArrayList(newList);
                                lvListMoney.setItems(obsMoneyList);
                            }

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

    private class DeleteMoneyType implements Runnable{
        private String moneyTypeID;

        private DeleteMoneyType (int moneyTypeID){
            this.moneyTypeID = String.valueOf(moneyTypeID);
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.deleteMoneyType(moneyTypeID);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");
                if (success == 1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            Iterator<WorkType> it = obsMoneyList.iterator();
                            while(it.hasNext()){
                                WorkType sel = it.next();
                                if( sel.getId() == Integer.valueOf(moneyTypeID)){
                                    it.remove();
                                    tfNameMoney.setText("");
                                }
                            }

                        }
                    });

                } else {
                    Massage.show("", "Ошибка удаления");
                }
            } else {
                Massage.showNetworkError();
            }

            activateProgressIndicator(false);
        }
    }

    private class UpdateMoneyType implements Runnable{
        private String moneyTypeID;
        private String nameMoneyType;

        private UpdateMoneyType (int moneyTypeID, String nameMoneyType){
            this.moneyTypeID = String.valueOf(moneyTypeID);
            this.nameMoneyType = nameMoneyType;
        }

        @Override
        public void run() {
            activateProgressIndicator(true);

            String jsonStr = HttpHandler.updateMoneyType(moneyTypeID, nameMoneyType);

            if (jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");
                if (success == 1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            int id = Integer.valueOf(moneyTypeID);
                            for(WorkType m: obsMoneyList){
                                if(m.getId() == id) m.setTypeName(nameMoneyType);
                            }

                            lvListMoney.refresh();

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



}
