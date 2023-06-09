package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.DragEvent;

import java.sql.*;
import java.util.Vector;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Button;
import Models.Model;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

public class GUI21importTabController implements Initializable {
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ImageView image;
    @FXML 
    private Button button;
    @FXML 
    private Button import_btn;
    
 // Liên kết với cơ sở dữ liệu :))
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
    //---------------
    class Question{
    	public String questionText;
    	public String questionAnswer;
    	public Vector<String> choiceList = new Vector<>();
    }

    public Vector<Question> question_List = new Vector<>();
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
    private static int category_id;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getComboBox();
        comboBox.setOnAction(event -> {
            String categoryName = comboBox.getValue().trim();
            categoryName = categoryName.substring(0, categoryName.indexOf('(') - 1);
            Connection connection = getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("" +
                        "SELECT category_id FROM category WHERE name = '" + categoryName + "';");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                category_id = resultSet.getInt("category_id");
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        Label label = new Label();
        // Xử lý sự kiện khi bắt đầu kéo
        image.setOnDragDetected(event -> {
            Dragboard dragboard = image.startDragAndDrop(TransferMode.COPY);
            dragboard.setDragView(image.snapshot(null, null));
            event.consume();
        });
        // Xử lý sự kiện khi có file được kéo vào
        image.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        // Xử lý sự kiện khi file được thả vào
        image.setOnDragDropped((DragEvent event) -> {
        	int num_line = 0;
        	int num_qs = 0;
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasFiles()) {
                String filePath = dragboard.getFiles().get(0).getAbsolutePath();
                label.setText("File path: " + filePath);
                try {
                    Image img = new Image("resources//Image//file.png");
                    image.setImage(img);
                    if (checkAndAddAikenStructure(filePath, num_line, num_qs)) {
                        success = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            // Tạo một massageBox để thông báo File ó đúng cấu trúc Aiken hay không
            if (!success) {
            	import_btn.setOnAction(e->{
            		 Alert alert = new Alert(Alert.AlertType.INFORMATION);
                     alert.setTitle("Thông báo");
                     alert.setHeaderText(null);
                     alert.setContentText("Import File thất bại!");
                     alert.showAndWait();
                     
                     Stage stage = (Stage)import_btn.getScene().getWindow();
                     Model.getInstance().getViewFactory().closeStage(stage);
                     Model.getInstance().getViewFactory().showGUI21();
                     
            	});
            } else {
                import_btn.setOnAction(e->{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Import File thành công!");
                    alert.showAndWait();
                   
                	Stage stage = (Stage)import_btn.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    Model.getInstance().getViewFactory().showGUI21();
                    
                    for(int i = 0; i < question_List.size(); i++) {
                    	System.out.println(question_List.elementAt(i).questionText);
                    	for(int j = 0; j < question_List.elementAt(i).choiceList.size(); j++) {
                    		System.out.println(question_List.elementAt(i).choiceList.elementAt(j));
                    	}
                    	System.out.println(question_List.elementAt(i).questionAnswer);
                    }

                    try {
                        Connection connection = getConnection();
                        Statement statement = connection.createStatement();
                        for (int i = 0; i < question_List.size(); i++) {
                            statement.executeUpdate("INSERT INTO question(category_id, mark,name, text) VALUES (" + category_id + ", 1, '"
                                    + question_List.elementAt(i).questionText +"','" + question_List.elementAt(i).questionText + "');");
                            statement.executeUpdate("set @id = LAST_INSERT_ID();");
                            for (int j = 0; j < question_List.elementAt(i).choiceList.size(); j++) {
                                if (question_List.elementAt(i).choiceList.elementAt(j).charAt(0) == question_List.elementAt(i).questionAnswer.charAt(8))
                                    statement.executeUpdate("INSERT INTO choice(question_id, grade, content) VALUES(@id, 100, '"
                                            + question_List.elementAt(i).choiceList.elementAt(j).substring(3) +"');");
                                else
                                    statement.executeUpdate("INSERT INTO choice(question_id, grade, content) VALUES(@id, 0, '"
                                            + question_List.elementAt(i).choiceList.elementAt(j).substring(3) +"');");
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
           	    });
            }
            event.consume();
            event.setDropCompleted(success);
        });
 
        button.setOnAction(e -> {
        	String filePath = null;
        	FileChooser fileChooser = new FileChooser();

            // Đặt tiêu đề của cửa sổ FileChooser
            fileChooser.setTitle("Open File");

            // Hiển thị cửa sổ FileChooser và lấy đường dẫn đến file đã chọn
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                filePath = selectedFile.getAbsolutePath();
            }
            try {
            	int num_line = 0;
            	int num_qs = 0;
                if (checkAndAddAikenStructure(filePath, num_line, num_qs)) {
                	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Import File thành công!");
                    alert.showAndWait();
                 
                	Stage stage = (Stage)button.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    Model.getInstance().getViewFactory().showGUI21();

                    try {
                        Connection connection = getConnection();
                        Statement statement = connection.createStatement();
                        for (int i = 0; i < question_List.size(); i++) {
                            statement.executeUpdate("INSERT INTO question(category_id, mark,name, text) VALUES (" + category_id + ", 1, '" + question_List.elementAt(i).questionText +"','" + question_List.elementAt(i).questionText + "');");
                            statement.executeUpdate("set @id = LAST_INSERT_ID();");
                            for (int j = 0; j < question_List.elementAt(i).choiceList.size(); j++) {
                                if (question_List.elementAt(i).choiceList.elementAt(j).charAt(0) == question_List.elementAt(i).questionAnswer.charAt(8))
                                    statement.executeUpdate("INSERT INTO choice(question_id, grade, content) VALUES(@id, 100, '" + question_List.elementAt(i).choiceList.elementAt(j).substring(3) +"');");
                                else
                                    statement.executeUpdate("INSERT INTO choice(question_id, grade, content) VALUES(@id, 0, '" + question_List.elementAt(i).choiceList.elementAt(j).substring(3) +"');");
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }else {
                	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText(null);
                    alert.setContentText("File không đúng cú pháp Aiken!");
                    alert.showAndWait();
                    
                    Stage stage = (Stage)button.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    Model.getInstance().getViewFactory().showGUI21();
                }
            } catch (IOException m) {
                m.printStackTrace();
            }
        });
    }
    // Check Aiken format
    public boolean checkAndAddAikenStructure(String filePath, int num_line, int num_qs) throws IOException {
    	try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = "";
            Question qs = new Question();
            
            // Kiểm tra các dòng tiếp theo theo định dạng Aiken
            int count = 0;
            num_line = 1;
            num_qs = 0;
     
            while ((line = reader.readLine()) != null) {
                if (count == 0) {
                	num_line++;
                	num_qs++;
                	qs.questionText = line; 
                    count++;
                } else if (count == 1) {
                    if (line.startsWith("A. ") || line.startsWith("B. ") || line.startsWith("C. ")
                            || line.startsWith("D. ") || line.startsWith("E. ") || line.startsWith("F. ")
                            || line.startsWith("G. ") || line.startsWith("H. ") || line.startsWith("I. ")
                            || line.startsWith("K. ") || line.startsWith("A) ") || line.startsWith("B) ")
                            || line.startsWith("C) ") || line.startsWith("D) ") || line.startsWith("E) ")
                            || line.startsWith("F) ") || line.startsWith("G) ") || line.startsWith("H) ")
                            || line.startsWith("I) ") || line.startsWith("K) ")){
                    	num_line++;
                    	qs.choiceList.add(line);
                    	
                    } else {
                        if (line.startsWith("ANSWER: ")) {
                            // TODO
                        	qs.questionAnswer = line;
                        	question_List.add(qs);
                        	qs = new Question();
                        	num_line++;
                            count++;
                            
                        } else {
                        	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Thông báo");
                            alert.setHeaderText(null);
                            alert.setContentText("ERROR AT LINE " + num_line);
                            alert.showAndWait();
                            return false;
                        }
                    }
                } else if (count == 2) {
                    if (!line.isEmpty()) {
                    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText(null);
                        alert.setContentText("ERROR AT LINE " + num_line);
                        alert.showAndWait();
                        return false;
                    } else {
                    	num_line++;
                        count = 0;
                    }
                }
            }

            // Đã kiểm tra qua tất cả các điều kiện, file đúng định dạng Aiken
            System.out.println("Success");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("SUCCESS: " + num_qs + " Questions");
            alert.showAndWait();
            return true;
        } catch (IOException e) {
            System.out.println("Đã xảy ra lỗi khi đọc file: " + e.getMessage());
            return false;
        }
    }
}