package Controllers;

import Models.Model;
import Models.QQuestion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.net.URL;
import java.security.cert.PolicyNode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import static Controllers.GUI63aItemController.prepareToAdd;

public class GUI65Controller implements Initializable {
    private final int itemPerPage = 10;

    @FXML
    private Pagination pagination;
    @FXML
    private CheckBox selectAll_ckb;
    private VBox listQues;
    @FXML
    private Label switch_lbl;
    @FXML
    private Button close_btn;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ComboBox<Integer> comboBox1;
    @FXML
    private TableColumn<QQuestion, String> questionColumn;
    @FXML
    private TableView<QQuestion> table;
    @FXML
    private CheckBox include_ckb;
    @FXML
    private Button add_btn;
    private String selectedCategory;
    public static String quiz_id_data;
    private final ObservableList<QQuestion> questionList = FXCollections.observableArrayList();

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
        String queryCategoryName = "" +
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

    public void getComboBox1() {
        comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String selectedItem = newValue.toString();
            int max = Integer.parseInt(selectedItem.substring(selectedItem.lastIndexOf("(") + 1, selectedItem.lastIndexOf(")")));
            comboBox1.getItems().clear();
            for (int i = 1; i <= max; i++) {
                comboBox1.getItems().add(i);
            }
            comboBox1.setValue(max);
            selectedCategory = newValue;
            loadQuestion();
        });
    }

    public void showGUI62a() {
        Stage stage = (Stage) switch_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showGUI62a();
    }

    public void showGUI62a_add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Fxml/GUI62a.fxml"));
            Parent root = loader.load();
            GUI62aController gui62aController = loader.getController();
            gui62aController.insertQuesToQuiz(new Vector<>(prepareToAdd));

            Stage stage = (Stage) switch_lbl.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showGUI62a();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static String query;

    private List<QQuestion> qQuestionList(String queryy) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<QQuestion> sQuestionList(String dad) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void loadQuestion() {
        int count = 0;
        try {
            questionList.clear();
            String query = "SELECT q.name FROM question q, category c WHERE q.category_id = c.category_id AND c.name = ?";
            String selectedCategory2 = selectedCategory.substring(0, selectedCategory.indexOf('(') - 1).trim();
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedCategory2);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String questionName = resultSet.getString("name");
                System.out.println(questionName);
                QQuestion question = new QQuestion();
                question.setName(questionName);
                questionList.add(question);
            }
            questionColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            count = questionList.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.setItems(questionList);
        table.refresh();
        pagination.setPageCount(count / itemPerPage + 1);
        pagination.setPageFactory(this::createPage);
    }

    private String categoryName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getComboBox();
        getComboBox1();
        int count = 0;
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM question");
            resultSet.first();
            count = resultSet.getInt(1);
            count = Math.max(count, itemPerPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        comboBox.setOnAction(event -> {
            selectedCategory = comboBox.getValue();
            if (comboBox1.getValue() != null) {
                loadQuestion();
            }
        });

        include_ckb.setOnAction(event -> {
            if (include_ckb.isSelected()) {
                System.out.println("Include questions from sub category too");
                table.getItems().clear();
                categoryName = comboBox.getValue().trim();
                categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                List<QQuestion> qQuestionList = new ArrayList<>(sQuestionList(categoryName));
                for (QQuestion question : qQuestionList) {
                    table.getItems().add(question);
                }
            } else {
                System.out.println("Don't show questions from sub category too");
                table.getItems().clear();
                categoryName = comboBox.getValue().trim();
                categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
                String query = "SELECT qs.name, qs.text, qs.question_id FROM question qs, category ct " +
                        "WHERE qs.category_id=ct.category_id and ct.name = '" + categoryName + "';";
                List<QQuestion> qQuestionList = new ArrayList<>(qQuestionList(query));
                for (QQuestion question : qQuestionList) {
                    table.getItems().add(question);
                }
            }
        });
        close_btn.setOnAction(event -> {
            showGUI62a();
        });

        add_btn.setOnAction(event -> {
            ObservableList<QQuestion> items = table.getItems();
            System.out.println(table);

        });


    }
    private Node createPage(int pageIndex) {
        int from = pageIndex * itemPerPage;
        int to = Math.min(from + itemPerPage, questionList.size());
        ObservableList<QQuestion> sublist = FXCollections.observableArrayList(questionList.subList(from, to));
        table.setItems(sublist);
        if (pageIndex >= pagination.getPageCount()) {
            return null;
        }
        return table;
    }
}