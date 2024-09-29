package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OnlineQuizApplication extends Application {

    private HashMap<String, String> users = new HashMap<>(); // Simulating a user database
    private List<Quiz> quizzes = new ArrayList<>(); // List to store quizzes
    private int currentQuestionIndex = 0; // To track the current question in a quiz
    private HashMap<String, List<QuizAttempt>> userAttempts = new HashMap<>(); // To track user attempts and scores
    private String currentUser = ""; // Track the current logged-in user

    @Override
    public void start(Stage primaryStage) {
        // Predefined users (for testing)
        users.put("admin", "admin123");
        users.put("user", "password"); // Adding a user for testing

        primaryStage.setTitle("Online Quiz - User Authentication");

        // Create login form
        GridPane loginForm = createLoginForm(primaryStage);

        Scene scene = new Scene(loginForm, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createLoginForm(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Username Label
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        // Username Input
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter username");
        GridPane.setConstraints(usernameInput, 1, 0);

        // Password Label
        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        // Password Input
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");
        GridPane.setConstraints(passwordInput, 1, 1);

        // Login Button
        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            if (authenticate(username, password)) {
                currentUser = username; // Set the current user
                if (username.equals("admin")) {
                    showAdminDashboard(primaryStage); // Admin login
                } else {
                    showQuizSelectionPage(primaryStage); // User login
                }
            } else {
                showMessage("Login Failed", "Invalid username or password.");
            }
        });

        // Register Button
        Button registerButton = new Button("Register");
        GridPane.setConstraints(registerButton, 2, 2);
        registerButton.setOnAction(e -> showRegistrationForm(primaryStage));

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton, registerButton);
        return grid;
    }

    private boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showRegistrationForm(Stage primaryStage) {
        Stage registerStage = new Stage();
        registerStage.setTitle("User Registration");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Username Label
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        // Username Input
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter username");
        GridPane.setConstraints(usernameInput, 1, 0);

        // Password Label
        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        // Password Input
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");
        GridPane.setConstraints(passwordInput, 1, 1);

        // Register Button
        Button registerButton = new Button("Register");
        GridPane.setConstraints(registerButton, 1, 2);
        // Registration logic
        registerButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Registration Failed", "Username and Password cannot be empty.");
            } else if (users.containsKey(username)) {
                showMessage("Registration Failed", "Username already exists.");
            } else {
                users.put(username, password); // Add user to the HashMap
                showMessage("Registration Successful", "User registered successfully!");
                registerStage.close();
            }
        });

        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, registerButton);
        Scene scene = new Scene(grid, 300, 200);
        registerStage.setScene(scene);
        registerStage.show();
    }

    private void showAdminDashboard(Stage primaryStage) {
        Stage adminStage = new Stage();
        adminStage.setTitle("Admin Dashboard");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Add Quiz Button
        Button addQuizButton = new Button("Add Quiz");
        addQuizButton.setOnAction(e -> showQuizForm(adminStage, -1)); // -1 indicates new quiz

        // List of Quizzes
        ListView<String> quizListView = new ListView<>();
        updateQuizListView(quizListView);

        // Edit Button
        Button editButton = new Button("Edit Quiz");
        editButton.setOnAction(e -> {
            int selectedQuizIndex = quizListView.getSelectionModel().getSelectedIndex();
            if (selectedQuizIndex >= 0) {
                showQuizForm(adminStage, selectedQuizIndex);
            } else {
                showMessage("No Selection", "Please select a quiz to edit.");
            }
        });

        // Delete Button
        Button deleteButton = new Button("Delete Quiz");
        deleteButton.setOnAction(e -> {
            int selectedQuizIndex = quizListView.getSelectionModel().getSelectedIndex();
            if (selectedQuizIndex >= 0) {
                quizzes.remove(selectedQuizIndex);
                updateQuizListView(quizListView);
                showMessage("Success", "Quiz deleted successfully.");
            } else {
                showMessage("No Selection", "Please select a quiz to delete.");
            }
        });

        layout.getChildren().addAll(addQuizButton, quizListView, editButton, deleteButton);
        Scene adminScene = new Scene(layout, 400, 400);
        adminStage.setScene(adminScene);
        adminStage.show();
    }

    private void updateQuizListView(ListView<String> listView) {
        listView.getItems().clear();
        for (Quiz quiz : quizzes) {
            listView.getItems().add(quiz.getTitle());
        }
    }

    private void showQuizForm(Stage parentStage, int quizIndex) {
        Stage quizFormStage = new Stage();
        quizFormStage.setTitle(quizIndex == -1 ? "Create New Quiz" : "Edit Quiz");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Quiz Title
        Label titleLabel = new Label("Quiz Title:");
        GridPane.setConstraints(titleLabel, 0, 0);
        TextField titleInput = new TextField();
        GridPane.setConstraints(titleInput, 1, 0);

        // Quiz Question
        Label quesLabel = new Label("Quiz Question:");
        GridPane.setConstraints(quesLabel, 0, 1);
        TextField quesInput = new TextField();
        GridPane.setConstraints(quesInput, 1, 1);

        // Option 1
        Label option1Label = new Label("Option 1:");
        GridPane.setConstraints(option1Label, 0, 2);
        TextField option1Input = new TextField();
        GridPane.setConstraints(option1Input, 1, 2);

        // Option 2
        Label option2Label = new Label("Option 2:");
        GridPane.setConstraints(option2Label, 0, 3);
        TextField option2Input = new TextField();
        GridPane.setConstraints(option2Input, 1, 3);

        // Option 3
        Label option3Label = new Label("Option 3:");
        GridPane.setConstraints(option3Label, 0, 4);
        TextField option3Input = new TextField();
        GridPane.setConstraints(option3Input, 1, 4);

        // Option 4
        Label option4Label = new Label("Option 4:");
        GridPane.setConstraints(option4Label, 0, 5);
        TextField option4Input = new TextField();
        GridPane.setConstraints(option4Input, 1, 5);

        // Correct Answer
        Label correctAnswerLabel = new Label("Correct Answer:");
        GridPane.setConstraints(correctAnswerLabel, 0, 6);
        TextField correctAnswerInput = new TextField();
        GridPane.setConstraints(correctAnswerInput, 1, 6);

        // Save Button
        Button saveButton = new Button("Save");
        GridPane.setConstraints(saveButton, 1, 7);
        saveButton.setOnAction(e -> {
            String title = titleInput.getText();
            String question = quesInput.getText();
            String option1 = option1Input.getText();
            String option2 = option2Input.getText();
            String option3 = option3Input.getText();
            String option4 = option4Input.getText();
            String correctAnswer = correctAnswerInput.getText();

            if (title.isEmpty() || question.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty() || correctAnswer.isEmpty()) {
                showMessage("Input Error", "Please fill in all fields.");
            } else {
                if (quizIndex == -1) {
                    Quiz newQuiz = new Quiz(title, question, option1, option2, option3, option4, correctAnswer);
                    quizzes.add(newQuiz);
                    showMessage("Success", "Quiz created successfully!");
                } else {
                    // Update existing quiz
                    quizzes.get(quizIndex).updateQuiz(title, question, option1, option2, option3, option4, correctAnswer);
                    showMessage("Success", "Quiz updated successfully!");
                }
                quizFormStage.close();
            }
        });

        grid.getChildren().addAll(titleLabel, titleInput, quesLabel, quesInput, option1Label, option1Input,
                option2Label, option2Input, option3Label, option3Input, option4Label, option4Input,
                correctAnswerLabel, correctAnswerInput, saveButton);

        if (quizIndex != -1) {
            // Populate fields for editing
            Quiz existingQuiz = quizzes.get(quizIndex);
            titleInput.setText(existingQuiz.getTitle());
            quesInput.setText(existingQuiz.getQuestion());
            option1Input.setText(existingQuiz.getOptions()[0]);
            option2Input.setText(existingQuiz.getOptions()[1]);
            option3Input.setText(existingQuiz.getOptions()[2]);
            option4Input.setText(existingQuiz.getOptions()[3]);
            correctAnswerInput.setText(existingQuiz.getCorrectAnswer());
        }

        Scene scene = new Scene(grid, 400, 400);
        quizFormStage.setScene(scene);
        quizFormStage.show();
    }

    private void showQuizSelectionPage(Stage primaryStage) {
        Stage quizSelectionStage = new Stage();
        quizSelectionStage.setTitle("Quiz Selection");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // List of Quizzes
        ListView<String> quizListView = new ListView<>();
        updateQuizListView(quizListView);

        // Start Quiz Button
        Button startQuizButton = new Button("Start Quiz");
        startQuizButton.setOnAction(e -> {
            int selectedQuizIndex = quizListView.getSelectionModel().getSelectedIndex();
            if (selectedQuizIndex >= 0) {
                startQuiz(quizSelectionStage, selectedQuizIndex);
            } else {
                showMessage("No Selection", "Please select a quiz to start.");
            }
        });

        // View Past Attempts Button
        Button viewAttemptsButton = new Button("View Past Attempts");
        viewAttemptsButton.setOnAction(e -> showPastAttempts(primaryStage));

        layout.getChildren().addAll(quizListView, startQuizButton, viewAttemptsButton);
        Scene quizScene = new Scene(layout, 400, 400);
        quizSelectionStage.setScene(quizScene);
        quizSelectionStage.show();
    }

    private void startQuiz(Stage parentStage, int quizIndex) {
        currentQuestionIndex = 0; // Reset for the new quiz
        Quiz selectedQuiz = quizzes.get(quizIndex);
        AtomicInteger score = new AtomicInteger(); // Initialize score for this attempt

        VBox quizLayout = new VBox(10);
        quizLayout.setPadding(new Insets(10));

        // Display the first question
        Label questionLabel = new Label(selectedQuiz.getQuestion());
        quizLayout.getChildren().add(questionLabel);

        // Display options
        ToggleGroup optionsGroup = new ToggleGroup();
        for (String option : selectedQuiz.getOptions()) {
            RadioButton optionButton = new RadioButton(option);
            optionButton.setToggleGroup(optionsGroup);
            quizLayout.getChildren().add(optionButton);
        }

        // Submit Button
        Button submitButton = new Button("Submit Answer");
        quizLayout.getChildren().add(submitButton);

        submitButton.setOnAction(e -> {
            RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
            if (selectedOption != null) {
                String userAnswer = selectedOption.getText();
                if (userAnswer.equals(selectedQuiz.getCorrectAnswer())) {
                    score.getAndIncrement(); // Increment score for correct answer
                }
            }

            // Track user progress
            if (currentQuestionIndex < quizzes.size() - 1) {
                currentQuestionIndex++;
                questionLabel.setText(quizzes.get(currentQuestionIndex).getQuestion());
            } else {
                // Quiz finished, show results
                showResults(parentStage, score.get());
            }
        });

        Scene quizScene = new Scene(quizLayout, 400, 400);
        parentStage.setScene(quizScene);
    }

    private void showResults(Stage parentStage, int score) {
        String resultMessage = "Your score: " + score;

        // Save attempt record
        saveQuizAttempt(currentUser, score);

        // Show results
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Completed");
        alert.setHeaderText(null);
        alert.setContentText(resultMessage);
        alert.showAndWait();

        // Go back to the quiz selection page
        showQuizSelectionPage(parentStage);
    }

    private void saveQuizAttempt(String username, int score) {
        QuizAttempt attempt = new QuizAttempt(score);
        if (!userAttempts.containsKey(username)) {
            userAttempts.put(username, new ArrayList<>());
        }
        userAttempts.get(username).add(attempt);
    }

    private void showPastAttempts(Stage primaryStage) {
        Stage attemptsStage = new Stage();
        attemptsStage.setTitle("Past Attempts");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        if (!userAttempts.containsKey(currentUser) || userAttempts.get(currentUser).isEmpty()) {
            layout.getChildren().add(new Label("No past attempts found."));
        } else {
            for (QuizAttempt attempt : userAttempts.get(currentUser)) {
                layout.getChildren().add(new Label("Score: " + attempt.getScore()));
            }
        }

        Scene attemptsScene = new Scene(layout, 300, 300);
        attemptsStage.setScene(attemptsScene);
        attemptsStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Quiz class
class Quiz {
    private String title;
    private String question;
    private String[] options;
    private String correctAnswer;

    public Quiz(String title, String question, String option1, String option2, String option3, String option4, String correctAnswer) {
        this.title = title;
        this.question = question;
        this.options = new String[]{option1, option2, option3, option4};
        this.correctAnswer = correctAnswer;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void updateQuiz(String title, String question, String option1, String option2, String option3, String option4, String correctAnswer) {
        this.title = title;
        this.question = question;
        this.options = new String[]{option1, option2, option3, option4};
        this.correctAnswer = correctAnswer;
    }
}

// QuizAttempt class
class QuizAttempt {
    private final int score;

    public QuizAttempt(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}