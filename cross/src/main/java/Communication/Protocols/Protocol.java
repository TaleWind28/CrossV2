package Communication.Protocols;

import java.net.Socket;

import Communication.Message;

public interface Protocol {
    //istanzio lo stream per la ricezione dei messaggi
    public void setReceiver(Socket input);
    //istanzio lo stream per l'invio dei messaggi
    public void setSender(Socket output);
    //definisco come invio i messaggi
    public int sendMessage(Message message);
    //definisco come ricevo i messaggi
    public Message receiveMessage();
    // public ClientMessage receiveClientMessage();

    // public ServerMessage receiveServerMessage();
}
