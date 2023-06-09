package Controllers;

import Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GUI21Controller implements Initializable {
    @FXML
    private Label switch_lbl;
    @FXML
    private Button home_btn;
    @FXML
    private TabPane tabPane;
    public void showGUI11() {
        Stage stage = (Stage)switch_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showGUI11();
    }
    public void setTabPane(String tabName){
        switch (tabName){
            case "Categories":
                tabPane.getSelectionModel().select(1);
                break;
            case "Import":
                tabPane.getSelectionModel().select(2);
                break;
            case "Export":
                tabPane.getSelectionModel().select(3);
            default:
                tabPane.getSelectionModel().select(0);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        home_btn.setOnAction(event -> showGUI11());
    }
}
