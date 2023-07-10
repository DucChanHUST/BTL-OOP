package Controllers;

import Models.QQuestion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class GUI65ItemController implements Initializable {
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public QQuestion getQuestion() {
        QQuestion question = new QQuestion();
        question.setName(name_lb.getText());
        question.setText(text_lbl.getText());
        question.setQuestion_id(Integer.parseInt(id_lbl.getText()));
        return question;
    }
}
