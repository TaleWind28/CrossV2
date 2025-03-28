package Server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


import Commands.Orders.Limitorder;
import Communication.Protocols.ServerProtocol;
import Communication.Protocols.TCP;
import Communication.Protocols.UDP;
import Config.ServerConfig;
import JsonAccessedData.Orderbook.Orderbook;
import JsonAccessedData.PriceHistory.TradeHistory;
import JsonAccessedData.Users.Userbook;
import Server.ServerTasks.*;
import Utils.AnsiColors;
import Utils.OrderSorting;

public class ServerClass extends ServerProtocol{
    private volatile Userbook registeredUsers;
    private volatile Orderbook orderbook;
    private volatile TradeHistory storico;
    private volatile int progressiveOrderNumber;
    private ServerSocket server;
    private String bindAddress;
    private UDP UDPListner;
    private Thread stopOrderListner;

    public ServerClass(ServerConfig config){
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

    public void dial(){
            try {
                this.server = new ServerSocket();
                this.server.bind(new InetSocketAddress(this.bindAddress,this.PORT));
                System.out.println(AnsiColors.ORANGE+"[ServerMain] server in attesa di connessioni...");
                while(true){
                    //creo il socket per comunicare col client
                    Socket client_Socket = server.accept();
                    //creo la task per gestire il client
                    GenericTask task = new GenericTask(client_Socket,this,new TCP());
                    //aggiungo il client alla lista di client attivi
                    addClient(client_Socket);
                    //sottometto il task al pool
                    this.pool.execute(task);
                }
            }
            catch(SocketException e){
                System.out.println(AnsiColors.BRIGHT_RED+"[InternalComms] Socket lato server in chiusura..."+AnsiColors.RESET);
                System.exit(0);
            }
            catch (Exception e) {
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
        try {
            this.stopOrderListner = new Thread(new StopOrderCheckerTask(this.orderbook, new GenericTask(this)));
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
    
    public ServerSocket getServer() {
        return server;
    }
}
