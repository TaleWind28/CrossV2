package Executables;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import Commands.Orders.Limitorder;
import Communication.Protocols.ServerProtocol;
import Communication.Protocols.TCP;
import Communication.Protocols.UDP;
import Config.ServerConfig;
import JsonAccessedData.Orderbook.Orderbook;
import JsonAccessedData.PriceHistory.TradeHistory;
import JsonAccessedData.Users.Userbook;
import ServerTasks.*;
import Utils.OrderSorting;

public class ServerMain extends ServerProtocol{
    private volatile Userbook registeredUsers;
    private volatile Orderbook orderbook;
    private volatile TradeHistory storico;
    private volatile int progressiveOrderNumber;
    private String bindAddress;
    private UDP UDPListner;
    private Thread stopOrderListner;

    public ServerMain(ServerConfig config){
        super(config.getTCPport(),Runtime.getRuntime().availableProcessors());
        this.bindAddress = config.getTCPaddress();
        this.registeredUsers = new Userbook(config.getUserbook());
        this.storico = new TradeHistory(config.getStorico());
        this.orderbook = new Orderbook(config.getOrderBook());
        try{
            this.UDPListner =   new UDP(config.getUDPaddress(),config.getUDPport(),null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            
        }
    }

    public static void main(String[] args) throws Exception {
        ServerConfig configuration = getServerConfig();
        //System.out.println(configuration.getOrderBook()+"\n"+configuration.getStorico());
        ServerMain server = new ServerMain(configuration);
        //System.out.println(server.UDPListner.toString());
        //Aggiungi uno shutdown hook alla JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new ClosingTask(server)));
        //TreeMap<Integer,TreeMap<DayTime,Trade>> tradeMap = new TradeHistory().monthlyTrades("cross\\src\\main\\java\\JsonUtils\\JsonFiles\\storicoOrdini.json", Month.OCTOBER);
        //ServerMain.printTradeMap(tradeMap.get(2024), 0);
        server.initialConfig();
        server.dial();
        return;
    }

    
    public static ServerConfig getServerConfig(){
        try{
            JsonAdapter<ServerConfig> jsonAdapter = new Moshi.Builder().build().adapter(ServerConfig.class);
            // Carica il file dalle risorse
            InputStream inputStream = ServerMain.class.getClassLoader()
                                     .getResourceAsStream("ServerConfig.json");
            
            if (inputStream == null) {
                throw new RuntimeException("File di configurazione non trovato nelle risorse");
            }
            
            // Leggi il contenuto del file
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            return jsonAdapter.fromJson(jsonContent.toString());
        }
        catch(Exception e){
            System.out.println("no");
        }
        return null;
    }
    
    public void dial(){
            try (ServerSocket server = new ServerSocket()) {
                //String bindAddress = "0.0.0.0"; // Ascolta su tutte le interfacce di rete
                server.bind(new InetSocketAddress(this.bindAddress,this.PORT));
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
        this.storico.loadData();
        
        progressiveOrderNumber = findOrderID(this.orderbook)+1;
        System.out.println("[ServerMain-initialConfig] Numero Ordine: "+progressiveOrderNumber);
        try {
            this.stopOrderListner = new Thread(new StopOrderCheckerTask(this.orderbook, new GenericTask(this)));
            System.out.println("[ServerMain-InitialConfig]");
            this.stopOrderListner.start();
        } catch (Exception e) {
            System.out.println("[ServerMain-InitialConfig]"+e.getMessage());
        }
        return;
    }

    public int findOrderID(Orderbook orderbook){
        int bestId = searchMap(orderbook, "ask", -1);
        bestId = searchMap(orderbook, "bid", bestId);
        return bestId;
    }

    public int searchMap(Orderbook orderbook,String requestedMap,int bestId){
        ConcurrentSkipListMap<OrderSorting, Limitorder> map = orderbook.getRequestedMap(requestedMap);
        for(Map.Entry<OrderSorting,Limitorder> entry :map.entrySet()){
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

    public UDP getUDPListner() {
        return UDPListner;
    }

    public TradeHistory getStorico() {
        return storico;
    }
}
