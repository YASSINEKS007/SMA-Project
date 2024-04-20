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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);
        Label bookName = new Label("Book Name");
        TextField bookNameField = new TextField();
        Button OK = new Button("OK");

        vBox.getChildren().addAll(bookName, bookNameField, OK);
        root.setTop(vBox);

        observableListData = FXCollections.observableArrayList();
        listViewMessages = new ListView<String>(observableListData);
        HBox hboxMessages = new HBox();
        hboxMessages.setPadding(new Insets(10, 10, 10, 10));
        hboxMessages.setSpacing(10);
        hboxMessages.getChildren().add(listViewMessages);
        root.setCenter(hboxMessages);

        OK.setOnAction(evt -> {
            String bookname = bookNameField.getText();
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(bookname);

            consumerAgent.onGuiEvent(guiEvent);
        });

        Scene scene = new Scene(root, 600, 400);
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
            observableListData.add(aclMessage.getSender().getLocalName() + " the best price for the book is  " + aclMessage.getContent());
        });
    }
}
