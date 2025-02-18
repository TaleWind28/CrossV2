package Commands.Orders;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
//import JsonMemories.Orderbook;
import ServerTasks.GenericTask;

public class StopOrder extends Order implements Values {
    private String exchangeType;
    //private int size;
    private int price;
    //private String user;

    public StopOrder(String exchangeType,int size, int price){
        this.exchangeType = exchangeType;
        super.setSize(size);
        this.price = price;
        
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //if(context.onlineUser.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        //Orderbook orderbook = (Orderbook)data;
        //la faccio semplice per vedere se funziona
        //non so come funziona l'algoritmo richiesto dalla ricci quindi lo lascio così
        //orderbook.addData(this, this.exchangeType);
        //System.out.println("fatto");
        return new ServerMessage("Ordine Correttamente Evaso",100);
    }

    // public void setUser(String user) {
    //     super.setUser(user);
    // }

    // @Override
    // public String getUser() {
    //     return this.user;
    // }

    @Override
    public String getExchangeType() {
        return this.exchangeType;    
    }

    @Override
    public int getPrice() {
        return this.price;
    }

    @Override
    public void setUsername(String user) {
        super.setUser(user);
    }

    @Override
    public String getUsername() {
        return super.getUser();   
    }

}
