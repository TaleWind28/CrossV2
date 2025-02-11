package Executables;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import Commands.Orders.Limitorder;
import Commands.Orders.Order;
import Communication.ServerProtocol;
import Communication.TCP;
import JsonMemories.OrderClass;
import JsonMemories.Orderbook;
import JsonMemories.Userbook;
import ServerTasks.*;
import Utils.OrderSortTst;
import Utils.OrderSorting;
import Utils.OrderSortingAdapter;
import okio.Okio;

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

        ConcurrentSkipListMap<OrderSorting,Limitorder> ordList = new ConcurrentSkipListMap<>();
        Limitorder ord0 = new Limitorder("ask", 3, 0);
        Limitorder ord1 = new Limitorder("ask", 12, 0);
        Limitorder ord2 = new Limitorder("ask", 22, 5);
        Limitorder ord3 = new Limitorder("ask", 6, 0);
        Limitorder ord4 = new Limitorder("ask", 1, 4);
        //ord0.setOrderId(0);
        ord0.setOrderId(2);
        ord1.setOrderId(3);
        ord2.setOrderId(4);
        ord3.setOrderId(5);
        ord4.setOrderId(6);
        ordList.put(new OrderSorting(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), ord0.getPrice(),ord0.getOrderId()), ord0);
        ordList.put(new OrderSorting(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), ord1.getPrice(),ord1.getOrderId()), ord1);
        ordList.put(new OrderSorting(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), ord2.getPrice(),ord2.getOrderId()), ord2);
        ordList.put(new OrderSorting(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), ord3.getPrice(),ord3.getOrderId()), ord3);
        ordList.put(new OrderSorting(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), ord4.getPrice(),ord4.getOrderId()), ord4);
        OrderSortTst ordToPrint = new OrderSortTst(ordList);
        
        Moshi moshi = new Moshi.Builder().add(new OrderSortingAdapter()).add(PolymorphicJsonAdapterFactory.of(ZonedDateTime.class,"GMT")).add(PolymorphicJsonAdapterFactory.of(Order.class,"Order").withSubtype(Limitorder.class, "Limitorder")).build();
        //JsonAdapter<Map<OrderSorting,Limitorder>> adapter = moshi.adapter(Types.newParameterizedType(Map.class, OrderSorting.class, Limitorder.class));
        JsonAdapter<OrderSortTst> adapter = moshi.adapter(OrderSortTst.class);
        try (JsonWriter writer = JsonWriter.of(Okio.buffer(Okio.sink(new File("cross\\src\\main\\java\\Utils\\try.json"))))) {
            writer.setIndent(" ");
            adapter.toJson(writer, ordToPrint);
        } catch (Exception e) {
            System.out.println("[SERVERMAIN] Test"+e.getMessage()+"\n"+e.getCause()+"\n"+e.getClass()+"\n");
            System.exit(0);
        }

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
