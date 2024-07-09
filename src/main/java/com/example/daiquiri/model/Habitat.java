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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.io.*;
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
    private int boyAIPriority = 5;
    private int girlAIPriority = 5;
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
        this.simulationStartTime = Instant.now(); // Инициализация переменной при создании объекта Habitat
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
        if (simulationTimer != null) {
            simulationTimer.cancel();
            simulationTimer = null;
        }
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
    public int getBoySpawnDelay() {
        return boySpawnDelay;
    }
    public void setGirlSpawnDelay(int delay) {
        this.girlSpawnDelay = delay;
    }
    public int getGirlSpawnDelay() {
        return girlSpawnDelay;
    }
    public void setBoyLifeTime(int lifeTime) {
        this.boyLifeTime = lifeTime;
    }
    public int getBoyLifeTime() {
        return boyLifeTime;
    }
    public void setGirlLifeTime(int lifeTime) {
        this.girlLifeTime = lifeTime;
    }
    public int getGirlLifeTime() {
        return girlLifeTime;
    }
    public void setBoyProbability(double probability) {
        this.boyProbability = probability;
    }
    public double getBoyProbability() {
        return boyProbability;
    }
    public void setGirlProbability(double probability) {
        this.girlProbability = probability;
    }
    public double getGirlProbability() {
        return girlProbability;
    }
    public void setBoyAIPriority(int priority) {
        this.boyAIPriority = priority;
    }
    public int getBoyAIPriority() {
        return boyAIPriority;
    }
    public void setGirlAIPriority(int priority) {
        this.girlAIPriority = priority;
    }
    public int getGirlAIPriority() {
        return girlAIPriority;
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
    public void openConsole(Controller controller) {
        if (!isConsoleOpen) {
            try {
                // Загрузка FXML и получение корневого элемента
                FXMLLoader loader = new FXMLLoader(getClass().getResource("habitat-console.fxml"));
                Parent root = loader.load();
                HabitatConsoleController consoleController = loader.getController();
                consoleController.setController(controller);
                // Создание новой сцены и установка корневого элемента
                Scene scene = new Scene(root);

                // Создание нового окна и установка сцены
                Stage stage = new Stage();
                stage.setTitle("Console");
                stage.setScene(scene);
                stage.setResizable(false);

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
            updateTimer();
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
            boyAI.setPriority(boyAIPriority);
            boyAI.start();
        } else {
            boyAI.setPriority(boyAIPriority);
            boyAI.resumeAI();
        }
    }

    public void startGirlAI() {
        isGirlAIRunning = true;
        if (girlAI == null || !girlAI.isAlive()) {
            girlAI = new GirlAI(radius, movementSpeed, studentList, simulationPane);
            girlAI.setPriority(girlAIPriority);
            girlAI.start();
        } else {
            girlAI.setPriority(girlAIPriority);
            girlAI.resumeAI();
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
    private String chooseFile() {
        stopSimulation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");

        Stage stage = (Stage) simulationPane.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);
        //resumeSimulation();

        return file != null ? file.getAbsolutePath() : null;
    }
    private String chooseSaveFile() {
        pauseSimulation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для сохранения");

        Stage stage = (Stage) simulationPane.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);
        resumeSimulation();

        return file != null ? file.getAbsolutePath() : null;
    }
    public void saveToFile() {
        String fileName = chooseSaveFile();
        if (fileName != null) { // Проверяем, был ли выбран файл
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
                oos.writeObject(studentList);
                oos.writeObject(birthTimeMap);
                oos.writeObject(generatedIds);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFromFile() {
        if(isSimulationRunning){
            stopSimulation();
        }
        String fileName = chooseFile();
        if (fileName != null) { // Проверяем, был ли выбран файл
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
                List<Student> loadedStudents = (List<Student>) ois.readObject();
                Map<Integer, Long> loadedBirthTimeMap = (Map<Integer, Long>) ois.readObject();
                Set<Integer> loadedGeneratedIds = (Set<Integer>) ois.readObject();

                for (Student student : loadedStudents) {
                    if (student instanceof StudentBoy) {
                        StudentBoy studentBoy = new StudentBoy(simulationPane, imgWidth, imgHeight);
                        studentBoy.moveTo(student.getCoordX(), student.getCoordY());
    /*                    studentBoy.coordX = student.coordX;
                        studentBoy.coordY = student.coordY;*/
                        studentBoy.setId(student.getId());
                        ImageView imageView = studentBoy.getImageView();
                        //studentBoy.moveTo(studentBoy.coordX, studentBoy.coordY);
                        simulationPane.getChildren().add(imageView);
                        students.add(studentBoy);
                        generatedIds.add(studentBoy.getId());
                        studentList.add(studentBoy);
                        birthTimeMap.put(studentBoy.getId(), 0L);
                    } else if (student instanceof StudentGirl) {
                        StudentGirl studentGirl = new StudentGirl(simulationPane, imgWidth, imgHeight);
                        studentGirl.moveTo(student.getCoordX(), student.getCoordY());
    /*                    studentGirl.coordX = student.coordX;
                        studentGirl.coordY = student.coordY;*/
                        studentGirl.setId(student.getId());
                        ImageView imageView = studentGirl.getImageView();
                        //studentGirl.moveTo(studentGirl.coordX, studentGirl.coordY);
                        simulationPane.getChildren().add(imageView);
                        students.add(studentGirl);
                        generatedIds.add(studentGirl.getId());
                        studentList.add(studentGirl);
                        birthTimeMap.put(studentGirl.getId(), 0L);

                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveSettings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("settings.txt"))) {
            writer.println("boySpawnDelay = " + boySpawnDelay);
            writer.println("girlSpawnDelay = " + girlSpawnDelay);
            writer.println("boyLifeTime = " + boyLifeTime);
            writer.println("girlLifeTime = " + girlLifeTime);
            writer.println("boyProbability = " + boyProbability);
            writer.println("girlProbability = " + girlProbability);
            writer.println("boyAIPriority = " + boyAIPriority);
            writer.println("girlAIPriority = " + girlAIPriority);
/*            writer.println("girlAIPriority = " + girlAIPriority);
            writer.println("boyAIPriority = " + boyAIPriority);*/
            writer.println("isTimerVisible = " + timeLabel.isVisible());
            writer.println("showInfoCheckbox = " + showInfoCheckBox.isSelected());
/*            if (isBoyAIRunning) {
                stopBoyAI();
            }
            if (isGirlAIRunning) {
                stopGirlAI();
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadSettings() {
        File file = new File("settings.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    switch (key) {
                        case "boySpawnDelay":
                            boySpawnDelay = Integer.parseInt(value);
                            break;
                        case "girlSpawnDelay":
                            girlSpawnDelay = Integer.parseInt(value);
                            break;
                        case "boyLifeTime":
                            boyLifeTime = Integer.parseInt(value);
                            break;
                        case "girlLifeTime":
                            girlLifeTime = Integer.parseInt(value);
                            break;
                        case "boyProbability":
                            boyProbability = Double.parseDouble(value);
                            break;
                        case "girlProbability":
                            girlProbability = Double.parseDouble(value);
                            break;
                        case "boyAIPriority":
                            boyAIPriority = Integer.parseInt(value);
                            break;
                        case "girlAIPriority":
                            girlAIPriority = Integer.parseInt(value);
                            break;
 /*                       case "girlAIPriority":
                            girlAIPriority = Integer.parseInt(value);
                            break;
                        case "boyAIPriority":
                            boyAIPriority = Integer.parseInt(value);
                            break;*/
                        case "isTimerVisible":
                            isTimerVisible = Boolean.parseBoolean(value);
                            break;
                        case "showInfoCheckbox":
                            showInfoCheckBox.setSelected(Boolean.parseBoolean(value));
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isHabitatTimerVisible(){
        return isTimerVisible;
    }
}

