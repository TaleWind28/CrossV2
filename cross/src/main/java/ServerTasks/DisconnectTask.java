package ServerTasks;

import java.net.Socket;

import ClientFactories.OrderFactory;
import Communication.Message;
import Communication.Protocol;
import Communication.ServerMessage;
import Communication.ServerProtocol;
import Communication.Values;


public class DisconnectTask implements Runnable{
    private Protocol protocol;
    private Socket socket;
    private ServerProtocol server;
    private GenericTask handlerClient;

    public DisconnectTask(Protocol proto, Socket socket, ServerProtocol server, GenericTask hanlder){
        this.protocol = proto;
        this.socket = socket;
        this.server = server;
        this.handlerClient = hanlder;
    }

    public void run(){
        try {
            //recupero l'utente attualmente loggato
            String onlineUser = handlerClient.getOnlineUser();
            //se esiste effettuo un logout prima di dsiconnettere il client
            if(!onlineUser.equals("")){
                String[] cmd = {"logout",onlineUser};
                OrderFactory ordf = new OrderFactory();
                Values command = ordf.createValue(cmd);
                command.execute(null);             
                //FactoryRegistry.getFactory(0).createUserCommand(cmd).execute(handlerClient);
            }
            //creo il messaggio per comunicare al client la sua disconnessione
            Message clientMessage = new ServerMessage("Timeout di inattività. Disconnessione.",408);
            //invio il messaggio al client
            this.protocol.sendMessage(clientMessage);
            //avvio la procedura di disconnessione client sul server per chiudere il socket
            this.server.onClientDisconnect(socket,"Client disconnesso per inattività");
        }catch(Exception e){
            //System.out.println(e.getMessage());
        }
    };
}
