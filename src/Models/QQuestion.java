package Models;

import javafx.scene.image.Image;

public class QQuestion {
    private int question_id;
    private int category_id;
    private String name;
    private String text;
    private int mark;
    private Image image;
    private String mediaLink;

    public int getCategory_id() {
        return category_id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getMark() {
        return mark;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }
}
