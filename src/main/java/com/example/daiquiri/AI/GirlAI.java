package com.example.daiquiri.AI;

import com.example.daiquiri.Student.Student;
import com.example.daiquiri.Student.StudentBoy;
import com.example.daiquiri.Student.StudentGirl;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GirlAI extends BaseAI {
    private List<Student> studentList;
    private final Pane simulationPane;
    private boolean isRunning;
    private int radius;
    private int currentAngle = 0;
    public GirlAI(int radius, int speed, List<Student> studentList, Pane simulationPane) {
        super(speed);
        this.radius = radius;
        this.studentList = studentList;
        this.simulationPane = simulationPane;
        this.isRunning = true;
    }

    public void stopAI() {
        isRunning = false;
    }

    public void resumeAI() {
        isRunning = true;
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            move();
            try {
                Thread.sleep(100); // Пауза между шагами
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void move() {
        for (Student student : studentList) {
            if (student instanceof StudentGirl) {
                StudentGirl studentGirl = (StudentGirl) student;
                 if(!studentGirl.isCentred){
                     studentGirl.isCentred = true;
                     //studentGirl.angle = ThreadLocalRandom.current().nextDouble(0, 360);
                     studentGirl.setAngle(Math.toRadians(ThreadLocalRandom.current().nextDouble(0, 360)));
                     studentGirl.centerX = studentGirl.getImageView().getX() + radius * Math.cos(studentGirl.getAngle());
                     studentGirl.centerY = studentGirl.getImageView().getY() + radius * Math.sin(studentGirl.getAngle());
                 }
                double currentX = studentGirl.getImageView().getX();
                double currentY = studentGirl.getImageView().getY();
                double newX = studentGirl.centerX + radius * -Math.cos(studentGirl.getAngle());
                double newY = studentGirl.centerY + radius * -Math.sin(studentGirl.getAngle());
                double objectWidth = student.getImageView().getFitWidth();
                double objectHeight = student.getImageView().getFitHeight();
                if (newX < 0) {
                    newX = 0;
                } else if (newX > simulationPane.getWidth() - objectWidth) {
                    newX = simulationPane.getWidth() - objectWidth;
                }

                if (newY < 0) {
                    newY = 0;
                } else if (newY > simulationPane.getHeight() - objectHeight) {
                    newY = simulationPane.getHeight() - objectHeight;
                }
                studentGirl.moveTo(newX, newY);
                studentGirl.setAngle(studentGirl.getAngle() + 0.001 * speed);
            }
        }
    }
}
