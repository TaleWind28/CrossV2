package ServerTasks;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import Communication.Message;
import Communication.Protocol;
import Users.Commands.*;
import Users.Commands.Factory.FactoryRegistry;
import Executables.ServerMain;

public class GenericTask implements Runnable {
    private Socket client;
    private long CONNECTION_TIMEOUT = 60;//tempo in secondi
    //private GameFactory factory;
    private ServerMain generatorServer;
    private ScheduledExecutorService timeoutScheduler;
    private ScheduledFuture<?> timeoutTask;
    private Protocol protocol;
    public volatile String onlineUser = new String();
    public String welcomeMessage = "Per fare trading inserire un ordine di qualunque tipo";
    
    public GenericTask(Socket client_socket,Protocol protocol) throws Exception{
        super();
        this.client = client_socket;
        this.protocol = protocol;
        protocol.setSender(client_socket);
        protocol.setReceiver(client_socket);
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
        //this.generatorServer = null; 
    }
    //overloading
    public GenericTask(Socket client_socket,ServerMain server,Protocol protocol) throws Exception{
        super();
        this.client = client_socket;
        this.protocol = protocol;
        this.protocol.setSender(client_socket);
        this.protocol.setReceiver(client_socket);
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
        this.generatorServer = server; 
    }
    //overloading
    public GenericTask(Socket client_socket,String delimiter,Protocol protocol) throws Exception{
        super();
        this.protocol = protocol;
        this.client = client_socket;
        protocol.setSender(client_socket);
        protocol.setReceiver(client_socket); 
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void run(){
        //creo la task di disconnessione
        DisconnectTask inactivityDisconnection = new DisconnectTask(this.protocol,this.client,this.generatorServer,this);
        //invio il messaggio di benvenuto
        protocol.sendMessage(new Message(welcomeMessage,200));
        try{
            while(!(Thread.currentThread().isInterrupted())){
                //this.onlineUser = "pippo";
                //avvio timeout inattività
                this.timeoutTask = this.timeoutScheduler.schedule(inactivityDisconnection, CONNECTION_TIMEOUT, TimeUnit.SECONDS);
                //recupero il messaggio del client
                Message clientRequest = protocol.receiveMessage();
                //azzero il timeout
                this.timeoutTask.cancel(false);
                this.timeoutTask = null;
                //stampa il contenuto del messaggio ricevuto
                System.out.println("run:"+clientRequest.payload.toString());
                //reagisci al messaggio
                this.serverReact(clientRequest);
            }
        }
        catch(IllegalStateException e){
            System.out.println("chiudo tutto");
            return;
        }
        catch(NullPointerException e){
            this.generatorServer.onClientDisconnect(client, "Disconnessione Client");
            return;
        }
        catch(Exception e){
            System.out.println(e.getClass()+" : "+e.getCause()+" Stack:"+e.getStackTrace()+":"+e.getLocalizedMessage()+" : "+e.getSuppressed());
            return;
        }
    }

    public void serverReact(Message clientRequest){
        try{
            //stampa di debug
            System.out.println("richiesta factory: "+clientRequest.payload);
            //creo il comando richiedendo la factory
            UserCommand cmd = FactoryRegistry.getFactory(clientRequest.code).createUserCommand(clientRequest.payload.split(" "));
            //stampa di debug
            System.out.println("Comando fabbricato: "+cmd.toString());
            //ottengo la risposta per il client eseguendo il comando creato dalla factory
            Message responseMessage = cmd.execute(this);
            //Stampa di debug -> risposta del server
            System.out.println("Messaggio generato:\nPayload: "+responseMessage.payload+", code: "+responseMessage.code);
            //invio il messaggio al client
            protocol.sendMessage(responseMessage);
            //stampa di debug
            System.out.println("messaggio inviato");
            return;
        }
        catch(Exception e){
            protocol.sendMessage(new Message("[400]: Comando non correttamente formulato, digitare aiuto per una lista di comandi disponibili",400));
        }
    }

    public synchronized String getOnlineUser(){
        return this.onlineUser;
    }
}


