package Commands.Orders;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;

public class Limitorder extends Order implements Values{
    private String exchangeType;
    private int size;
    private int price;
    private int orderID;
    private String user;

    public Limitorder(String exchangeType,int size, int price){
        this.exchangeType = exchangeType;
        this.size = size;
        this.price = price;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user){
        if(user.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        Orderbook orderbook = (Orderbook)data;
        //la faccio semplice per vedere se funziona
        //non so come funziona l'algoritmo richiesto dalla ricci quindi lo lascio così
        orderbook.addData(this, this.exchangeType);
        //System.out.println("fatto");
        return new ServerMessage("Ordine Correttamente Evaso",100);
    }

    @Override
    public String toString() {
        return "Limitorder{ exchangeType="+this.exchangeType+" size="+this.size+" price="+this.price+" orderID="+this.orderID+"}";    
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    @Override
    public String getExchangeType() {
        return this.exchangeType;
    }
    @Override
    public int getOrderID() {
        return this.orderID;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public int getPrice() {
        return this.price;    
    }

    @Override
    public int getSize() {
        return this.size;    
    }

    public void addSize(int size) {
        this.size+=size;    
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
