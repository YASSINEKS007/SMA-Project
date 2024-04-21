package ma.enset.project_sma_final.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {

    protected ConsumerAgent consumerAgent;
    public ObservableList<String> observableListData;
    public ListView<String> listViewMessages;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        startContainer();
        stage.setTitle("Consumer Container");
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");

        // Top Pane
        VBox topPane = new VBox();
        topPane.setPadding(new Insets(10));
        topPane.setSpacing(10);

        Label bookName = new Label("Book Name");
        bookName.setStyle("-fx-font-weight: bold;");
        TextField bookNameField = new TextField();
        bookNameField.setPromptText("Enter book name");
        Button OK = new Button("OK");
        OK.getStyleClass().addAll("button", "ok-button");
        HBox.setMargin(OK, new Insets(0, 0, 0, 10));

        topPane.getChildren().addAll(bookName, bookNameField, OK);
        root.setTop(topPane);

        // Center Pane
        observableListData = FXCollections.observableArrayList();
        listViewMessages = new ListView<>(observableListData);
        listViewMessages.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        VBox.setVgrow(listViewMessages, javafx.scene.layout.Priority.ALWAYS);

        VBox centerPane = new VBox(listViewMessages);
        centerPane.setPadding(new Insets(10));
        centerPane.setSpacing(10);
        root.setCenter(centerPane);

        // OK Button Action
        OK.setOnAction(evt -> {
            String bookname = bookNameField.getText();
            if (!bookname.isEmpty()) {
                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(bookname);

                consumerAgent.onGuiEvent(guiEvent);
                bookNameField.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a book name!");
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/css/consumer.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void startContainer() {
        try {
            Runtime runtime = Runtime.instance();
            ProfileImpl profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            AgentContainer container = runtime.createAgentContainer(profile);
            AgentController consumerController = container.createNewAgent("consumer",
                    ConsumerAgent.class.getName(),
                    new Object[]{this});
            consumerController.start();
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }

    public void logMessages(ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableListData.add(aclMessage.getSender().getLocalName() + " => "+ "the best price for the book is  " + aclMessage.getContent());
        });
    }
}
