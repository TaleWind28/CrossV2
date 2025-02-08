package ServerTasks;

import java.net.Socket;

<<<<<<< HEAD
import ClientFactories.OrderFactory;
import Communication.Message;
import Communication.Protocol;
import Communication.ServerMessage;
import Communication.ServerProtocol;
import Communication.Values;

=======
import Commands.CommandFactory;
import Communication.Message;
import Communication.Protocol;
import Communication.ServerMessage;
import Executables.ServerMain;
>>>>>>> 3e237bfbb9e4fd2228522158d159d02e8e8819eb

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
<<<<<<< HEAD
                String[] cmd = {"logout",onlineUser};
                OrderFactory ordf = new OrderFactory();
                Values command = ordf.createValue(cmd);
                command.execute(null);             
=======
                //devo passare l'userbook
                new CommandFactory().createValue(cmd).execute(server.getRegisteredUsers(),onlineUser);
                System.out.println("[DisconnectTask] logout elaborato");
>>>>>>> 3e237bfbb9e4fd2228522158d159d02e8e8819eb
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
