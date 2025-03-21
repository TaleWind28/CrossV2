package Communication.Protocols;

import Communication.Messages.Message;

public interface Protocol {
    //definisco come invio i messaggi
    public int sendMessage(Message message);
    //definisco come ricevo i messaggi
    public Message receiveMessage();
    //definisco un metodo per la chiusura in sicurezza del protocollo
    public void close();
}
