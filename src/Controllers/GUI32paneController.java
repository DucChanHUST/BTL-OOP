package Controllers;

import Models.Model;
import Models.QuestionChoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.LightBase;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class GUI32paneController implements Initializable {
    @FXML
    private ComboBox categoryComboBox;
    @FXML
    private Label switch_lbl;
    @FXML
    private Button blanks_btn;
    @FXML
    private Button saveAndContinue_btn;
    @FXML
    private Button saveChanges_btn;
    @FXML
    private Button cancel_btn;
    @FXML
    private Label test_label;
    @FXML
    private VBox choicesLayout;

    @FXML
    private TextField questionMark;

    @FXML
    private TextField questionName;

    @FXML
    private TextArea questionText;

    public Connection getConnection() {
        try {
//            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "0000");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void getCategoryComboBox() {
        String queryCategoryName = "" +
                "SELECT CONCAT( REPEAT(' ', COUNT(parent.name) - 1), node.name) AS name " +
                "FROM category AS node," +
                "category AS parent " +
                "WHERE node.lft BETWEEN parent.lft AND parent.rgt " +
                "GROUP BY node.category_id ORDER BY node.lft;";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryCategoryName);
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<String> categoryName = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String item = resultSet.getString("name");
                categoryName.add(item);
            }
            categoryComboBox.setItems(categoryName);
        } catch (Exception e) {e.printStackTrace();}
    }
    public void showGUI21() {
        Stage stage = (Stage)switch_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showGUI21();
    }

    public void insertKMoreChoices(int k){
        for(int i=1;i<=k;i++){
            FXMLLoader loader= new FXMLLoader();
            loader.setLocation(getClass().getResource("/resources/Fxml/GUI32Choice.fxml"));
            try {
                Pane pane = loader.load();
                GUI32ChoiceController controller;
                choicesLayout.getChildren().add(pane);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void insertQuestion(){
        try{
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into question (name, text, mark)"
                    + "value ('" + questionName.getText() + "','"
                    + questionText.getText() + "','"
                    + Integer.parseInt(questionMark.getText()) + "');");
            System.out.println("Question Created!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getCategoryComboBox();
        cancel_btn.setOnAction(event -> showGUI21());
        saveChanges_btn.setOnAction(event -> insertQuestion());
        blanks_btn.setOnAction(event -> insertKMoreChoices(3));
        insertKMoreChoices(2);

    }
}
