package com.example.demo1;

import com.example.demo1.dao.mongo.AuthorMongoDAO;
import com.example.demo1.model.Author;
import com.example.demo1.model.Moderator;
import com.example.demo1.service.AuthorService;
import com.example.demo1.service.ModeratorService;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class LoginController {
    @FXML
    private TextField insertedName;
    @FXML
    private TextField insertedPassword;
    private Stage stage;

    @FXML
    public void onLogoutClick(ActionEvent actionEvent) throws IOException {
        String nomeSchermata = "hello-view.fxml";
        cambiaSchermata(actionEvent,nomeSchermata);
    }

    @FXML
    public void onRegisterClick(ActionEvent actionEvent) throws IOException {
        String nomeSchermata = "Register.fxml";
        DataSingleton.getInstance().setTypeOfUser("author");
        cambiaSchermata(actionEvent,nomeSchermata);
    }

    public void onLoginClick(ActionEvent actionEvent) throws IOException {
        String nomePagina = null;
        String name = insertedName.getText();
        String password = insertedPassword.getText();
        boolean existAuthor = AuthorService.login(name,password);
        Moderator currentModerator = new Moderator(name,password);
        boolean existModerator = ModeratorService.tryLogin(currentModerator);
        if (existAuthor == true) {
            Author currentAuthor = AuthorService.getAuthor(name);
            int avatarIndex = currentAuthor.getImage();
            DataSingleton.getInstance().setAvatar(avatarIndex);
            DataSingleton.getInstance().setAvatarIndex(avatarIndex);
            DataSingleton.getInstance().setAuthorPromotion(currentAuthor.getPromotion());
            DataSingleton.getInstance().setTypeOfUser("author");
            nomePagina = "Loggato.fxml";
        }
        else if (existModerator == true){
            DataSingleton.getInstance().setTypeOfUser("moderator");
            nomePagina = "Moderator.fxml";
        }
        if (nomePagina != null) {
            DataSingleton data = DataSingleton.getInstance();
            data.setAuthorName(name);
            data.setPassword(password);
            if (nomePagina.equals("Loggato.fxml") && DataSingleton.getInstance().getAuthorPromotion() == 1)
                cambiaSchermata(actionEvent,"PromotionOffer.fxml");
            else cambiaSchermata(actionEvent,nomePagina);
        }
        /*
        String uri = Configuration.MONGODB_URL;
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(Configuration.MONGODB_DB); //da scegliere il nome uguale per tutti
            MongoCollection<Document> collectionAuthor = database.getCollection(Configuration.MONGODB_AUTHOR);
            MongoCollection<Document> collectionModerator = database.getCollection(Configuration.MONGODB_MODERATOR);
            Bson filterAuthor = Filters.and(
                    Filters.eq("authorName", name),
                    Filters.eq("password", password));
            MongoCursor<Document> cursorAuthor = collectionAuthor.find(filterAuthor).iterator();
            Bson filterModerator = Filters.and(
                    Filters.eq("moderatorName", name),
                    Filters.eq("password", password));
            MongoCursor<Document> cursorModerator = collectionModerator.find(filterModerator).iterator();
            if (cursorAuthor.hasNext()) {
                Document currentAuthor = cursorAuthor.next();
                int avatarIndex = (int) currentAuthor.get("image");
                DataSingleton.getInstance().setAvatar(avatarIndex);
                DataSingleton.getInstance().setAvatarIndex(avatarIndex);
                DataSingleton.getInstance().setAuthorPromotion((Integer) currentAuthor.get("promotion"));
                DataSingleton.getInstance().setTypeOfUser("author");
                System.out.println("TROVATO AUTHOR");
                nomePagina = "Loggato.fxml";
            }
            else {
                System.out.println("NON TROVATO AUTHOR");
                if (cursorModerator.hasNext()) {
                    DataSingleton.getInstance().setTypeOfUser("moderator");
                    System.out.println("TROVATO MODERATOR");
                    nomePagina = "Moderator.fxml";
                }
                else System.out.println("NON TROVATO MODERATOR");
            }
            if (nomePagina != null) {
                DataSingleton data = DataSingleton.getInstance();
                data.setAuthorName(name);
                data.setPassword(password);
                if (nomePagina.equals("Loggato.fxml") && DataSingleton.getInstance().getAuthorPromotion() == 1)
                    cambiaSchermata(actionEvent,"PromotionOffer.fxml");
                else cambiaSchermata(actionEvent,nomePagina);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void cambiaSchermata(ActionEvent actionEvent,String nomeSchermata) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(nomeSchermata));
        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void onTextClick(MouseEvent mouseEvent) {
        System.out.println("TESTO CLICKATO");
    }

}