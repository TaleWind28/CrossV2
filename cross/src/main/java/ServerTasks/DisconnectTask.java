package ServerTasks;

import java.net.Socket;

import Commands.CommandFactory;
import Communication.Messages.Message;
import Communication.Messages.ServerMessage;
import Communication.Protocols.Protocol;
import Executables.ServerMain;

public class DisconnectTask implements Runnable{
    private Protocol protocol;
    private Socket socket;
    private ServerMain server;
    private GenericTask handlerClient;

    public DisconnectTask(Protocol proto, Socket socket, ServerMain server, GenericTask hanlder){
        this.protocol = proto;
        this.socket = socket;
        this.server = server;
        this.handlerClient = hanlder;
    }

    public void run(){
        try {
            //recupero l'utente attualmente loggato
            String onlineUser = handlerClient.getOnlineUser();
            String[] cmd = {"logout",onlineUser};
            //se esiste effettuo un logout prima di disconnettere il client
            if(!onlineUser.equals("")){
                //devo passare l'userbook
                new CommandFactory().createValue(cmd).execute(server.getRegisteredUsers(),onlineUser,this.handlerClient);
                System.out.println("[DisconnectTask] logout elaborato");
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
