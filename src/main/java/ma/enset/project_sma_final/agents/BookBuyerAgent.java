package ma.enset.project_sma_final.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class BookBuyerAgent extends GuiAgent {

    protected BookBuyerContainer bookBuyerContainer;

    protected AID[] sellers;

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            bookBuyerContainer = (BookBuyerContainer) getArguments()[0];
            bookBuyerContainer.bookBuyerAgent = this;
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int counter;
            private List<ACLMessage> replies = new ArrayList<ACLMessage>();
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                                )
                        )
                );

                ACLMessage aclMessage = receive(messageTemplate);

                if (aclMessage != null) {

                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.REQUEST:
                            String book = aclMessage.getContent();
                            ACLMessage aclMessage1 = new ACLMessage(ACLMessage.CFP);
                            aclMessage1.setContent(book);
                            for (AID sellerAID : sellers) {
                                aclMessage1.addReceiver(sellerAID);
                            }
                            send(aclMessage1);
                            break;


                        case ACLMessage.PROPOSE:
                            ++counter;
                            replies.add(aclMessage);
                            if(counter == sellers.length) {
                                ACLMessage bestOffre = replies.get(0);
                                double mini = Double.parseDouble(replies.get(0).getContent());
                                for(ACLMessage offre: replies){
                                    double price = Double.parseDouble(offre.getContent());
                                    System.out.println(bestOffre.getSender() + " proposes the price :  " + price);
                                    if(price < mini) {
                                        bestOffre = offre;
                                        mini = price;
                                    }
                                }

                                System.out.println(bestOffre.getSender() + " proposes the best price :  " + mini);

                                ACLMessage aclMessageAccept = bestOffre.createReply();
                                aclMessageAccept.setContent("Accepted");
                                aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(aclMessageAccept);

                            }

                            break;
                        case ACLMessage.AGREE:
                            ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage2.addReceiver(new AID("consumer", AID.ISLOCALNAME));
                            aclMessage2.setContent(aclMessage.getContent());
                            System.out.println("Agree message : " + aclMessage.getContent());
                            send(aclMessage2);

                            break;
                        case ACLMessage.REFUSE:
                            break;

                    }
                    String bookPrice = aclMessage.getContent();
                    bookBuyerContainer.logMessages(aclMessage);
                    ACLMessage aclMessageReply = aclMessage.createReply();
                    aclMessageReply.setPerformative(ACLMessage.INFORM);
                    aclMessageReply.setContent("Offre Received for the price " + bookPrice);
                    aclMessageReply.setSender(bookBuyerContainer.bookBuyerAgent.getAID());
                    send(aclMessageReply);


                } else {
                    block();
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                try {
                    DFAgentDescription dfAgentDescription = new DFAgentDescription();
                    ServiceDescription serviceDescription = new ServiceDescription();
                    serviceDescription.setType("transaction");
                    serviceDescription.setName("Book Selling");
                    dfAgentDescription.addServices(serviceDescription);
                    DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
                    sellers = new AID[results.length];
                    for (int i = 0; i < sellers.length; i++) {
                        sellers[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
