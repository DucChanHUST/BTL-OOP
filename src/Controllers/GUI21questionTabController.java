package Controllers;

import Models.Model;
import Models.QQuestion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class GUI21questionTabController extends GUI21Controller implements Initializable {
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Label SW_lbl;
    @FXML
    private javafx.scene.control.Button createNewQuest_btn;
    @FXML
    private TableView<QQuestion> tableView;
    @FXML
    private TableColumn<QQuestion, String> tv_actions;
    @FXML
    private TableColumn<QQuestion, String> tv_question;
    @FXML
    private TableColumn<QQuestion, Integer> tv_id;
    @FXML
    private CheckBox alsoShow_ckb;
    public void showGUI32() {
        Stage stage = (Stage)SW_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showGUI32();
    }
    public GUI32Controller showGUI32Edited(){
        Stage stage = (Stage)SW_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Fxml/GUI32.fxml"));
        Model.getInstance().getViewFactory().createStage(loader);

        GUI32Controller gui32Controller = loader.getController();
        return gui32Controller;
    }
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
                String item = (resultSet.getString("name"));
                categoryName.add(item);
            }
            comboBox.setItems(categoryName);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void preloadQuestion(Integer questionID) {
        GUI32Controller gui32Controller = showGUI32Edited();
        gui32Controller.preloadQuestion(questionID);
    }

    private void loadQuestion() {
        try {
            ObservableList<QQuestion> questionsList = FXCollections.observableArrayList();
            String query = "SELECT * FROM question";
            Connection connection = getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while (resultSet.next()) {
                QQuestion question = new QQuestion();
                question.setName(resultSet.getString("name"));
                question.setQuestion_id(Integer.parseInt(resultSet.getString("question_id")));
                question.setCategory_id(Integer.parseInt(resultSet.getString("category_id")));
                question.setText(resultSet.getString("text"));
                question.setMark(Integer.parseInt(resultSet.getString("mark")));
                questionsList.add(question);
            }
            tv_question.setCellValueFactory((new PropertyValueFactory<>("name")));
            tv_id.setCellValueFactory((new PropertyValueFactory<>("question_id")));
            Callback<TableColumn<QQuestion, String>, TableCell<QQuestion, String>> cellFactory = (param) -> {
                final TableCell<QQuestion, String> cell = new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Label edit_lbl = new Label("Edit");
                            edit_lbl.setStyle(
                                    "-fx-text-fill: blue; -fx-font-size: 1em;"
                            );
//                            adding edit function
                            edit_lbl.setOnMouseClicked(event -> {
                                Integer index = tableView.getSelectionModel().getSelectedIndex();
                                Integer questionID = tv_id.getCellData(index);
                                preloadQuestion(questionID);
                            });

                            HBox hBox = new HBox(edit_lbl);
                            setGraphic(hBox);
                            setText(null);
                        }
                    }

                };
                return cell;
            };
            tv_actions.setCellFactory(cellFactory);
            tableView.setItems(questionsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadQuestionSub(){
        try {
            String category_name = comboBox.getValue().trim();
            category_name = category_name.substring(0, category_name.indexOf('(') - 1);
            ObservableList<QQuestion> questionsList = FXCollections.observableArrayList();
            String query = "SELECT qs.name, qs.text, qs.category_id, qs.question_id, qs.mark " +
                    "FROM question qs, category ct " +
                    "WHERE qs.category_id = ct.category_id AND ct.name = '" + category_name + "';";
            Connection connection = getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            while (resultSet.next()) {
                QQuestion question = new QQuestion();
                question.setName(resultSet.getString("name"));
                question.setQuestion_id(Integer.parseInt(resultSet.getString("question_id")));
                question.setCategory_id(Integer.parseInt(resultSet.getString("category_id")));
                question.setText(resultSet.getString("text"));
                question.setMark(Integer.parseInt(resultSet.getString("mark")));
                questionsList.add(question);
            }
            tv_question.setCellValueFactory((new PropertyValueFactory<>("name")));
            tv_id.setCellValueFactory((new PropertyValueFactory<>("question_id")));
            tableView.setItems(questionsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadQuestion();
        getComboBox();
        comboBox.setOnAction(event -> loadQuestionSub());
        createNewQuest_btn.setOnAction(event -> showGUI32());
        alsoShow_ckb.setOnAction(event -> {
            if (alsoShow_ckb.isSelected()) {
                System.out.println("also show questions from sub categories");
                tableView.getItems().clear();
                String categoryName = comboBox.getValue().trim();
                categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                ObservableList<QQuestion> questionsList = FXCollections.observableArrayList();
                Connection connection = getConnection();
                try {
                    CallableStatement callableStatement = connection.prepareCall("{call subCategory(?)}");
                    callableStatement.setString(1, categoryName);
                    callableStatement.execute();
                    ResultSet resultSet = callableStatement.getResultSet();
                    while (resultSet.next()) {
                        QQuestion question = new QQuestion();
                        question.setName(resultSet.getString("name"));
                        question.setText(resultSet.getString("text"));
                        question.setQuestion_id(resultSet.getInt("question_id"));
                        question.setCategory_id(resultSet.getInt("category_id"));
                        questionsList.add(question);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tv_question.setCellValueFactory((new PropertyValueFactory<>("name")));
                tv_id.setCellValueFactory((new PropertyValueFactory<>("question_id")));
                tableView.setItems(questionsList);
            } else {
                System.out.println("don't show questions from sub categories");
                loadQuestionSub();
            }
        });
    }
}