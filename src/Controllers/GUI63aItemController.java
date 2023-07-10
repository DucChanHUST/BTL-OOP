package Controllers;

import Models.QQuestion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Vector;

public class GUI63aItemController implements Initializable {
    public CheckBox question_cbx;
    @FXML
    private Label name_lb;
    @FXML
    private Label text_lbl;
    @FXML
    private Label id_lbl;
    public void setData(QQuestion qQuestion){
        name_lb.setText(qQuestion.getName());
        text_lbl.setText(qQuestion.getText());
        id_lbl.setText(qQuestion.getQuestion_id()+"");
    }
    public static Vector<String> prepareToAdd = new Vector<>();
    public void setQuestion_cbx() {
        if (question_cbx.isSelected()) {
            prepareToAdd.add(id_lbl.getText());
        }
        else {
            for (int i = 0; i < prepareToAdd.size(); i++)
                if (Objects.equals(id_lbl.getText(), prepareToAdd.get(i))) {
                    prepareToAdd.remove(i);
                    break;
                }
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        question_cbx.setOnAction(event -> setQuestion_cbx());
    }
}