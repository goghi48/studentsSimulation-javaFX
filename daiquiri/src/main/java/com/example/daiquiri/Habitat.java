package com.example.daiquiri;

import com.example.daiquiri.AI.BoyAI;
import com.example.daiquiri.AI.GirlAI;
import com.example.daiquiri.Student.Student;
import com.example.daiquiri.Student.StudentBoy;
import com.example.daiquiri.Student.StudentGirl;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Habitat {
    private static Habitat instance;
    private final Pane simulationPane;
    private final LinkedList<Student> studentList = new LinkedList<>();
    private final List<Student> students;
    private final TreeMap<Integer, Long> birthTimeMap = new TreeMap<>();
    private final Set<Integer> generatedIds = new HashSet<>();
    private Label timeLabel;
    private int imgWidth = 100;
    private int imgHeight = 100;
    private int boyLifeTime = 10;
    private int girlLifeTime = 10;
    private int boySpawnDelay = 5;
    private int girlSpawnDelay = 5;
    private double boyProbability = 1;
    private double girlProbability = 1;
    private long lastBoySpawnTime = 0;
    private long lastGirlSpawnTime = 0;
    private int boyCount = 0;
    private int girlCount = 0;
    private Duration simulationDuration;
    private Instant simulationStartTime;
    private Instant simulationPauseTime;
    private boolean isTimerVisible = false;
    private Timer simulationTimer;
    private boolean isSimulationRunning = false;
    private CheckBox showInfoCheckBox;
    private BoyAI boyAI;
    private GirlAI girlAI;
    private boolean isBoyAIRunning = false;
    private boolean isGirlAIRunning = false;
    private int movementSpeed = 100;
    private int changeDirectionTime = 3;
    private int radius = 200;
    private boolean isConsoleOpen = false;

    public Habitat(Pane simulationPane, Label timeLabel, CheckBox showInfoCheckBox) {
        this.simulationPane = simulationPane;
        this.students = new ArrayList<>();
        this.timeLabel = timeLabel;
        this.showInfoCheckBox = showInfoCheckBox;

    }
    public void startSimulation() {
        if (!isSimulationRunning) {
            isSimulationRunning = true;
            simulationStartTime = Instant.now();
            lastBoySpawnTime = 0;
            lastGirlSpawnTime = 0;
            simulationTimer = new Timer();
            simulationTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (isSimulationRunning) {
                        long elapsedSeconds = Duration.between(simulationStartTime, Instant.now()).getSeconds();
                        final boolean[] generateBoy = {false}; // Объявление массива для хранения флага
                        final boolean[] generateGirl = {false}; // Объявление массива для хранения флага

                        if (elapsedSeconds - lastBoySpawnTime >= boySpawnDelay) {
                            if (Math.random() < boyProbability) {
                                generateBoy[0] = true;
                            }
                            lastBoySpawnTime = elapsedSeconds;
                        }

                        if (elapsedSeconds - lastGirlSpawnTime >= girlSpawnDelay) {
                            if (Math.random() < girlProbability) {
                                generateGirl[0] = true;
                            }
                            lastGirlSpawnTime = elapsedSeconds;
                        }

                        Platform.runLater(() -> {
                            if (generateBoy[0]) {
                                generateStudentBoy();
                                boyCount++;
                            }
                            if (generateGirl[0]) {
                                generateStudentGirl();
                                girlCount++;
                            }
                            updateTimer();
                            removeExpiredStudents();
                        });
                    }
                }
            }, 0, 1000);
        }
    }
    public boolean stopSimulation() {
        if(isSimulationRunning) {
            isSimulationRunning = false;
            if (showInfoCheckBox.isSelected()) {
                return showAlertWithSimulationInfo();
            } else {
                clearSimulationResult();
                return true;
            }
        }
        return false;
    }
    public boolean getSimulationState(){
        return isSimulationRunning;
    }
    public void clearSimulationResult() {
        simulationTimer.cancel();
        simulationTimer = null;
        boyCount = 0;
        girlCount = 0;
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            simulationPane.getChildren().remove(student.getImageView());
            iterator.remove();
            generatedIds.remove(student.getId());
            birthTimeMap.remove(student.getId());
            synchronized (studentList) {
                studentList.remove(student);
            }
        }
        timeLabel.setText("00:00");
    }
        public void pauseSimulation(){
        simulationPauseTime = Instant.now();
        isSimulationRunning = false;
    }
    public void resumeSimulation(){
        simulationStartTime = simulationStartTime.plus(Duration.between(simulationPauseTime, Instant.now()));
        isSimulationRunning = true;
    }
    private void updateTimer() {  //Обновление времени
        if (isSimulationRunning) {
            Duration duration = Duration.between(simulationStartTime, Instant.now());
            long seconds = duration.getSeconds();
            long minutes = seconds / 60;
            seconds %= 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            timeLabel.setText(formattedTime);
        }
    }
    public void setBoySpawnDelay(int delay) {
        this.boySpawnDelay = delay;
    }
    public void setGirlSpawnDelay(int delay) {
        this.girlSpawnDelay = delay;
    }
    public void setBoyLifeTime(int lifeTime) {
        this.boyLifeTime = lifeTime;
    }
    public void setGirlLifeTime(int lifeTime) {
        this.girlLifeTime = lifeTime;
    }
    public void setBoyProbability(double probability) {
        this.boyProbability = probability;
    }
    public void setGirlProbability(double probability) {
        this.girlProbability = probability;
    }
    public boolean showAlertWithSimulationInfo() {
        pauseSimulation();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("habitat-info.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Информация о симуляции");
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            HabitatInfoController controller = loader.getController();
            controller.initializeData(boyCount, girlCount, Long.toString(getSimulationDuration()));

            stage.showAndWait();

            if (controller.isOKClicked()) {
                clearSimulationResult();
                return true;
            } else {
                resumeSimulation();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void openConsole() {
        if (!isConsoleOpen) {
            try {
                // Загрузка FXML и получение корневого элемента
                FXMLLoader loader = new FXMLLoader(getClass().getResource("habitat-console.fxml"));
                Parent root = loader.load();

                // Создание новой сцены и установка корневого элемента
                Scene scene = new Scene(root);

                // Создание нового окна и установка сцены
                Stage stage = new Stage();
                stage.setTitle("Console");
                stage.setScene(scene);

                // Установка модальности и владельца для окна
                stage.initModality(Modality.NONE);
                stage.initOwner(simulationPane.getScene().getWindow());

                // Показать окно и установить флаг isConsoleOpen в true
                stage.show();
                isConsoleOpen = true;

                // Обработчик закрытия окна
                stage.setOnCloseRequest(event -> isConsoleOpen = false);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void showCurrentObjectsDialog(Stage primaryStage) {
        if(isSimulationRunning) {
            pauseSimulation();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Текущие объекты");
            alert.setHeaderText(null);

            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);

            StringBuilder message = new StringBuilder();
            message.append("Список текущих объектов с временем их рождения:\n");
            birthTimeMap.forEach((objectId, birthTime) -> {
                message.append("ID: ").append(objectId).append(", Время рождения: ").append(birthTime).append("\n");
            });
            textArea.setText(message.toString());

            alert.getDialogPane().setContent(textArea);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.CLOSE);

            alert.initOwner(primaryStage);
            alert.showAndWait();

            resumeSimulation();
        }
    }
    public void addStudentToScene(Student student) {
        ImageView imageView = student.getImageView();
        simulationPane.getChildren().add(imageView);
    }
    public void setRandCoords(Student student) {
        double maxX = simulationPane.getWidth() - student.getImgWidth();
        double maxY = simulationPane.getHeight() - student.getImgHeight();
        double coordX = ThreadLocalRandom.current().nextDouble(0, maxX);
        double coordY = ThreadLocalRandom.current().nextDouble(0, maxY);
        student.moveTo(coordX, coordY);
    }
    private long getSimulationDuration() {
        simulationDuration = Duration.between(simulationStartTime, Instant.now());
        return simulationDuration.getSeconds();
    }
    public void generateStudentBoy() {  //Создания студента
        StudentBoy studentBoy = new StudentBoy(simulationPane, imgWidth, imgHeight);
        setRandCoords(studentBoy);
        addStudentToScene(studentBoy);
        synchronized (studentList) {
            studentList.add(studentBoy);
        }
        students.add(studentBoy);
        birthTimeMap.put(studentBoy.getId(), getSimulationDuration());
    }
    public void generateStudentGirl() {  //Создания студентки
        StudentGirl studentGirl = new StudentGirl(simulationPane, imgWidth, imgHeight);
        setRandCoords(studentGirl);
        addStudentToScene(studentGirl);
        synchronized (studentList) {
            studentList.add(studentGirl);
        }
        students.add(studentGirl);
        birthTimeMap.put(studentGirl.getId(), getSimulationDuration());
    }
    private void removeExpiredStudents() {
        long currentSimulationTime = getSimulationDuration();

        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            long birthTime = birthTimeMap.get(student.getId());
            long elapsedTime = currentSimulationTime - birthTime;

            // Изменение условия на проверку времени жизни студента
            if ((student instanceof StudentBoy && elapsedTime >= boyLifeTime) ||
                    (student instanceof StudentGirl && elapsedTime >= girlLifeTime)) {
                simulationPane.getChildren().remove(student.getImageView());
                iterator.remove();
                generatedIds.remove(student.getId());
                birthTimeMap.remove(student.getId());
                synchronized (studentList) {
                    studentList.remove(student);
                }
            }
        }
    }
    public void startBoyAI() {
        isBoyAIRunning = true;
        if (boyAI == null || !boyAI.isAlive()) {
            boyAI = new BoyAI(changeDirectionTime, movementSpeed, studentList, simulationPane);
            //boyAI.setPriority(boyAIPriority);
            boyAI.start();
        } else {
            //boyAI.setPriority(boyAIPriority);
            boyAI.resumeAI();
        }
    }

    public void startGirlAI() {
        isGirlAIRunning = true;
        if (girlAI == null || !girlAI.isAlive()) {
            girlAI = new GirlAI(radius, movementSpeed, studentList, simulationPane);
            girlAI.start();
            //girlAI.setPriority(girlAIPriority);
        } else {
            girlAI.resumeAI();
            //girlAI.setPriority(girlAIPriority);
        }
    }

    public void stopBoyAI() {
        isBoyAIRunning = false;
        if (boyAI != null) { // Проверяем, что объект girlAI не равен null
            boyAI.stopAI();
        }
    }

    public void stopGirlAI() {
        isGirlAIRunning = false;
        if (girlAI != null) { // Проверяем, что объект girlAI не равен null
            girlAI.stopAI();
        }
    }
    public boolean getBoyAIState(){
        return isBoyAIRunning;
    }
    public boolean getGirlAIState(){
        return isGirlAIRunning;
    }
    public static synchronized Habitat getInstance(Pane simulationPane, Label timeLabel, CheckBox showInfoCheckBox) {
        if (instance == null) {
            instance = new Habitat(simulationPane, timeLabel, showInfoCheckBox);
        }
        return instance;
    }
}

