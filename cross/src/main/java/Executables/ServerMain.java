package Executables;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import Communication.ServerProtocol;
import Communication.TCP;
import JsonMemories.Orderbook;
import JsonMemories.Userbook;
import ServerTasks.*;
import Users.Commands.Order;
import Users.Commands.Factory.FactoryRegistry;

public class ServerMain extends ServerProtocol{
    private Userbook registeredUsers;
    private Orderbook orderbook;
    public ServerMain(int port, int numThreads){
        super(port,numThreads);
        this.registeredUsers = new Userbook("cross\\src\\main\\java\\JsonFiles\\Users.json");
        this.orderbook = new Orderbook("cross\\src\\main\\java\\JsonFiles\\OrderBook.json");
    }

    public static void main(String[] args) throws Exception {
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
        int progressiveOrderNumber = findOrderID(orderbook);
        System.out.println("Numero Ordine: "+progressiveOrderNumber);
        FactoryRegistry.updateFactoryData(0, registeredUsers,"");
        FactoryRegistry.updateFactoryData(1, orderbook,""+progressiveOrderNumber);
        FactoryRegistry.getFactory(1);
        return;
    }

    public int findOrderID(Orderbook orderbook){
        int bestId = searchMap(orderbook, "ask", -1);
        bestId = searchMap(orderbook, "bid", bestId);
        return bestId;
    }

    public int searchMap(Orderbook orderbook,String requestedMap,int bestId){
        TreeMap<String,Order> map = orderbook.getRequestedMap(requestedMap);
        for(Map.Entry<String,Order> entry :map.entrySet()){
            if((entry.getValue().getorderID()< bestId))continue;
            bestId = entry.getValue().getorderID();
        }
        return bestId;
    }
}
