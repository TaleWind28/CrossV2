package Commands.Orders;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
import ServerTasks.GenericTask;
import Utils.OrderSorting;

public class CancelOrder  extends Order implements Values{

    public CancelOrder(int orderId, String user){
        super(user,0,0,null);
        this.setOrderId(orderId);
        //this.setUsername(user);
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String utente,GenericTask task){
        //potrebbe essere una funzione
        if(this.getUser().equals(""))return new ServerMessage("Devi effettuare l'accesso per piazzare ordini", 104);
        Orderbook orderbook = (Orderbook)data;

        if(searchMap(orderbook, "ask", this.getOrderId(), this.getUser()))return new ServerMessage("[100] Ordine correttamente Cancellato",100);
        else if(searchMap(orderbook, "bid", this.getOrderId(), this.getUser()))return new ServerMessage("[100] Ordine correttamente Cancellato",100);
        else if(searchStopOrder(orderbook, this.getOrderId(), this.getUser()))return new ServerMessage("Ordine correttamente Cancellato",100);
        else return new ServerMessage("[104] Non è stato possibile cancellare l'ordine richiesto",104);
    }

    public boolean searchMap(Orderbook orderbook,String requestedMap,int Id, String user){
        ConcurrentSkipListMap<OrderSorting, Limitorder> map = orderbook.getRequestedMap(requestedMap);
        for(Map.Entry<OrderSorting,Limitorder> entry :map.entrySet()){
            if(!(entry.getValue().getOrderId()== Id))continue;
            if(!entry.getValue().getUser().equals(user))return false;//controllare eccezione
            orderbook.removeData(requestedMap, entry.getKey());
            return true;
        }
        return false;
    }

    public boolean searchStopOrder(Orderbook orderbook,int id, String user){
        Iterator<StopOrder> navi = orderbook.getStopOrders().iterator();
        while(navi.hasNext()){
            //ciclo sugli stoporder
            StopOrder ord = navi.next();
            //controllo che utente e orderId siano corretti
            if(ord.getOrderId() != id || !ord.getUser().equals(user))continue;
            //solo in quel caso lo rimuovo
            orderbook.getStopOrders().remove(ord);
            return true;
        }
        return false;
    }

    @Override
    public String getExchangeType() {
        return null;
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
