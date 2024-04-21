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
    private int counter = 0;
    private List<ACLMessage> replies = new ArrayList<>();

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            bookBuyerContainer = (BookBuyerContainer) getArguments()[0];
            bookBuyerContainer.bookBuyerAgent = this;
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
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
                            bookBuyerContainer.logMessages(aclMessage);
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
                            if (counter == sellers.length) {
                                ACLMessage bestOffer = replies.get(0);
                                double minPrice = Double.parseDouble(replies.get(0).getContent());
                                for (ACLMessage offer : replies) {
                                    double price = Double.parseDouble(offer.getContent());
                                    System.out.println(offer.getSender() + " proposes the price :  " + price);
                                    if (price < minPrice) {
                                        bestOffer = offer;
                                        minPrice = price;
                                    }
                                }


                                for (ACLMessage offer : replies) {
                                    bookBuyerContainer.logMessagesPrice(" I offre the price " , offer);
                                    ACLMessage aclMessageRefuse = offer.createReply();
                                    aclMessageRefuse.setContent("Proposal Refused");
                                    aclMessageRefuse.setPerformative(ACLMessage.REFUSE);
                                    if (Double.parseDouble(offer.getContent()) > minPrice) {
                                        send(aclMessageRefuse);
                                    } else {
                                        ACLMessage aclMessageAccept = offer.createReply();
                                        aclMessageAccept.setContent("Proposal Accepted");
                                        aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                        send(aclMessageAccept);
                                    }

                                }
                                bookBuyerContainer.logMessagesString( " proposes the best price :  "+ bestOffer.getContent(), bestOffer);
                                counter = 0;
                                replies.clear();
                            }
                            break;

                        case ACLMessage.AGREE:
                            ACLMessage aclMessage2 = aclMessage.createReply();
                            aclMessage2.setPerformative(ACLMessage.CONFIRM);
                            aclMessage2.addReceiver(new AID("consumer", AID.ISLOCALNAME));
                            aclMessage2.setContent(aclMessage.getContent());
                            send(aclMessage2);
                            break;

                        case ACLMessage.REFUSE:
                            break;
                    }

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
    protected void onGuiEvent(GuiEvent evt) {
        if (evt.getType() == 1) {
            String msgBookName = (String) evt.getParameter(0);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(msgBookName);
            aclMessage.addReceiver(new AID("BookBuyerAgent", AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}
