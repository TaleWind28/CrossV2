package ServerTasks;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import Commands.Credentials.Login;
import Communication.ClientMessage;
import Communication.Protocol;
import Communication.ServerMessage;
import Executables.ServerMain;
import JsonMemories.JsonAccessedData;

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
    private String nonLoggedUserMessage = "Comandi:\n" + 
                        "register<username,password> -> ti permette di registrarti per poter accedere al servizio di trading\n" + 
                        "login<username,password> -> permette di accedere ad un account registrato\n" +
                        "";
    private String loggedUserMessage = "Comandi:\n"+
                        "updateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\n"+
                        "logout<username> -> permette di uscire dal servizio di trading\n"+
                        "showorderbook -> fa visualizzare l'orderbook\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> -> inserisce un marketorder\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> <limitprice> -> inserisce un limitorder\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> <stopprice> -> inserisce uno stoporder\n"+
                        "cancelorder <orderID>\n";
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
    }

    public void run(){
        //creo la task di disconnessione
        DisconnectTask inactivityDisconnection = new DisconnectTask(this.protocol,this.client,this.generatorServer,this);
        //invio il messaggio di benvenuto
        protocol.sendMessage(new ServerMessage(welcomeMessage,200));
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
            String additionalInfo = null;
            //stampa di debug
            System.out.println("[GenericTask] Operation: "+clientRequest.operation+"\nValues: "+clientRequest.values.toString());
            //setto l'username per i comandi           
            clientRequest.values.setUsername(this.onlineUser);
            //stampa di debug
            System.out.println("[GenericTask] Comando fabbricato: "+clientRequest.toString());
            //dati in formato json per controllare gli utenti
            JsonAccessedData data = null;
            //controllo la struttura dati da assegnare al comando
            if(clientRequest.operation.contains("order"))data = this.generatorServer.getOrderbook();
            else data = this.generatorServer.getRegisteredUsers();
            //controllo le informazioni addizionali da passare al comando
            if(clientRequest.operation.equals("help"))additionalInfo = this.currentHelpMessage;
            else additionalInfo = this.onlineUser;
            //ottengo la risposta per il client eseguendo il comando creato dalla factory
            /*ESECUZIONE COMANDO */
            ServerMessage responseMessage = clientRequest.values.execute(data,additionalInfo);
            /*FINE ESECUZIONE COMANDO */
            if(responseMessage.response == 200 && this.onlineUser.equals("") && clientRequest.operation.equals("login")){
                Login log = (Login)clientRequest.values;
                this.onlineUser = log.getUsername();
                System.out.println("[GenericTask] Username: "+log.getUsername());
            }
            //Stampa di debug -> risposta del server
            System.out.println("[GenericTask] Messaggio generato:\nPayload: "+responseMessage.errorMessage+", code: "+responseMessage.response);
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
}


