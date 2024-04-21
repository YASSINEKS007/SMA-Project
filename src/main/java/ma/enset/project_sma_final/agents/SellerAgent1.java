package ma.enset.project_sma_final.agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class SellerAgent1 extends GuiAgent {

    protected SellerContainer1 sellerContainer;
    String choosenPrice;

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            sellerContainer = (SellerContainer1) getArguments()[0];
            sellerContainer.sellerAgent1 = this;
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    sellerContainer.logMessages(aclMessage);
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CFP:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            choosenPrice = String.valueOf(new Random().nextDouble(10000));
                            reply.setContent(choosenPrice);
                            sellerContainer.logMessagesPrice(choosenPrice);
                            send(reply);
                            break;

                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage aclMessage1 = aclMessage.createReply();
                            aclMessage1.setPerformative(ACLMessage.AGREE);
                            aclMessage1.setContent(choosenPrice);
                            send(aclMessage1);
                    }
                } else {
                    block();
                }
            }

        });


        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    DFAgentDescription dfAgentDescription = new DFAgentDescription();
                    dfAgentDescription.setName(getAID());
                    ServiceDescription serviceDescription = new ServiceDescription();
                    serviceDescription.setType("transaction");
                    serviceDescription.setName("Book Selling");
                    dfAgentDescription.addServices(serviceDescription);
                    DFService.register(myAgent, dfAgentDescription);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
