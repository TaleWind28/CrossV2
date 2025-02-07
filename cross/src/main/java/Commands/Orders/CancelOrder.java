package Commands.Orders;

import java.util.Map;
import java.util.TreeMap;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import Users.Commands.Order;

public class CancelOrder implements Values{
    private int OrderID;
    private String user;

    public CancelOrder(int orderID, String user){
        this.OrderID = orderID;
        this.user = user;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data){
        //Order order = (Order)cmd;
        
        Orderbook orderbook = (Orderbook)data;//order.getOrderbook();
        
        if(searchMap(orderbook, "ask", this.OrderID, this.user))return new ServerMessage("[100] Ordine correttamente Cancellato",100);
        
        else if(searchMap(orderbook, "bid", this.OrderID, this.user))return new ServerMessage("[100] Ordine correttamente Cancellato",100);
        
        else return new ServerMessage("[104] Non è stato possibile cancellare l'ordine richiesto",104);
    }

    public boolean searchMap(Orderbook orderbook,String requestedMap,int Id, String user){
        TreeMap<String,Order> map = orderbook.getRequestedMap(requestedMap);
        for(Map.Entry<String,Order> entry :map.entrySet()){
            if(!(entry.getValue().getorderID()== Id))continue;
            if(!entry.getValue().getUser().equals(user))return false;//controllare eccezione
            orderbook.removeData("ask",entry.getKey());
            return true;
        }
        return false;
    }
}
