import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalTaxTrackerWithGUI extends Application {
    private List<Transaction> transactions = new ArrayList<>();
    private TextField descriptionField;
    private TextField amountField;
    private TextArea transactionListArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Personal Tax Tracker");

        // Create input fields
        descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        amountField = new TextField();
        amountField.setPromptText("Amount");

        // Create transaction list area
        transactionListArea = new TextArea();
        transactionListArea.setEditable(false);

        // Create buttons
        Button addButton = new Button("Add Transaction");
        addButton.setOnAction(e -> addTransaction());
        Button calculateButton = new Button("Calculate Totals");
        calculateButton.setOnAction(e -> calculateTotals());

        // Create a layout for input fields and buttons
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10, 10, 10, 10));
        inputGrid.add(descriptionField, 0, 0);
        inputGrid.add(amountField, 1, 0);
        inputGrid.add(addButton, 2, 0);
        inputGrid.add(calculateButton, 0, 1);

        // Create the main layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(inputGrid);
        borderPane.setCenter(transactionListArea);

        // Create the scene and set it on the stage
        Scene scene = new Scene(borderPane, 400, 300);
        primaryStage.setScene(scene);

        // Load transactions from a file (if available)
        loadTransactionsFromFile();

        primaryStage.show();
    }

    private void addTransaction() {
        String description = descriptionField.getText();
        String amountText = amountField.getText();

        if (!description.isEmpty() && !amountText.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountText);
                Transaction transaction = new Transaction(description, amount);
                transactions.add(transaction);

                // Update the transaction list area
                transactionListArea.appendText("Description: " + description + ", Amount: $" + amount + "\n");

                // Clear input fields
                descriptionField.clear();
                amountField.clear();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Amount must be a valid number.");
            }
        } else {
            showAlert("Missing Information", "Please enter both description and amount.");
        }
    }

    private void calculateTotals() {
        double totalIncome = 0;
        double totalExpenses = 0;

        for (Transaction transaction : transactions) {
            double amount = transaction.getAmount();
            if (amount > 0) {
                totalIncome += amount;
            } else {
                totalExpenses += Math.abs(amount);
            }
        }

        showAlert("Totals", "Total Income: $" + totalIncome + "\nTotal Expenses: $" + totalExpenses +
                "\nNet Income: $" + (totalIncome - totalExpenses));
    }

    private void loadTransactionsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String description = parts[0].trim();
                    double amount = Double.parseDouble(parts[1].trim());
                    transactions.add(new Transaction(description, amount));

                    // Update the transaction list area
                    transactionListArea.appendText("Description: " + description + ", Amount: $" + amount + "\n");
                }
            }
        } catch (IOException e) {
            // Ignore if the file does not exist
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

class Transaction {
    private String description;
    private double amount;

    public Transaction(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}
