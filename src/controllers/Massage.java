package controllers;

import javafx.scene.control.Alert;

public class Massage {
    static public void show(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    static public void iShow(String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Подтверждение!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
