import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LineGraphExample extends Application {

    private List<Double> data = new ArrayList<>();
    private final int MAX_DISPLAY_POINTS = 10;

    private LineChart<Number, Number> lineChart;
    private ScrollBar scrollBar;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    private String filePath;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 650);

        // Create X and Y axes
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("");
        yAxis.setLabel("Ms");

        // Create the line chart
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Ping Time Chart");
        // Initialize filePath
        getFile();

        // Create initial series with the first set of data points (if any)
        updateChartDataFromFile();

        // Create a scroll bar for navigating through the data
        scrollBar = new ScrollBar();
        scrollBar.setMin(0);
        scrollBar.setMax(0); // Initially set to zero, will update based on data size
        scrollBar.setVisibleAmount(MAX_DISPLAY_POINTS);
        scrollBar.setUnitIncrement(1);
        scrollBar.setBlockIncrement(MAX_DISPLAY_POINTS);

        // Update chart when scroll bar value changes
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(() -> updateChart(newValue.intValue()));
            }
        });

        // Set up root layout
        root.setCenter(lineChart);
        root.setBottom(scrollBar);

        // Show the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.show();

        // Schedule a task to update data periodically (every 5 seconds in this example)
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::updateChartDataFromFile, 0, 5, TimeUnit.SECONDS);
    }

    private void getFile() {
        String fileName = "tests/PingTimestamps.txt";
        File pingFile = new File(fileName);
        ArrayList<Integer> fileCounter = new ArrayList<>();
        int arrayCounter = 0;
        String newFileName = null;
        while (pingFile.exists()) {

            arrayCounter++;
            newFileName = "tests/PingTimestamps" + arrayCounter + ".txt";
            fileCounter.add(arrayCounter);
            pingFile = new File(newFileName);
        }
arrayCounter= arrayCounter - 1;
        newFileName = "tests/PingTimestamps" + arrayCounter + ".txt";
        filePath = newFileName;  // Example file path
    }

    // Method to update the LineChart with data from file
    private void updateChartDataFromFile() {
        List<Double> newData = readDataFromFile(filePath);
        if (newData != null && !newData.isEmpty()) {
            data.clear();
            data.addAll(newData);

            // Update scrollbar max value
            Platform.runLater(() -> {
                scrollBar.setMax(data.size() - 1);

                // Update chart if scrollbar is at max
                if (scrollBar.getValue() >= scrollBar.getMax() - scrollBar.getBlockIncrement()) {
                    updateChart((int) scrollBar.getMax());
                }
            });
        }
    }

    // Method to read data from file based on provided filePath
    // Method to read data from file based on provided filePath
    private List<Double> readDataFromFile(String filePath) {
        List<Double> newData = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim(); // Trim leading and trailing whitespace

                if (line.isEmpty()) {
                    // Skip empty lines
                    continue;
                }

                try {
                    double value = Main.findTime(line);
                    newData.add(value);
                } catch (NumberFormatException e) {
                    // Handle parsing errors if necessary
                    System.err.println("Error parsing line as double at line " + lineNumber + ": " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return newData;
    }



    // Method to update the LineChart with current data
    private void updateChart(int startIndex) {
        lineChart.getData().clear();
        xAxis.setAutoRanging(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        int endIndex = Math.min(startIndex + MAX_DISPLAY_POINTS, data.size());
        for (int i = startIndex; i < endIndex; i++) {
            series.getData().add(new XYChart.Data<>(i, data.get(i)));
        }

        lineChart.getData().add(series);

        xAxis.setLowerBound(startIndex);
        xAxis.setUpperBound(endIndex - 1);
        xAxis.setTickUnit(1);

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for (int i = startIndex; i < endIndex; i++) {
            double value = data.get(i);
            if (value < minY) {
                minY = value;
            }
            if (value > maxY) {
                maxY = value;
            }
        }
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);

        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                int index = object.intValue();
                if (index >= startIndex && index < endIndex) {
                    return String.valueOf(index);
                } else {
                    return "";
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
