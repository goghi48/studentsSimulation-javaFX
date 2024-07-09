package com.example.daiquiri.Student;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Student implements Serializable {
    private static final Set<Integer> generatedIds = new HashSet<>();
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private double angle;
    private final DoubleProperty coordX = new SimpleDoubleProperty();
    private final DoubleProperty coordY = new SimpleDoubleProperty();
    protected int imgWidth;
    protected int imgHeight;
    transient protected Pane simulationPane;

    transient protected ImageView imageView;
    public Student() {
        generateUniqueId();
        this.coordX.addListener((obs, oldX, newX) -> imageView.setX(newX.doubleValue()));
        this.coordY.addListener((obs, oldY, newY) -> imageView.setY(newY.doubleValue()));
    }

    private void generateUniqueId() {
        do {
            id = new Random().nextInt(1000); // Генерация случайного числа
        } while (generatedIds.contains(this.id));
        generatedIds.add(this.id);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getCoordX() {
        return coordX.getValue();
    }
    public double getCoordY() {
        return coordY.getValue();
    }
    //angle
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public double getAngle() {
        return angle;
    }
    public void setImgWidth(int width) {
        this.imgWidth = width;
    }
    public int getImgWidth() {
        return imgWidth;
    }
    public void setImgHeight(int height) {
        this.imgHeight = height;
    }
    public int getImgHeight() {
        return imgHeight;
    }
    public void moveTo(double x, double y) {
        coordX.set(x);
        coordY.set(y);
    }
    public ImageView getImageView() {
        return imageView;
    }
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Сначала выполняем обычную сериализацию
        out.writeDouble(coordX.getValue()); // Затем записываем координаты
        out.writeDouble(coordY.getValue());
        out.writeInt(id);
    }
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        coordX.set(in.readDouble());
        coordY.set(in.readDouble());
        id = in.readInt();
    }
}