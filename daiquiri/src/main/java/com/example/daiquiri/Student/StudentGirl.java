package com.example.daiquiri.Student;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class StudentGirl extends Student implements IBehaviour {
    transient private static Image girlImage; // Статическая переменная для хранения изображения
    private long birthTime;
    public double centerX;
    public double centerY;
    public boolean isCentred = false;

    public StudentGirl(Pane simulationPane, int imgWidth, int imgHeight) {
        super();
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        if (girlImage == null) { // Проверяем, было ли изображение уже загружено
            try {
                girlImage = new Image(new FileInputStream("src/main/resources/com/example/daiquiri/girl.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        imageView = new ImageView(girlImage); // Используем статическое изображение
        imageView.setFitWidth(imgWidth);
        imageView.setFitHeight(imgHeight);
/*        double maxX = simulationPane.getWidth() - imgWidth;
        double maxY = simulationPane.getHeight() - imgHeight;
        coordX = ThreadLocalRandom.current().nextDouble(0, maxX);
        coordY = ThreadLocalRandom.current().nextDouble(0, maxY);
        imageView.setX(coordX);
        imageView.setY(coordY);*/
    }
}

