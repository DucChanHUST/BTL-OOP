package Models;


import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Choice {
    private String choiceText;
    private Integer choiceId;
    private Image choiceImage;
    private Double choiceGrade;
    public List<Double> listGrade=new ArrayList<>();

    public Choice(){
        double[] grade={100,90,83.33333,80,75,70,66.66666,60,50,40,33.33333,30,25,20,16.66666,14.28571,12.5,11.11111,10,5};
        for(int i=0;i<grade.length;i++){
            listGrade.add(grade[i]);
        }
        listGrade.add((double) 0);
        for(int i=grade.length-1;i>=0;i--){
            listGrade.add(-grade[i]);
        }
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public Integer getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }

    public Image getChoiceImage() {
        return choiceImage;
    }

    public void setChoiceImage(Image choiceImage) {
        this.choiceImage = choiceImage;
    }

    public Double getChoiceGrade() {
        return choiceGrade;
    }

    public void setChoiceGrade(Double choiceGrade) {
        this.choiceGrade = choiceGrade;
    }
}
