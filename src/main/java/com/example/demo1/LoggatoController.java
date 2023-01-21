package com.example.demo1;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static com.mongodb.client.model.Aggregates.*;

public class LoggatoController implements Initializable{
    public AnchorPane anchorPane;
    @FXML
    private Label welcomeText;
    private Stage stage;
    private ClassForTableView TableViewObject = new ClassForTableView();
    @FXML
    private TextField nameToSearchTextField;

    private String nameToSearch = null;
    private Integer pageNumber = 0;
    @FXML
    public void onLogoutClick(ActionEvent actionEvent) throws IOException {
        String nomeSchermata = "hello-view.fxml";
        cambiaSchermata(actionEvent,nomeSchermata);
    }

    @FXML
    public void onCercaUtenteClick(ActionEvent actionEvent) throws IOException {
        String nomeSchermata = "Ricerca_Utente.fxml";
        cambiaSchermata(actionEvent,nomeSchermata);
    }

    @FXML
    public void onAnalyticsClick(ActionEvent actionEvent) throws IOException {
        String nomeSchermata = "LoggatoAnalytics.fxml";
        cambiaSchermata(actionEvent,nomeSchermata);
    }

    @FXML
    public void onProvaRecipe(ActionEvent actionEvent) throws IOException {
        String nomeSchermata = "Recipe.fxml";
        cambiaSchermata(actionEvent,nomeSchermata);
    }
    @FXML
    public void onNextPageClick(){
        pageNumber = pageNumber + 1;
        //updateTableView(TableViewObject,pageNumber);
        searchInDBAndLoadInTableView(nameToSearch,pageNumber);
    }
    @FXML
    public void onPreviousPageClick(){
        if(pageNumber>=1){
            pageNumber = pageNumber - 1;
            //updateTableView(TableViewObject,pageNumber);
            searchInDBAndLoadInTableView(nameToSearch,pageNumber);
        }
    }
    @FXML
    public void onFindRecipeByNameClick(){
        nameToSearch = nameToSearchTextField.getText();
        if(nameToSearch.isBlank()) nameToSearch = null;
        System.out.println(nameToSearch); //solo per debug sarà da togliere
        pageNumber = 0;
        searchInDBAndLoadInTableView(nameToSearch,pageNumber);
    }
    public void searchInDBAndLoadInTableView(String nameToSearch, Integer pageNumber){
        Document recipeDoc;
        try (MongoClient mongoClient = MongoClients.create(Configuration.MONGODB_URL)) {
            MongoDatabase database = mongoClient.getDatabase(Configuration.MONGODB_DB);
            MongoCollection<Document> collection = database.getCollection(Configuration.MONGODB_RECIPE);
            MongoCursor<Document> cursor;
            //Bson filter = Filters.regex("Name", "^(?)" + nameToSearch); //da togliere era il vecchio filtro
            Bson filter = new Document("Name",new Document("$regex",nameToSearch).append("$options","i"));
            Bson match = match(filter);
            Bson project = project(new Document("Name",1).append("AuthorName",1)
                    .append("Images", new Document("$first","$Images")));
            if(nameToSearch == null){
                cursor = collection.aggregate(Arrays.asList(skip(10*pageNumber),limit(10),project)).iterator();
            }else{
                cursor = collection.aggregate(Arrays.asList(match,skip(10*pageNumber),limit(10),project)).iterator();
                System.out.println(nameToSearch);
            }
            TableViewObject.resetObservableArrayList();
            while (cursor.hasNext()){
                recipeDoc = cursor.next();
                Recipe recipe = new Recipe(recipeDoc.getString("Name"),
                        recipeDoc.getString("AuthorName"),new ClassForTableView.CustomImage(new ImageView(recipeDoc.getString("Images"))).getImage());
                TableViewObject.addToObservableArrayList(recipe);
            }
            TableViewObject.setItems();
        }
    }

    //alla fine printDocuments sarà inutile, da togliere in ultimo
    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson());
    }

    public void cambiaSchermata(ActionEvent actionEvent,String nomeSchermata) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(nomeSchermata));
        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (DataSingleton.getInstance().getAuthorPromotion() == 1) {
            System.out.println("Scegli se accettare o meno la promozione");
            Label notifyAuthor = new Label("Scegli con i pulsanti qui sotto se accettare o meno la promozione");
            Button rejectPromotionButton = new Button("REJECT PROMOTION");
            Button acceptPromotionButton = new Button("ACCEPT PROMOTION");
            notifyAuthor.setLayoutX(780);
            notifyAuthor.setLayoutY(320);
            rejectPromotionButton.setLayoutX(750);
            rejectPromotionButton.setLayoutY(340);
            acceptPromotionButton.setLayoutX(870);
            acceptPromotionButton.setLayoutY(340);
            for (int i = 0; i < 2; i++) {
                Button currentButton;
                int promotion;
                if (i == 0) {
                    currentButton = rejectPromotionButton;
                    promotion = 0;
                }
                else {
                    currentButton = acceptPromotionButton;
                    promotion = 2;
                    //da mettere qui l'evento che fa inserire le credenziali all'autore come nuovo moderatore
                }
                currentButton.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
                    try (MongoClient mongoClient = MongoClients.create(Configuration.MONGODB_URL)) {
                        MongoDatabase database = mongoClient.getDatabase(Configuration.MONGODB_DB); //da scegliere il nome uguale per tutti
                        MongoCollection<Document> collectionAuthor = database.getCollection(Configuration.MONGODB_AUTHOR);
                        Document query = new Document().append("authorName", DataSingleton.getInstance().getAuthorName());
                        Bson updates = Updates.combine(
                                Updates.set("promotion", promotion)
                        );
                        UpdateOptions options = new UpdateOptions().upsert(true);
                        try {
                            UpdateResult result = collectionAuthor.updateOne(query, updates, options);
                            System.out.println("Modified document count: " + result.getModifiedCount());
                        } catch (MongoException me) {
                            System.err.println("Unable to update due to an error: " + me);
                        }
                    }
                    for (int j = 0;j < 3; j++) anchorPane.getChildren().remove(1); //questo for elimina i 2 Button e la Label della promozione
                });
            }
            anchorPane.getChildren().add(1,notifyAuthor);
            anchorPane.getChildren().add(2,rejectPromotionButton);
            anchorPane.getChildren().add(3,acceptPromotionButton);
        }
        createTableView(TableViewObject);
    }

    public void createTableView (ClassForTableView TableViewObject) {
        TableViewObject.initializeTableView("Loggato");
        searchInDBAndLoadInTableView(nameToSearch,pageNumber);
        TableViewObject.setEventForTableCells();
        TableViewObject.setTabellaDB();
        anchorPane.getChildren().add(TableViewObject.getTabellaDB());
    }

    @FXML
    public void onAddRecipeClick(ActionEvent actionEvent) throws IOException {
        cambiaSchermata(actionEvent,"AddRecipe.fxml");
    }

    public void onPersonalProfileClick(ActionEvent actionEvent) throws IOException {
        cambiaSchermata(actionEvent,"AuthorProfile.fxml");
    }
}