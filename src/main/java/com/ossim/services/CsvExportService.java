package com.ossim.services;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvExportService {

    /**
     * Opens a Save dialog and writes the given headers + rows as CSV.
     * Returns true if the file was saved, false if the user cancelled
     * the dialog or a write error occurred — the caller decides how to
     * report failure to the user.
     */
    public static boolean exportToCsv(Window ownerWindow, String defaultFileName,
                                       List<String> headers, List<List<String>> rows) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Results as CSV");
        fileChooser.setInitialFileName(defaultFileName);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file == null) {
            return false;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(buildCsvContent(headers, rows));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Builds the CSV text content from headers and rows. Kept separate
     * from the file dialog / file-writing so it can be verified without
     * a JavaFX runtime.
     */
    public static String buildCsvContent(List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", escapeAll(headers))).append("\n");
        for (List<String> row : rows) {
            sb.append(String.join(",", escapeAll(row))).append("\n");
        }
        return sb.toString();
    }

    private static List<String> escapeAll(List<String> values) {
        List<String> escaped = new ArrayList<>();
        for (String v : values) {
            escaped.add(escapeCsvValue(v));
        }
        return escaped;
    }

    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}