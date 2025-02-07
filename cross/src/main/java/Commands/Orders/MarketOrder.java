package Commands.Orders;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;

public class MarketOrder extends Values implements Order{
    private String exchangeType;
    private int size;
    private int orderID;

    public MarketOrder(String exchangeType,int size){
        this.exchangeType = exchangeType;
        this.size = size;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data){
        //if(context.onlineUser.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        Orderbook orderbook = (Orderbook)data;
        //la faccio semplice per vedere se funziona
        //non so come funziona l'algoritmo richiesto dalla ricci quindi lo lascio così
        orderbook.addData(this, this.exchangeType);
        //System.out.println("fatto");
        return new ServerMessage("Ordine Correttamente Evaso",100);
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    @Override
    public String getUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public String getExchangeType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExchangeType'");
    }

    @Override
    public int getPrice() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPrice'");
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSize'");
    }

    @Override
    public int getOrderID() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrderID'");
    }
}
