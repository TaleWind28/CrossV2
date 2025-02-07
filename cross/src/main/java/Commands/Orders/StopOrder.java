package Commands.Orders;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;

public class StopOrder implements Values{
    private String exchangeType;
    private int size;
    private int price;

    public StopOrder(String exchangeType,int size, int price){
        this.exchangeType = exchangeType;
        this.size = size;
        this.price = price;
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

}
