package com.example.daiquiri.Student;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ThreadLocalRandom;

public class StudentBoy extends Student implements IBehaviour {
    transient private static Image boyImage;
    private long birthTime;
    public long lastMoveTime;
    public boolean isInit = false;
    public StudentBoy(Pane simulationPane, int imgWidth, int imgHeight) {
        super();
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        if (boyImage == null) { // Проверяем, было ли изображение уже загружено
            try {
                boyImage = new Image(new FileInputStream("src/main/resources/com/example/daiquiri/boy.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        imageView = new ImageView(boyImage); // Используем статическое изображение
        imageView.setFitWidth(imgWidth);
        imageView.setFitHeight(imgHeight);
/*        double maxX = simulationPane.getWidth() - imgWidth;
        double maxY = simulationPane.getHeight() - imgHeight;
        coordX = ThreadLocalRandom.current().nextDouble(0, maxX);
        coordY = ThreadLocalRandom.current().nextDouble(0, maxY);
        imageView.setX(coordX);
        imageView.setY(coordY);*/
    }

    public void smoothMoveTo(double x, double y, int changeDirectionTime) {
        lastMoveTime = System.currentTimeMillis();
        Duration duration = Duration.seconds(changeDirectionTime); // Продолжительность анимации (1 секунда)
        KeyValue keyValueX = new KeyValue(this.getImageView().xProperty(), x);
        KeyValue keyValueY = new KeyValue(this.getImageView().yProperty(), y);
        KeyFrame keyFrame = new KeyFrame(duration, keyValueX, keyValueY);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(1); // Однократное выполнение анимации
        timeline.play();
    }
}
