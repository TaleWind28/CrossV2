package Executables;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


import Commands.Orders.Limitorder;
import Communication.ServerProtocol;
import Communication.TCP;
import JsonMemories.Orderbook;
import JsonMemories.Userbook;
import ServerTasks.*;

public class ServerMain extends ServerProtocol{
    private volatile Userbook registeredUsers;
    private volatile Orderbook orderbook;
    private volatile int progressiveOrderNumber;

    public ServerMain(int port, int numThreads){
        super(port,numThreads);
        this.registeredUsers = new Userbook("cross\\src\\main\\java\\JsonFiles\\Users.json");
        this.orderbook = new Orderbook("cross\\src\\main\\java\\JsonFiles\\OrderBook.json");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("mino");
        ServerMain server = new ServerMain(20000,16);
        
        // Aggiungi uno shutdown hook alla JVM
        Runtime.getRuntime().addShutdownHook(
            new Thread(
                () -> {
                    System.out.println("Ctrl+C rilevato -> chiusura server in corso...");
                    //Arresta il thread pool
                    server.pool.shutdown();
                    try {
                        //Attende la terminazione dei thread attivi
                        if (!server.pool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                            System.out.println("[Server] Interruzione forzata dei thread attivi...");
                            server.pool.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        //Forza l'arresto in caso di interruzione
                        server.pool.shutdownNow();
                    }
                    System.out.println("[Server] Threadpool terminato");
                    server.registeredUsers.getUserMap().forEach((username, user)-> user.setLogged(false));
                    server.registeredUsers.dataFlush();
                    System.out.println("[Server] Utenti correttamente sloggati");
                }
            )
        );
        server.initialConfig();
        server.dial();
        return;
    }
    
    public void dial(){
            try (ServerSocket server = new ServerSocket()) {
                String bindAddress = "0.0.0.0"; // Ascolta su tutte le interfacce di rete
                server.bind(new InetSocketAddress(bindAddress,this.PORT));
                while(true){
                    Socket client_Socket = server.accept();
                    //realizzare con factory per miglior versatilità -> inutile in quanto ho solo una task
                    //TCP protocolToUse = new TCP();
                    //protocolToUse.setGson(this.gson);
                    GenericTask task = new GenericTask(client_Socket,this,new TCP());
                    addClient(client_Socket);
                    this.pool.execute(task);
                }
            } catch (Exception e) {
                System.out.println(e.getClass()+": "+e.getMessage());
                System.exit(0);
            }
    }
    public Userbook getRegisteredUsers() {
        return registeredUsers;
    }

    public Orderbook getOrderbook() {
        return orderbook;
    }

    public void initialConfig(){
        //carico in memoria
        this.registeredUsers.loadData();
        this.orderbook.loadData();
        progressiveOrderNumber = findOrderID(orderbook)+1;
        System.out.println("Numero Ordine: "+progressiveOrderNumber);
        return;
    }

    public int findOrderID(Orderbook orderbook){
        int bestId = searchMap(orderbook, "ask", -1);
        bestId = searchMap(orderbook, "bid", bestId);
        return bestId;
    }

    public int searchMap(Orderbook orderbook,String requestedMap,int bestId){
        TreeMap<String,Limitorder> map = orderbook.getRequestedMap(requestedMap);
        for(Map.Entry<String,Limitorder> entry :map.entrySet()){
            if((entry.getValue().getOrderId()< bestId))continue;
            bestId = entry.getValue().getOrderId();
        }
        return bestId;
    }

    public synchronized int getProgressiveOrderNumber() {
        return progressiveOrderNumber;
    }

    public synchronized void increaseProgressiveOrderNumber(){
        this.progressiveOrderNumber++;
        return;
    }
}
