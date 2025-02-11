package Commands.Orders;

import java.time.ZonedDateTime;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import ServerTasks.GenericTask;

public class Limitorder extends Order implements Values{
    private String exchangeType;
    private int size;
    private int price;

    public Limitorder(String exchangeType,int size, int price){
        super();
        this.exchangeType = exchangeType;
        this.size = size;
        this.price = price;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        if(user.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        super.setOrderId(task.getProgressiveOrderNumber());
        task.increaseProgressiveOrderNumber();
        Orderbook orderbook = (Orderbook)data;
        //la faccio semplice per vedere se funziona
        super.setGmt(ZonedDateTime.now());
        //non so come funziona l'algoritmo richiesto dalla ricci quindi lo lascio così
        orderbook.addData(this, this.exchangeType);
        //System.out.println("fatto");
        return new ServerMessage("Ordine Correttamente Evaso",100);
    }

    @Override
    public String toString() {
        return "Limitorder{ exchangeType="+this.exchangeType+" size="+this.size+" price="+this.price+" orderID="+super.getOrderId()+"}";    
    }

    @Override
    public String getExchangeType() {
        return this.exchangeType;
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
    public String getUsername() {
        return super.getUser();    
    }

    @Override
    public void setUsername(String user) {
        super.setUser(user);    
    }

}
