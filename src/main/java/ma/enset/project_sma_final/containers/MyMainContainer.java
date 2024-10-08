package ma.enset.project_sma_final.containers;

import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ControllerException;

public class MyMainContainer {
    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.instance();
            ProfileImpl profile = new ProfileImpl();
            profile.setParameter(Profile.GUI, "true");
            AgentContainer mainContainer = runtime.createMainContainer(profile);
            mainContainer.start();
        } catch (ControllerException e) {
            throw new RuntimeException(e);
        }
    }
}
