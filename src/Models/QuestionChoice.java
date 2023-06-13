package Models;

import java.util.ArrayList;
import java.util.List;

public class QuestionChoice {
    private String choiceText;
    private Integer choiceId;
    private String choiceImageSource;
    private Double choiceGrade;
    public List<Double> gradeList = new ArrayList<>(){};

    public QuestionChoice(){
        final double[] grade={100,90,500.0/6,80,75,70,400.0/6,60,50,40,200.0/6,30,25,20,100.0/6,100.0/7,100.0/8,100.0/9,10,5};
        for(int i=0;i<grade.length;i++){
            gradeList.add(grade[i]);
        }
        for(int i=grade.length-1;i>=0;i--){
            gradeList.add(-grade[i]);
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

    public String getChoiceImageSource() {
        return choiceImageSource;
    }

    public void setChoiceImageSource(String choiceImageSource) {
        this.choiceImageSource = choiceImageSource;
    }

    public Double getChoiceGrade() {
        return choiceGrade;
    }

    public void setChoiceGrade(Double choiceGrade) {
        this.choiceGrade = choiceGrade;
    }
}
