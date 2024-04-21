package ma.enset.project_sma_final.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
    protected ConsumerContainer consumerContainer;

    @Override
    protected void setup() {
        String bookName = null;
        if(this.getArguments().length == 1){
            consumerContainer = (ConsumerContainer) getArguments()[0];
            consumerContainer.consumerAgent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null) {
                    switch (msg.getPerformative()){
                        case ACLMessage.CONFIRM:
                            consumerContainer.logMessages(msg);
                            break;
                    }
                }
                else{
                    block();
                }
            }
        });
    }



    @Override
    protected void onGuiEvent(GuiEvent evt) {
        if (evt.getType() == 1){
            String bookName = (String) evt.getParameter(0);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent("I request the book : " + bookName);
            aclMessage.addReceiver(new AID("BookBuyerAgent", AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}
