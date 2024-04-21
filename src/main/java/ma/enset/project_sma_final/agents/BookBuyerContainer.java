package ma.enset.project_sma_final.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
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
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookBuyerContainer extends Application {

    protected BookBuyerAgent bookBuyerAgent;
    public ListView<String> listViewMessages;
    public ObservableList<String> observableListData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Book Buyer Container");
        BorderPane root = new BorderPane();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        observableListData = FXCollections.observableArrayList();
        listViewMessages = new ListView<String>(observableListData);

        vBox.getChildren().add(listViewMessages);
        root.setCenter(vBox);

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
            AgentController bookBuyerController = container.createNewAgent("BookBuyerAgent",
                    BookBuyerAgent.class.getName(),
                    new Object[]{this});
            bookBuyerController.start();
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }

    public void logMessages(ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableListData.add(aclMessage.getSender().getLocalName() + " => " + aclMessage.getContent());
        });
    }

    public void logMessagesPrice(String msg, ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableListData.add(aclMessage.getSender().getLocalName() + " => " + msg + aclMessage.getContent());
        });
    }

    public void logMessagesString(String msg, ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableListData.add(aclMessage.getSender().getLocalName() + " " + msg + aclMessage.getContent());
        });
    }


}
