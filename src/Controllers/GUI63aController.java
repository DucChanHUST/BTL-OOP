package Controllers;

import Models.Model;
import Models.QQuestion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

public class GUI63aController extends GUI63aItemController implements Initializable {
    @FXML
    private CheckBox alsoShow_ckb;
    @FXML
    private CheckBox selectAll_ckb;
    @FXML
    private VBox listQues;
    @FXML
    private Button add_btn;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Button close_btn;
    @FXML
    private Label switch_lbl;
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
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
                String item = resultSet.getString("name");
                categoryName.add(item);
            }
            comboBox.setItems(categoryName);
        } catch (Exception e) {e.printStackTrace();}
    }
    public void showGUI62a(){
        Stage stage = (Stage) switch_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showGUI62a();
    }
    public void showGUI62a_add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Fxml/GUI62a.fxml"));
            Parent root = loader.load();
            GUI62aController gui62aController = loader.getController();
            Vector<String> quesList = prepareToAdd;
            gui62aController.insertQuesToQuiz(quesList);

            Stage stage = (Stage) switch_lbl.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showGUI62a();
        } catch (Exception e) {e.printStackTrace();}
    }

    static String query;
    private List<QQuestion> qQuestionList(String queryy){
        Connection connection = getConnection();
        List<QQuestion> list = new ArrayList<>();
        QQuestion qQuestion;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryy);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                qQuestion = new QQuestion();
                qQuestion.setName(resultSet.getString("name"));
                qQuestion.setText(resultSet.getString("text"));
                qQuestion.setQuestion_id(resultSet.getInt("question_id"));
                list.add(qQuestion);
            }
        } catch (Exception e) {e.printStackTrace();}
        return list;
    }

    private List<QQuestion> sQuestionList(String dad){
        Connection connection = getConnection();
        List<QQuestion> list = new ArrayList<>();
        QQuestion qQuestion;
        try {
            CallableStatement callableStatement = connection.prepareCall("{call subCategory(?)}");
            callableStatement.setString(1, dad);
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();
            while (resultSet.next()) {
                qQuestion = new QQuestion();
                qQuestion.setName(resultSet.getString("name"));
                qQuestion.setText(resultSet.getString("text"));
                qQuestion.setQuestion_id(resultSet.getInt("question_id"));
                list.add(qQuestion);
            }
        } catch (Exception e) {e.printStackTrace();}
        return list;
    }

    private String categoryName;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getComboBox();
        close_btn.setOnAction(event -> {
            showGUI62a();
            prepareToAdd.clear();
        });
        add_btn.setOnAction(event -> {
            showGUI62a_add();
            prepareToAdd.clear();
        });
        List<QQuestion> qQuestionList = new ArrayList<>(qQuestionList("SELECT * FROM question"));
        for (int i = 0; i < qQuestionList.size(); i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
            try {
                HBox hBox = loader.load();
                GUI63aItemController itemController = loader.getController();
                itemController.setData(qQuestionList.get(i));
                listQues.getChildren().add(hBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        comboBox.setOnAction(event -> {
            listQues.getChildren().clear();
            categoryName = comboBox.getValue().trim();
            categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
            query = "SELECT qs.name, qs.text, qs.question_id FROM question qs, category ct " +
                    "WHERE qs.category_id=ct.category_id and ct.name = '" + categoryName + "';";
            List<QQuestion> qQuestionList2 = new ArrayList<>(qQuestionList(query));
            for (int i = 0; i < qQuestionList2.size(); i++) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                try {
                    HBox hBox = loader.load();
                    GUI63aItemController itemController = loader.getController();
                    itemController.setData(qQuestionList2.get(i));
                    listQues.getChildren().add(hBox);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        alsoShow_ckb.setOnAction(event -> {
            if (alsoShow_ckb.isSelected()) {
                System.out.println("also show questions from sub categories");
                listQues.getChildren().clear();
                categoryName = comboBox.getValue().trim();
                categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                List<QQuestion> qQuestionList3 = new ArrayList<>(sQuestionList(categoryName));
                for (int i = 0; i < qQuestionList3.size(); i++) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                    HBox hBox;
                    try {
                        hBox = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    GUI63aItemController itemController = loader.getController();
                    itemController.setData(qQuestionList3.get(i));
                    listQues.getChildren().add(hBox);
                }
            } else {
                System.out.println("don't show questions from sub categories");
                listQues.getChildren().clear();
                categoryName = comboBox.getValue().trim();
                categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                query = "SELECT qs.name, qs.text, qs.question_id FROM question qs, category ct " +
                        "WHERE qs.category_id=ct.category_id and ct.name = '" + categoryName + "';";
                List<QQuestion> qQuestionList2 = new ArrayList<>(qQuestionList(query));
                for (int i = 0; i < qQuestionList2.size(); i++) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                    HBox hBox;
                    try {
                        hBox = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    GUI63aItemController itemController = loader.getController();
                    itemController.setData(qQuestionList2.get(i));
                    listQues.getChildren().add(hBox);
                }
            }
        });
        selectAll_ckb.setOnAction(event ->
        {
            if (selectAll_ckb.isSelected()) {
                if (alsoShow_ckb.isSelected()) {
                    System.out.println("also show questions from sub categories");
                    listQues.getChildren().clear();
                    categoryName = comboBox.getValue().trim();
                    categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                    List<QQuestion> qQuestionList3 = new ArrayList<>(sQuestionList(categoryName));
                    for (int i = 0; i < qQuestionList3.size(); i++) {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                        HBox hBox;
                        try {
                            hBox = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        GUI63aItemController itemController = loader.getController();
                        itemController.setData(qQuestionList3.get(i));
                        itemController.question_cbx.setSelected(true);
                        itemController.setQuestion_cbx();
                        listQues.getChildren().add(hBox);
                    }
                } else {
                    listQues.getChildren().clear();
                    categoryName = comboBox.getValue().trim();
                    categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                    query = "SELECT qs.name, qs.text, qs.question_id FROM question qs, category ct " +
                            "WHERE qs.category_id=ct.category_id and ct.name = '" + categoryName + "';";
                    List<QQuestion> qQuestionList2 = new ArrayList<>(qQuestionList(query));
                    for (int i = 0; i < qQuestionList2.size(); i++) {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                        HBox hBox;
                        try {
                            hBox = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        GUI63aItemController itemController = loader.getController();
                        itemController.setData(qQuestionList2.get(i));
                        itemController.question_cbx.setSelected(true);
                        itemController.setQuestion_cbx();
                        listQues.getChildren().add(hBox);
                    }
                }
            } else {
                if (alsoShow_ckb.isSelected()) {
                    System.out.println("also show questions from sub categories");
                    listQues.getChildren().clear();
                    categoryName = comboBox.getValue().trim();
                    categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                    List<QQuestion> qQuestionList3 = new ArrayList<>(sQuestionList(categoryName));
                    for (int i = 0; i < qQuestionList3.size(); i++) {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                        HBox hBox;
                        try {
                            hBox = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        GUI63aItemController itemController = loader.getController();
                        itemController.setData(qQuestionList3.get(i));
                        itemController.question_cbx.setSelected(false);
                        listQues.getChildren().add(hBox);
                    }
                } else {
                    listQues.getChildren().clear();
                    categoryName = comboBox.getValue().trim();
                    categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                    query = "SELECT qs.name, qs.text, qs.question_id FROM question qs, category ct " +
                            "WHERE qs.category_id=ct.category_id and ct.name = '" + categoryName + "';";
                    List<QQuestion> qQuestionList2 = new ArrayList<>(qQuestionList(query));
                    for (int i = 0; i < qQuestionList2.size(); i++) {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/resources/Fxml/GUI63aItem.fxml"));
                        HBox hBox;
                        try {
                            hBox = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        GUI63aItemController itemController = loader.getController();
                        itemController.setData(qQuestionList2.get(i));
                        itemController.question_cbx.setSelected(false);
                        listQues.getChildren().add(hBox);
                    }
                }
            }
        });
    }
}