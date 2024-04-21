package ma.enset.project_sma_final.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
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

public class SellerContainer1 extends Application {

    protected SellerAgent1 sellerAgent1;
    public ListView<String> listViewMessages;
    public ObservableList<String> observableListData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("Seller 1 Container");
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
            AgentController sellerController = container.createNewAgent("Seller-1",
                    SellerAgent1.class.getName(),
                    new Object[]{this});
            sellerController.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }

    public void logMessages(ACLMessage aclMessage) {
        Platform.runLater(() -> {
            observableListData.add(aclMessage.getSender().getLocalName() + " => " + aclMessage.getContent());

        });
    }

    public void logMessagesPrice(String price) {
        Platform.runLater(() -> {
            observableListData.add("the proposed price for Seller 1 is : " + price);
        });
    }

}
