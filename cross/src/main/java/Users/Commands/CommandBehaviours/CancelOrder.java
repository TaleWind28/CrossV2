package Users.Commands.CommandBehaviours;

import java.util.Map;
import java.util.TreeMap;

import Communication.Message;
import ServerTasks.GenericTask;
import Users.Commands.Order;
import Users.Commands.UserCommand;
import JsonMemories.Orderbook;;

public class CancelOrder implements CommandBehaviour {

    @Override
    public Message executeOrder(UserCommand cmd, GenericTask context) {
        Order order = (Order)cmd;
        Orderbook orderbook = order.getOrderbook();
        
        if(searchMap(orderbook, "ask", order.getorderID(), order.getUser()))return new Message("[200] Ordine correttamente Cancellato");
        
        else if(searchMap(orderbook, "bid", order.getorderID(), order.getUser()))return new Message("[200] Ordine correttamente Cancellato");
        
        else return new Message("[404] Non è stato possibile cancellare l'ordine richiesto",404);
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

    @Override
    public int getUnicode() {
        return 110;
    }
}
