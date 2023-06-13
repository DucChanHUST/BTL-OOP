package Controllers;

import Models.QuestionChoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ResourceBundle;

public class GUI32ChoiceController implements Initializable {
        @FXML
        private ComboBox<String> grade;
        @FXML
        private ImageView image;
        @FXML
        private TextArea text;

        public Connection getConnection(){
            Connection connection;
            try {
//            Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "0000");
                return connection;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void getGradeComboBox(){
            QuestionChoice questionChoice = new QuestionChoice() ;
            ObservableList<String> observableList = FXCollections.observableArrayList();
            observableList.add("None");
            for(int i = 0; i<questionChoice.gradeList.size(); i++){
                String boxItem = String.format("%.5f",questionChoice.gradeList.get(i));
                observableList.add(boxItem+'%');
            }
            grade.setItems(observableList);
        }

        public void insertChoice(){
            try{
                Connection connection = getConnection();
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into choice (content,grade)"
                        + "value ('" + text.getText() + "','"
                        + Double.parseDouble(grade.getValue()) + "');");
                System.out.println("Choice Created");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getGradeComboBox();
    }
}
