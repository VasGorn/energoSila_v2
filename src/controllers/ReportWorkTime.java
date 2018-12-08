package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Employee;
import model.Order;

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


}
