package Executables;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import Commands.CommandFactory;
import Commands.Credentials.Disconnect;
import Communication.Messages.ClientMessage;
import Communication.Messages.ServerMessage;
import Communication.Messages.UDPMessage;
import Communication.Protocols.ClientProtocol;
import Communication.Protocols.TCP;
import Communication.Protocols.UDP;
import Config.ClientConfig;
import okio.BufferedSource;
import okio.Okio;

public class ClientMain extends ClientProtocol{
    public volatile boolean canSend = true;
    public Socket sock = null;
    public String helpMessage = "Comandi:\nregister<username,password> -> ti permette di registrarti per poter accedere al servizio di trading\nlogin<username,password> -> permette di accedere ad un account registrato\nupdateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\nlogout<username> -> permette di uscire dal servizio di trading";
    public CommandFactory factory;
    public UDP UDPUpdater;
    public Thread UDPReceiver;
    public String onlineUser = "";
    public String cmdSent = "";
    //public CountDownLatch latch = new CountDownLatch(1);
    private volatile boolean sigintTermination = false;
    public ClientMain(String IP, int PORT){
        super(IP,PORT);
        this.canSend = false;   
        this.factory = new CommandFactory();
        this.UDPReceiver = new Thread(() -> {
            while(true){
                UDPMessage message = (UDPMessage)this.UDPUpdater.receiveMessage();
            
                if(this.onlineUser.equals(""))continue;
                else if(!message.getInterestedUser().equals(this.onlineUser))continue;
                else System.out.println("[ReceiverUDP] Received:\n"+message.tradeNotification());
                  
            }
        });

    }
        
    public static void main(String args[]) throws Exception{
        ClientConfig config = getClientConfig();
        ClientMain client = new ClientMain(config.getTCPaddress(),config.getTCPport());
        client.multiDial();
    }
    
    public static ClientConfig getClientConfig(){
        JsonAdapter<ClientConfig> jsonAdapter = new Moshi.Builder().build().adapter(ClientConfig.class);
            // InputStream inputStream = ClientMain.class.getClassLoader().getResourceAsStream("ClientConfig.json");
            // if(inputStream == null)throw new RuntimeException("File di configurazione non trovato");
            try (BufferedSource source = Okio.buffer(Okio.source(ClientMain.class.getClassLoader().getResourceAsStream("ClientConfig.json")))) {
                return jsonAdapter.fromJson(source);
            } catch (Exception e) {
                System.out.println("minosse");
            }
        return null;
    }

    public void receiveBehaviour(){
        // Aggiungi uno shutdown hook alla JVM
        Runtime.getRuntime().addShutdownHook(
            new Thread(
                () -> {
                    //ho bisogno di shutdownhook solo per controllare il logout dell'utente dal server nel caso di sigint
                    if (this.sigintTermination == false)return;
                    try {
                        System.out.println("[TerminationThread] "+this.protocol.receiveMessage().toString());
                    } catch (Exception e) {;}
                }
            )
        );
        
        try{
            while(true){
                ServerMessage serverAnswer = (ServerMessage)this.protocol.receiveMessage();
                //controllo risposta server
                switch (serverAnswer.response) {
                    case 999://configurazione interna al server dell'UDPListner
                        this.UDPUpdater = UDP.buildFromString(serverAnswer.errorMessage);
                        this.UDPReceiver.start();
                        continue;
                    case 100:
                        if (serverAnswer.errorMessage.contains("Disconnessione avvenuta con successo")){
                            System.out.println("Chiusura Connessione");
                            this.sock.close();
                            this.protocol.close();
                            System.exit(0);
                        }
                        System.out.print("[TCPReceiver]"+serverAnswer.toString()+"\n>>");
                        
                        this.canSend = true;
                        continue;
                    //disconnessione
                    case 408:
                        System.out.println(serverAnswer.errorMessage);
                        if (!this.sigintTermination)System.exit(0);
                        return;
                    //default
                    default:
                        if(this.cmdSent.equals("login"))this.onlineUser = "";
                        System.out.print("[Server]"+serverAnswer.toString()+"\n >>");
                        this.canSend = true;
                        continue;
                }            
            }
        }
        catch(IOException e){
            System.out.println("[ClientReceiver-IOException] "+e.getMessage());
            System.exit(0);
        }
        catch(Exception e){
            System.out.println("[ClientReceiver]"+e.getMessage()+"\n"+e.getClass()+"\n"+e.getCause()+"\n"+e.getSuppressed());
            System.out.println("Stiamo riscontrando dei problemi sul server, procederemo a chiudere la connessione, ci scusiamo per il disagio");
            System.exit(0);
        }    
    }

        public void sendBehaviour(){
            while(true){
                if(this.canSend){
                    String rawClientRequest = this.userInput.nextLine();
                
                    String[] clientRequest = rawClientRequest.split(" ");
                    ClientMessage userMessage = new ClientMessage(clientRequest[0],this.factory.createValue(clientRequest));
                    if(userMessage.operation.equals("")|| userMessage.operation.equals("aiuto"))userMessage.operation = "help";
                    //System.out.println("[CLIENTMAIN]"+userMessage.toString());
                    System.out.println("[ClientHelper] Message Sent");
                    if(userMessage.operation.equals("login"))this.onlineUser = userMessage.values.getUsername();
                    this.protocol.sendMessage(userMessage);
                    
                    this.canSend = false;
                }
            }
        }
        //dialogo col server sfruttando il multithreading
        public void multiDial(){
            try {
                //apro il socket lato client
                this.sock = new Socket(this.ip,this.port);
                //stampa di debug
                System.out.println("socket"+this.sock.toString());
                //imposto il protocollo di comunicazione
                this.setProtocol(new TCP());
                //imposto receiver, sender e receiverThread
                this.protocol.setReceiver(sock);
                this.protocol.setSender(sock);
                this.setReceiverThread();
                //attivo il thread di ricezione
                this.receiverThread.start();
                //faccio eseguire il comportamento di invio dal main thread
                this.sendBehaviour();
            }
            //disconnessione accidentale
            catch (SocketException e) {
                //System.out.println(e.getClass());
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
            catch(Exception e ){
                
                System.out.println("eccezione: "+e.getClass()+" : "+e.getStackTrace()+" : "+e.getCause());
            }
            finally{
                //se il socket non è stato aperto termino direttamente perchè non ho niente da chiudere
                if (this.sock == null)System.exit(0);
                try {
                    
                    //attendo la terminazione del receiver se è in esecuzione
                    if(this.receiverThread.isAlive())this.receiverThread.join(0);
                    //chiudo il socket
                    this.sock.close();
                    //stampa di debug   
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }
}
