package ServerTasks;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import Commands.Credentials.Login;
import Communication.Messages.ClientMessage;
import Communication.Messages.ServerMessage;
import Communication.Protocols.Protocol;
import Communication.Protocols.UDP;
import Executables.ServerMain;
import JsonUtils.JsonAccessedData;

public class GenericTask implements Runnable {
    private Socket client;
    private long CONNECTION_TIMEOUT = 60;//tempo in secondi
    //private GameFactory factory;
    private ServerMain generatorServer;
    private ScheduledExecutorService timeoutScheduler;
    private ScheduledFuture<?> timeoutTask;
    private Protocol protocol;
    public volatile String onlineUser = new String();
    public UDP UDPsender;
    public String welcomeMessage = "Per fare trading inserire un ordine di qualunque tipo";
    private String nonLoggedUserMessage = "---------------------------------------------------------------------------------------------------\n"+
                        "Comandi:\n" + 
                        "register<username,password> -> ti permette di registrarti per poter accedere al servizio di trading\n" + 
                        "login<username,password> -> permette di accedere ad un account registrato\n" +
                        "---------------------------------------------------------------------------------------------------";
    private String loggedUserMessage = "------------------------------------------------------------------------------------------------------------\n"+
                        "Comandi:\n"+
                        "updateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\n"+
                        "logout<username> -> permette di uscire dal servizio di trading\n"+
                        "showorderbook -> fa visualizzare l'orderbook\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> -> inserisce un marketorder\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> <limitprice> -> inserisce un limitorder\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> <stopprice> -> inserisce uno stoporder\n"+
                        "cancelorder <orderID>\n"+
                        "------------------------------------------------------------------------------------------------------------";

    String currentHelpMessage = "";
    
    //costruttore
    public GenericTask(Socket client_socket,ServerMain server,Protocol protocol) throws Exception{
        super();
        this.client = client_socket;
        this.protocol = protocol;
        this.protocol.setSender(client_socket);
        this.protocol.setReceiver(client_socket);
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
        this.generatorServer = server;
        this.UDPsender = this.generatorServer.getUDPListner();
    }

    public void run(){
        //creo la task di disconnessione
        DisconnectTask inactivityDisconnection = new DisconnectTask(this.protocol,this.client,this.generatorServer,this);
        //invio il messaggio di benvenuto
        //UDP UDPListner = this.generatorServer.getUDPListner();
        protocol.sendMessage(new ServerMessage(UDPsender.toBuilderString(),999));
        
        protocol.sendMessage(new ServerMessage(welcomeMessage,200));
        //UDPListner.sendMessage(new UDPMessage("Prova Multicast",this.onlineUser));
        try{
            while(!(Thread.currentThread().isInterrupted())){
                //avvio timeout inattività
                this.timeoutTask = this.timeoutScheduler.schedule(inactivityDisconnection, CONNECTION_TIMEOUT, TimeUnit.SECONDS);
                //recupero il messaggio del client
                ClientMessage clientRequest = (ClientMessage)protocol.receiveMessage();
                //azzero il timeout
                this.timeoutTask.cancel(false);
                this.timeoutTask = null;
                //imposto il messaggio di errore
                if(this.onlineUser.equals(""))this.currentHelpMessage = nonLoggedUserMessage;
                else this.currentHelpMessage = loggedUserMessage;
                //stampa il contenuto del messaggio ricevuto
                System.out.println("run:"+clientRequest.operation.toString());
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

    public void serverReact(ClientMessage clientRequest){
        try{
            String additionalInfo = this.onlineUser;
            System.out.println("[GenericTask] SendUDP");
            //this.generatorServer.getUDPListner().sendMessage(new ServerMessage("Prova Multicast", getProgressiveOrderNumber()));
            //stampa di debug
            System.out.println("[GenericTask] Operation: "+clientRequest.operation+"\nValues: "+clientRequest.values.toString());
            // //setto l'username per i comandi           
            if(clientRequest.operation.toLowerCase().contains("order"))clientRequest.values.setUsername(this.onlineUser);
            //stampa di debug
            System.out.println("[GenericTask] Comando fabbricato: "+clientRequest.toString());
            //dati in formato json per controllare gli utenti
            JsonAccessedData data = this.generatorServer.getRegisteredUsers();
            //controllo la struttura dati da assegnare al comando
            if(clientRequest.operation.contains("order"))data = this.generatorServer.getOrderbook();
            
            //controllo le informazioni addizionali da passare al comando
            if(clientRequest.operation.equals("help"))additionalInfo = this.currentHelpMessage;
            
            /*ESECUZIONE COMANDO */

            //ottengo la risposta per il client eseguendo il comando creato dalla factory
            ServerMessage responseMessage = clientRequest.values.execute(data,additionalInfo,this);
            
            /*FINE ESECUZIONE COMANDO */
            
            if(responseMessage.response == 200 && this.onlineUser.equals("") && clientRequest.operation.equals("login")){
                Login log = (Login)clientRequest.values;
                this.onlineUser = log.getUsername();
                System.out.println("[GenericTask] Username: "+log.getUsername());
            }

            //Stampa di debug -> risposta del server
            System.out.println("[GenericTask] Messaggio generato:\n"+responseMessage.toString());
            //invio il messaggio al client
            protocol.sendMessage(responseMessage);
            //stampa di debug
            System.out.println("[GenericTask] messaggio inviato");
            return;
        }
        catch(Exception e){
            protocol.sendMessage(new ServerMessage("[400]: Comando non correttamente formulato, digitare aiuto per una lista di comandi disponibili",400));
        }
    }

    public synchronized String getOnlineUser(){
        return this.onlineUser;
    }

    public synchronized int getProgressiveOrderNumber(){
        return this.generatorServer.getProgressiveOrderNumber();
    }

    public synchronized void increaseProgressiveOrderNumber(){
        this.generatorServer.increaseProgressiveOrderNumber();
    }
}


