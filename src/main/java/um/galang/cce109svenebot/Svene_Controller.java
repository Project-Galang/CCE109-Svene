package um.galang.cce109svenebot;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

public class Svene_Controller {
    static Svene_Backend sveneBackend = new Svene_Backend();
    @FXML
    private TextField chatField;
    @FXML
    private TextArea chatBox;
    HashMap<String, String> CHAT_LOG = new HashMap<>();
    String NameOfUserSession;
    boolean isFirstClick = true;
    @FXML
    public void initialize() {
        chatBox.setWrapText(true);
        chatBox.appendText("Hello! I am Svene the Bot" + "\n");
        chatBox.appendText("What is your name?" + "\n");
    }
    @FXML
    protected void chatButton() {
        if (isFirstClick) {
            NameOfUserSession = chatField.getText();
            CHAT_LOG.put(NameOfUserSession, NameOfUserSession);
            chatBox.appendText("Trisha: Hello, " + NameOfUserSession + "! I am Trisha the Chat Bot." + "\n");
            CHAT_LOG.put("Trisha", "Hello, " + NameOfUserSession + "! I am Trisha the Chat Bot." + "\n");
            isFirstClick = false;
        } else {
            textProcess();
        }
    }
    @FXML
    protected void saveChat() {
        chatExport();
    }
    protected void textProcess() {
        try {
            String userPrompt = chatField.getText();
            chatBox.appendText(NameOfUserSession + ": " + userPrompt + "\n");
            CHAT_LOG.put(NameOfUserSession, userPrompt);
            StringBuilder retrievedJSON = sveneBackend.apiConfiguration(userPrompt);
            String promptResponse = sveneBackend.parsingResponse(retrievedJSON);
            CHAT_LOG.put("Trisha", promptResponse);
            chatBox.appendText("Trisha: " + promptResponse + "\n");
            chatField.clear();
        } catch (Exception e) {
            chatBox.appendText("Trisha: An Error has Occured :( Please try again later." + "\n");
            chatBox.appendText("Error: " + e);
        }
    }
    protected void chatExport() {
        sveneBackend.chatLogExport(CHAT_LOG);
    }
}

class Svene_Backend {
    StringBuilder apiConfiguration(String prompt) throws IOException {
        String apiKey = "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";
        String endpoint = "https://api.openai.com/v1/completions";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        String model = "text-davinci-003";
        int maximumLength = 150;
        String requestBody = "{\"prompt\": \"" + prompt + "\", \"model\": \"" + model + "\", \"max_tokens\": " + maximumLength + "}";
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(requestBody);
        writer.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response;
    }
    String parsingResponse(StringBuilder response) {
        JsonParser parser = new JsonParser();
        JsonElement responseJson = parser.parse(response.toString());
        JsonObject responseObject = responseJson.getAsJsonObject();
        JsonArray choicesArray = responseObject.getAsJsonArray("choices");
        JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
        String text = firstChoice.get("text").getAsString();
        String newText = text.replaceAll("\n\n", "");
        return newText;
    }

    void chatLogExport(Map<String, String> toFile) {
        // Create the GUI
        JFrame frame = new JFrame("Write HashMap to File");
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            // Write the contents of the HashMap to the file at the specified location
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (Map.Entry<String, String> entry : toFile.entrySet()) {
                    bw.write(entry.getKey() + "=" + entry.getValue());
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(frame, "File saved successfully");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}