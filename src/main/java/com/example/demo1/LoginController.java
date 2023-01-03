package com.example.demo1;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class LoginController {
    @FXML
    private PasswordField insertedName;
    @FXML
    private PasswordField insertedPassword;
    private Stage stage;

    @FXML
    public void onLogoutClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onRegisterClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public void onLoginClick(ActionEvent actionEvent) {
        /*dati di un utente che esiste nella collection se volete usarli per prova:
            NAME = Carmen
            PASSWORD = 6czYhW4F
        */
        String name = insertedName.getText();
        String password = insertedPassword.getText();
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("RecipeShare");
            MongoCollection<Document> collection = database.getCollection("author");
            Bson filter = Filters.and(
                    Filters.eq("authorName", name),
                    Filters.eq("password", password));
            MongoCursor<Document> cursor = collection.find(filter).iterator();
            if (cursor.hasNext()) {
                System.out.println("TROVATO");
                //cambio pagina
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Loggato.fxml"));
                stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load(), 600, 500);
                stage.setTitle("Hello "+ name);
                stage.setScene(scene);
                stage.show();
            }
            else System.out.println("NON ESISTE QUESTO AUTHOR");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}