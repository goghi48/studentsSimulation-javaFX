package com.example.daiquiri.AI;

import com.example.daiquiri.Student.Student;
import com.example.daiquiri.Student.StudentBoy;
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

public class BoyAI extends BaseAI {

    private List<Student> studentList;
    private int changeDirectionTime;
    private final Pane simulationPane;
    private boolean isRunning;
    public BoyAI(int changeDirectionTime, int speed, List<Student> studentList, Pane simulationPane) {
        super(speed);
        this.changeDirectionTime = changeDirectionTime;
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
            if (student instanceof StudentBoy) {
                StudentBoy studentBoy = (StudentBoy) student;
                long currentTime = System.currentTimeMillis();
                if(!studentBoy.isInit){
                    studentBoy.isInit = true;
                    studentBoy.setAngle(Math.toRadians(ThreadLocalRandom.current().nextDouble(0, 360)));
                    studentBoy.lastMoveTime = System.currentTimeMillis();
                }
                if (currentTime - studentBoy.lastMoveTime >= (changeDirectionTime * 1000)) {
                    studentBoy.setAngle(Math.toRadians(ThreadLocalRandom.current().nextDouble(0, 360)));
                    studentBoy.lastMoveTime = System.currentTimeMillis();
                }
                double currentX = studentBoy.getImageView().getX();
                double currentY = studentBoy.getImageView().getY();
                double newX = currentX + (speed * Math.cos(studentBoy.getAngle()) / 10);
                double newY = currentY + (speed * Math.sin(studentBoy.getAngle()) / 10);
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
                studentBoy.moveTo(newX, newY);
                // studentBoy.lastMoveTime = System.currentTimeMillis();

/*                Duration duration = Duration.seconds(changeDirectionTime); // Продолжительность анимации (1 секунда)
                KeyValue keyValueX = new KeyValue(studentBoy.getImageView().xProperty(), newX);
                KeyValue keyValueY = new KeyValue(studentBoy.getImageView().yProperty(), newY);
                KeyFrame keyFrame = new KeyFrame(duration, keyValueX, keyValueY);
                Timeline timeline = new Timeline(keyFrame);
                timeline.setCycleCount(1); // Однократное выполнение анимации
                timeline.play();*/
            }
        }
    }
}
