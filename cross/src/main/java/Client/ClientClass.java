package Client;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;

import Client.ClientTask.UDPReceiverTask;
import Commands.CommandFactory;
import Commands.Credentials.Disconnect;
import Communication.Messages.ClientMessage;
import Communication.Messages.ServerMessage;
import Communication.Protocols.ClientProtocol;
import Communication.Protocols.TCP;
import Communication.Protocols.UDP;
import Utils.AnsiColors;

public class ClientClass extends ClientProtocol{
    public volatile boolean canSend = true;
    public Socket sock = null;
    public String helpMessage = "Comandi:\nregister<username,password> -> ti permette di registrarti per poter accedere al servizio di trading\nlogin<username,password> -> permette di accedere ad un account registrato\nupdateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\nlogout<username> -> permette di uscire dal servizio di trading";
    public CommandFactory factory;
    public UDP UDPUpdater;
    public Thread UDPReceiver;
    public volatile String onlineUser = "";
    public volatile String cmdSent = "";
    private volatile boolean sigintTermination = false;

    public ClientClass(String IP, int PORT){
        super(IP,PORT);
        this.canSend = false;   
        this.factory = new CommandFactory();

    }

    public void receiveBehaviour(){
        try{
            while(true){
                ServerMessage serverAnswer = (ServerMessage)this.protocol.receiveMessage();
                //controllo risposta server
                switch (serverAnswer.response) {
                    case 999://configurazione interna al server dell'UDPListner
                        this.UDPUpdater = UDP.buildFromString(serverAnswer.errorMessage);
                        this.UDPReceiver = new Thread(new UDPReceiverTask(UDPUpdater, this));
                        this.UDPReceiver.start();
                        continue;
                    case 100:
                        if (cmdSent.toLowerCase().equals("logout")){
                            System.out.println("Chiusura Connessione"+AnsiColors.RESET);
                            this.sock.close();
                            this.protocol.close();
                            System.exit(0);
                        }
                        break;
                    //disconnessione
                    case 408:
                        System.out.println(serverAnswer.errorMessage);
                        
                        if (!this.sigintTermination)System.exit(0);;
                        System.out.println("termino con ctrl+c");
                        this.protocol.close();
                        return;
                    //default
                    default:
                        if(this.cmdSent.equals("login"))this.onlineUser = "";
                        break;
                }
                System.out.println("[ServerAnswer]"+serverAnswer.toString());        
                this.canSend = true;
                                    
            }
        }
        catch(IOException e){
            System.out.println("[ClientReceiver-IOException] "+e.getMessage());
            System.exit(0);
        }
        catch(NullPointerException e){//in caso di sigint sul server viene sollevata questa eccezione
            System.out.println(AnsiColors.RED+"Stiamo riscontrando dei problemi sul server, procederemo a chiudere la connessione, ci scusiamo per il disagio"+AnsiColors.RESET);
            System.exit(0);
        }    
    }

    public void sendBehaviour(){
        while(true){
            if(this.canSend){
                System.out.print(AnsiColors.MAGENTA+this.onlineUser+"@>"+AnsiColors.RESET);
                String rawClientRequest =this.userInput.nextLine();
                
                if(rawClientRequest.length()>1){
                    // Trova la posizione del primo spazio nella rischiesta del client
                    int firstSpaceIndex = rawClientRequest.indexOf(" ");
                    if (firstSpaceIndex != -1) {
                        // Ricostruisci la stringa inserendo onlineUser dopo il primo token
                        rawClientRequest = rawClientRequest.substring(0, firstSpaceIndex) + " " + this.onlineUser + rawClientRequest.substring(firstSpaceIndex);
                    }
                }
                
                //splitto la stringa per ottenere il comando da passare alla factory
                String[] clientRequest = rawClientRequest.split(" ");    
                
                //creo la tramite simple factory la richiesta da mandare al server
                ClientMessage userMessage = new ClientMessage(clientRequest[0],this.factory.createValue(clientRequest));

                // if(userMessage.values.getClass()==ErrorMessage.class){
                //     System.out.println(userMessage.values.execute(null, "", null).toString());
                //     continue;
                // }

                //aggiorno il comando inviato per controllare sul receiver
                this.cmdSent = userMessage.operation;
                //controllo il caso di messaggio di aiuto
                if(userMessage.operation.equals("")|| userMessage.operation.equals("aiuto"))userMessage.operation = "help";
                //imposto l'username corrente
                if(userMessage.operation.equals("login"))this.onlineUser = userMessage.values.getUsername();
                    
                /*INVIO MESSAGGIO */
                this.protocol.sendMessage(userMessage);

                //imposto la variabile a false per impedire di inviare altri messaggi prima che mi ritorni la risposta a quello attuale
                this.canSend = false;
                
                //stampa di debug
                //System.out.println(AnsiColors.ORANGE+"[ClientHelper] Message Sent");
                    
            }
        }
    }

    //dialogo col server sfruttando il multithreading
    public void multiDial(){
        try {
            //apro il socket lato client
            this.sock = new Socket(this.ip,this.port);
            //imposto il protocollo di comunicazione
            this.setProtocol(new TCP());
            //imposto la ricezione e l'invio dei messaggi sul socket TCP
            this.protocol.setReceiver(sock);
            this.protocol.setSender(sock);
            //imposto il thread di ricezione
            this.setReceiverThread();
            //attivo il thread di ricezione
            this.receiverThread.start();
            //faccio eseguire il comportamento di invio dal main thread
            this.sendBehaviour();
        }
        //disconnessione accidentale
        catch (SocketException e) {
            System.out.println("Il server al momento non è disponibile :( , ci scusiamo per il disagio ");
        }
        //input errato
        catch(IOException e){
            System.out.println("C'è stato un'errore sulla lettura dal socket");
        }
        catch(NoSuchElementException e){
            //comunico al client la chiusura imminente
            System.out.println("Ctr+c Rilevato -> disconnessione in corso...");
            //invio al server un messaggio di exit
            this.protocol.sendMessage(new ClientMessage("exit",new Disconnect("disconnect")));
            //imposto la variabile sigintTermination a true per poter sfruttare il shutdownHook sul receiver
            this.sigintTermination = true;
        }
        //eccezione generica
        catch(ArrayIndexOutOfBoundsException e ){        
            System.out.println("[ClientMain]Out of Bounds");
        }
        finally{
            //se il socket non è stato aperto termino direttamente perchè non ho niente da chiudere
            if (this.sock == null)System.exit(0);
            try {       
                //attendo la terminazione del receiver se è in esecuzione
                if(this.receiverThread.isAlive())this.receiverThread.join(0);
                //chiudo il socket
                this.sock.close();
                //interrompo il receiverUDP
                this.UDPReceiver.interrupt();
                //chiudo il protocollo
                this.UDPUpdater.close();
                //this.protocol.close();
                //stampa di debug   
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
