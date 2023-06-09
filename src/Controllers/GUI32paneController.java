package Controllers;

import Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.Vector;

public class GUI32paneController implements Initializable {
    @FXML
    private ComboBox<String> comboBox;
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
    private VBox choicesLayout;
    @FXML
    private TextField questionMarkTextField;
    @FXML
    private TextField questionNameTextField;
    @FXML
    private TextArea questionTextTextArea;
    @FXML
    private VBox mediaVBox;
    @FXML
    private VBox videoVBox;

    public VBox getPictureVBox() {
        return pictureVBox;
    }

    @FXML
    private VBox pictureVBox;
    @FXML
    private Button insertPictureButton;
    @FXML
    private Button insertVideoButton;
    private Vector<GUI32ChoiceController> choicesControllers = new Vector<>();
    private GUI32MediaPaneController gui32MediaPaneController = new GUI32MediaPaneController();
    private GUI32PicturePaneController gui32PicturePaneController = new GUI32PicturePaneController();

    public Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Button getSaveChanges_btn() {
        return saveChanges_btn;
    }
    public Button getSaveAndContinue_btn() {
        return saveAndContinue_btn;
    }
    public void getComboBox() {
        String queryCategoryName = ""+
                "SELECT CONCAT( REPEAT(' ', COUNT(parent.name) - 1),' ' ,node.name,' (', " +
                "(SELECT COUNT(question_id) FROM question " +
                "WHERE question.category_id=node.category_id),') '  ) AS name " +
                "FROM category AS node,category AS parent " +
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
            comboBox.setItems(categoryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                HBox hBox = loader.load();
                GUI32ChoiceController controller=loader.getController();
                choicesControllers.add(controller);
                choicesLayout.getChildren().add(hBox);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void setQuestionMediaView(String link){
        FXMLLoader loader= new FXMLLoader();
        loader.setLocation(getClass().getResource("/resources/Fxml/GUI32MediaPane.fxml"));
        try {
            VBox vBox = loader.load();
            gui32MediaPaneController = loader.getController();
            videoVBox.getChildren().add(vBox);
        }catch (Exception e){
            e.printStackTrace();
        }
        gui32MediaPaneController.setMediaView(link);
        gui32MediaPaneController.setParentController(this);
    }
    public void setQuestionPictureView(InputStream inputStream){
        FXMLLoader loader= new FXMLLoader();
        loader.setLocation(getClass().getResource("/resources/Fxml/GUI32PicturePane.fxml"));
        try {
            VBox vBox = loader.load();
            gui32PicturePaneController = loader.getController();
            pictureVBox.getChildren().add(vBox);
        }catch (Exception e){
            e.printStackTrace();
        }
        gui32PicturePaneController.setParentController(this);
        gui32PicturePaneController.setImageView(inputStream);
    }

    public void insertVideo(){
        removeVideo();
        FXMLLoader loader= new FXMLLoader();
        loader.setLocation(getClass().getResource("/resources/Fxml/GUI32MediaPane.fxml"));
        try {
            VBox vBox = loader.load();
            gui32MediaPaneController = loader.getController();
            videoVBox.getChildren().add(vBox);
        }catch (Exception e){
            e.printStackTrace();
        }
        gui32MediaPaneController.setMediaView();
        gui32MediaPaneController.setParentController(this);
    }
    public void removeVideo(){
        videoVBox.getChildren().clear();
    }

    public void insertPicture(){
        FXMLLoader loader= new FXMLLoader();
        loader.setLocation(getClass().getResource("/resources/Fxml/GUI32PicturePane.fxml"));
        try {
            VBox vBox = loader.load();
            gui32PicturePaneController = loader.getController();
            pictureVBox.getChildren().add(vBox);
        }catch (Exception e){
            e.printStackTrace();
        }
        gui32PicturePaneController.setImageView();
        gui32PicturePaneController.setParentController(this);
    }
    public void removePicture(){
        pictureVBox.getChildren().clear();
    }

    //Insert Question into Database
    public void insertQuestion(){
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            String categoryName = comboBox.getValue().trim();

            categoryName = categoryName.substring(0,categoryName.indexOf('(') - 1);
            ResultSet categorySet = statement.executeQuery("select * from test.category " +
                    "where name = '" + categoryName + "';");
            categorySet.next();
            Integer categoryId = Integer.parseInt(categorySet.getString("category_id"));

            statement.executeUpdate("insert into question (name,text,mark,category_id)"
                + "value ('" + questionNameTextField.getText() + "','"
                + questionTextTextArea.getText() + "','"
                + Integer.parseInt(questionMarkTextField.getText()) + "','"
                + categoryId + "');" );

            ResultSet questionIdSet = statement.executeQuery("select last_insert_id();");
            questionIdSet.next();
            Integer questionId= Integer.parseInt(questionIdSet.getString("last_insert_id()"));
            System.out.println("Inserted Successfully, Question ID: " + questionId);
            if(pictureVBox.getChildren().size()>=1){
                gui32PicturePaneController.insertPicture(questionId);
            }
            else{
                gui32PicturePaneController.removePicture();
                gui32PicturePaneController.insertPicture(questionId);
            }
            gui32MediaPaneController.insertVideo(questionId);

            for(int i=0;i<choicesControllers.size();i++){
                choicesControllers.get(i).insertChoice(questionId);
            }
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //Alter Question when press Edit button in GUI21questionTab.fxml
    public void alterQuestion(Integer questionId){
        try {
            Connection connection = getConnection();

            Statement statement = connection.createStatement();
            String categoryName = comboBox.getValue().trim();
            categoryName = categoryName.substring(0,categoryName.indexOf('(')-1);
            ResultSet categorySet = statement.executeQuery("select * from test.category " +
                    "where name = '" + categoryName + "';");
            categorySet.next();
            Integer categoryId = Integer.parseInt(categorySet.getString("category_id"));

            String query = "UPDATE test.question SET category_id=?, name=?, text=?, mark=? WHERE question_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,categoryId);
            preparedStatement.setString(2,questionNameTextField.getText());
            preparedStatement.setString(3,questionTextTextArea.getText());
            preparedStatement.setInt(4,Integer.parseInt(questionMarkTextField.getText()));
            preparedStatement.setInt(5,questionId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            gui32MediaPaneController.insertVideo(questionId);
            if(pictureVBox.getChildren().size()>=1){
                gui32PicturePaneController.insertPicture(questionId);
            }
            else{
                gui32PicturePaneController.removePicture();
                gui32PicturePaneController.insertPicture(questionId);
            }

            //Alter choices by removing old choices in Database then insert new ones
            choicesControllers.get(0).removeChoice(questionId);
            for(int i=0;i<choicesControllers.size();i++){
                choicesControllers.get(i).insertChoice(questionId);
            }
            System.out.println("Modified Successfully, Question ID: " + questionId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setCategory(String categoryString){
        ObservableList<String> categoryNames=comboBox.getItems();
        int index=-1;
        for(int i=0;i<categoryNames.size();i++){
            String categoryName = new String(categoryNames.get(i).trim());
            categoryName=categoryName.substring(0,categoryName.indexOf('(')-1);
            if (categoryString.equals(categoryName)){
                index=i;
            }
        }
        comboBox.getSelectionModel().select(index);
    }
    public void setQuestionNameTextField(String questionName){
        this.questionNameTextField.setText(questionName);
    }
    public void setQuestionTextTextArea(String questionText){
        this.questionTextTextArea.setText(questionText);
    }
    public void setQuestionMarkTextField(Integer questionMark){
        this.questionMarkTextField.setText(questionMark.toString());
    }
    public Vector<GUI32ChoiceController> getChoicesControllers() {
        return choicesControllers;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getComboBox();
        insertKMoreChoices(2);
        insertVideoButton.setOnAction(event -> insertVideo());
        insertPictureButton.setOnAction(event -> insertPicture());
        cancel_btn.setOnAction(event -> showGUI21());
        blanks_btn.setOnAction(event -> insertKMoreChoices(3));
        saveChanges_btn.setOnAction(event -> {
            insertQuestion();
            showGUI21();
        });
        saveAndContinue_btn.setOnAction(event -> {
            insertQuestion();
//            saveAndContinue_btn.setOnAction(event -> alterQuestion(questionID));
        });
    }
}