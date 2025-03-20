package ServerTasks;

import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import Communication.Messages.ClientMessage;
import Communication.Messages.ServerMessage;
import Communication.Protocols.Protocol;
import Communication.Protocols.TCP;
import Communication.Protocols.UDP;
import Executables.ServerMain;
import JsonAccessedData.JsonAccessedData;
import Utils.AnsiColors;

public class GenericTask implements Runnable {
    private Socket client;
    private long CONNECTION_TIMEOUT = 60;//tempo in secondi
    private ServerMain generatorServer;
    private ScheduledExecutorService timeoutScheduler;
    private ScheduledFuture<?> timeoutTask;
    private TCP protocol;
    public volatile String onlineUser = new String();
    public UDP UDPsender;
    public int tid;
    private String printScope;
    public String welcomeMessage = "Per fare trading inserire un ordine di qualunque tipo";
    private String nonLoggedUserMessage = "---------------------------------------------------------------------------------------------------\n"+
                        "Comandi:\n" + 
                        "register<username,password> -> ti permette di registrarti per poter accedere al servizio di trading\n" + 
                        "login<username,password> -> permette di accedere ad un account registrato\n" +
                        "updateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\n"+
                        "---------------------------------------------------------------------------------------------------";
    private String loggedUserMessage = "------------------------------------------------------------------------------------------------------------\n"+
                        "Comandi:\n"+
                        "logout<username> -> permette di uscire dal servizio di trading\n"+
                        "showorderbook -> permette di visualizzare l'orderbook\n"+
                        "showstoporder -> permette di visualizzare gli stoporder precedentemente piazzati"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> -> inserisce un marketorder\n"+
                        "insertlimitorder <ask/bid> <qtà di bitcoin da vendere/comprare> <limitprice> -> inserisce un limitorder\n"+
                        "insertstoporder <ask/bid> <qtà di bitcoin da vendere/comprare> <stopprice> -> inserisce uno stoporder\n"+
                        "cancelorder <orderID> -> cancella un ordine piazzato dall'utente\n"+
                        "getpricehistory <data in formato numerico MMYYYY> -> fa visualizzare tutti gli scambi del mese desiderato\n"+
                        "------------------------------------------------------------------------------------------------------------";

    String currentHelpMessage = "";
    //costruttore per StopOrderChecker
    public GenericTask(ServerMain server){
        this.generatorServer = server;
        this.UDPsender = server.getUDPListner();
        this.tid = server.getActiveClients().size();
        this.printScope = "[GenericTask - "+this.tid+"]";
    }

    //costruttore
    public GenericTask(Socket client_socket,ServerMain server,Protocol protocol) throws Exception{
        super();
        this.client = client_socket;
        this.protocol = (TCP)protocol;
        this.protocol.setSender(client_socket);
        this.protocol.setReceiver(client_socket);
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
        this.generatorServer = server;
        this.UDPsender = this.generatorServer.getUDPListner();
        this.tid = server.getActiveClients().size();
        this.printScope = "[GenericTask - "+this.tid+"]";
    }

    public void run(){
        //creo la task di disconnessione
        DisconnectTask inactivityDisconnection = new DisconnectTask(this.protocol,this.client,this.generatorServer,this);
        //UDP UDPListner = this.generatorServer.getUDPListner();
        //invio l'UDP Listner
        protocol.sendMessage(new ServerMessage(UDPsender.toBuilderString(),999));
        //invio il messaggio di benvenuto
        protocol.sendMessage(new ServerMessage(welcomeMessage,200));
        //UDPListner.sendMessage(new UDPMessage("Prova Multicast",this.onlineUser));
        try{
            while(!(Thread.currentThread().isInterrupted())){
                // Controlla se ci sono messaggi di sistema (non bloccante)

                /*AVVIO TIMEOUT */
                this.timeoutTask = this.timeoutScheduler.schedule(inactivityDisconnection, CONNECTION_TIMEOUT, TimeUnit.SECONDS);
                
                /*RICEVO IL MESSAGGIO DEL CLIENT CHE STO GESTENDO */
                ClientMessage clientRequest = (ClientMessage)protocol.receiveMessage();
                
                /*AZZERO IL TIMEOUT */
                this.timeoutTask.cancel(false);

                /*PREPARO I DATI PER LA EXECUTE*/
                JsonAccessedData neededData = this.serverReact(clientRequest);
                
                /*ESECUZIONE COMANDO */
                ServerMessage responseMessage = clientRequest.values.execute(neededData, this.onlineUser, this);
                
                /*CONTROLLI PRE-INVIO RISPOSTA */
                if(responseMessage.response == 100 && this.onlineUser.equals("") && clientRequest.operation.equals("login")){
                    this.onlineUser = clientRequest.values.getUsername();
                    System.out.println(AnsiColors.ORANGE+this.printScope+" Username: "+this.onlineUser+AnsiColors.RESET);
                }
                
                //invio il messaggio al client
                protocol.sendMessage(responseMessage);
                
                System.out.println(AnsiColors.ORANGE+this.printScope+" messaggio inviato: \n"+responseMessage.toString()+AnsiColors.RESET);
                System.out.println(AnsiColors.ORANGE+"//////////////////////////////////////////////////////////////////////////////////"+AnsiColors.RESET);

                /*CONTROLLI POST-INVIO RISPOSTA*/
                if (responseMessage.response == 100 && (clientRequest.operation.equals("logout") || clientRequest.operation.toLowerCase().equals("exit"))){
                    this.protocol.close();
                    this.generatorServer.onClientDisconnect(client, this.printScope+" disconnessione richiesta dall'utente");
                    return;
                }

            }
            this.protocol.sendMessage(new ServerMessage("chiusura forzata",408));
            this.protocol.close();
            Thread.currentThread().interrupt();
        }
        catch(IllegalStateException e){
            System.out.println("chiudo tutto");
            this.protocol.close();
            this.generatorServer.onClientDisconnect(client, currentHelpMessage);
            return;
        }
        catch(NullPointerException e){
            this.generatorServer.onClientDisconnect(client, "Disconnessione Client");
            this.protocol.close();
            //Thread.currentThread().interrupt();
            System.out.println(this.printScope+"terminato");
            return;
        }
        catch(Exception e){
            System.out.println(e.getClass()+" : "+e.getCause()+" Stack:"+e.getStackTrace()+":"+e.getLocalizedMessage()+" : "+e.getSuppressed());
            return;
        }
    }

    public JsonAccessedData serverReact(ClientMessage clientRequest){
        try{
            //setto l'username per i comandi           
            if(clientRequest.operation.toLowerCase().contains("order"))clientRequest.values.setUsername(this.onlineUser);
            //stampa di debug
            System.out.println(this.printScope+" Comando fabbricato: "+clientRequest.toString());
            //controllo la struttura dati da assegnare al comando
            if(clientRequest.operation.contains("order"))return this.generatorServer.getOrderbook();
            else if(clientRequest.operation.contains("getpricehistory"))return this.generatorServer.getStorico();
            //else return null; 
            return this.generatorServer.getRegisteredUsers();
        }
        catch(Exception e){
            return null;
        }
    }

    public synchronized String getOnlineUser(){
        return this.onlineUser;
    }

    public synchronized int getProgressiveOrderNumber(){
        return this.generatorServer.getProgressiveOrderNumber();
    }

    public String getHelpMessage(){
        //imposto il messaggio di help
        if(this.onlineUser.equals(""))return nonLoggedUserMessage;
        else return loggedUserMessage;
    }

    public synchronized void increaseProgressiveOrderNumber(){
        this.generatorServer.increaseProgressiveOrderNumber();
    }
}


