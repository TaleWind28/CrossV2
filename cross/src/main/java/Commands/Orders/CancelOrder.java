package Commands.Orders;

import java.util.Map;
import java.util.TreeMap;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;

public class CancelOrder  extends Order implements Values{
    private int orderID;
    private String user;

    public CancelOrder(int orderID, String user){
        this.orderID = orderID;
        this.user = user;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String user){
        //Order order = (Order)cmd;
        this.setUsername(user);
        if(this.user.equals(""))return new ServerMessage("Devi effettuare l'accesso per piazzare ordini", 104);
        Orderbook orderbook = (Orderbook)data;//order.getOrderbook();
        
        if(searchMap(orderbook, "ask", this.orderID, this.user))return new ServerMessage("[100] Ordine correttamente Cancellato",100);
        
        else if(searchMap(orderbook, "bid", this.orderID, this.user))return new ServerMessage("[100] Ordine correttamente Cancellato",100);
        
        else return new ServerMessage("[104] Non è stato possibile cancellare l'ordine richiesto",104);
    }

    public boolean searchMap(Orderbook orderbook,String requestedMap,int Id, String user){
        TreeMap<String,Limitorder> map = orderbook.getRequestedMap(requestedMap);
        for(Map.Entry<String,Limitorder> entry :map.entrySet()){
            if(!(entry.getValue().getOrderID()== Id))continue;
            if(!entry.getValue().getUser().equals(user))return false;//controllare eccezione
            orderbook.removeData("ask",entry.getKey());
            return true;
        }
        return false;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return this.user;
    }
    @Override
    public String getExchangeType() {
        return null;
    }
    @Override
    public int getPrice() {
        return 0;
    }
    @Override
    public int getSize() {
        return 0;
    }
    public int getOrderID() {
        return orderID;
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
