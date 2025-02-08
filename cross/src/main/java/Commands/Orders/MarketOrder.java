package Commands.Orders;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;

public class MarketOrder implements Values,Order{
    private String exchangeType;
    private int size;
    private int orderID;
    private String user;

    public MarketOrder(String exchangeType,int size){
        this.exchangeType = exchangeType;
        this.size = size;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String user){
        if(user.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        Orderbook orderbook = (Orderbook)data;
        //la faccio semplice per vedere se funziona
        //non so come funziona l'algoritmo richiesto dalla ricci quindi lo lascio così
        //orderbook.addData(this, this.exchangeType);
        //System.out.println("fatto");
        return new ServerMessage("Ordine Correttamente Evaso",100);
    }

    @Override
    public String toString() {
        return "Marketorder{ exchangeType="+this.exchangeType+" size="+this.size+" orderID="+this.orderID+"}";    
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getExchangeType() {
        return this.exchangeType;    
    }

    @Override
    public int getPrice() {
        return 0;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getOrderID() {
        return this.orderID;
    }

    @Override
    public void setUsername(String user) {
        this.user = user;    
    }

    @Override
    public String getUsername() {
        return this.user;    
    }
}
