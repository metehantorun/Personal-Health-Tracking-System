package com.example.kisiselsagliktakipsistem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent fxmlContent = loader.load();

        Image background = new Image(getClass().getResource("/images/arkaplan.png").toExternalForm());
        ImageView backgroundView = new ImageView(background);
        backgroundView.setFitWidth(1100);
        backgroundView.setFitHeight(700);
        backgroundView.setPreserveRatio(false);

        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundView, fxmlContent);

        Scene scene = new Scene(root, 1100, 700);

        Image icon = new Image(getClass().getResourceAsStream("/images/app_logo.png"));
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("Kişisel Sağlık Takip Sistemi");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
