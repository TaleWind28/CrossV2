package Commands.Orders;

import java.util.Iterator;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
import ServerTasks.GenericTask;

public class ShowStopOrder extends Order implements Values{

    @Override
    public String getExchangeType() {
        return null;    
    }

    @Override
    public ServerMessage execute(JsonAccessedData data, String user, GenericTask genericTask) {
        String prettyStopOrders = new String();
        Orderbook orderbook = (Orderbook) data;
        Iterator<StopOrder> navi = orderbook.getStopOrders().iterator();
        while (navi.hasNext()) {
            StopOrder currentOrder = navi.next();
            prettyStopOrders+= currentOrder.toString()+"\n";
        }
        return new ServerMessage(prettyStopOrders,100);
    }

    @Override
    public void setUsername(String user) {
       this.setUser(user);
    }

    @Override
    public String getUsername() {
        return this.getUser();
    }

}
