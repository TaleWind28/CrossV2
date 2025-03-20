package Commands.Orders;

import java.util.Iterator;

import Commands.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
import Server.ServerTasks.GenericTask;
import Utils.AnsiColors;

public class ShowStopOrder extends Order implements Values{

    public ShowStopOrder() {
        super();
        this.setColor(AnsiColors.BLUE_MEDIUM);
    }
    
        @Override
    public String getExchangeType() {
        return null;    
    }

    @Override
    public ServerMessage execute(JsonAccessedData data, String user, GenericTask genericTask) throws ClassCastException{
        String prettyStopOrders = new String();
        Orderbook orderbook = (Orderbook) data;
        if(genericTask.onlineUser.equals("")) return new ServerMessage("Devi essere loggato per poter visualizzare gli stoporder",101);
        //creo un'iteratore per scorrere la lista
        Iterator<StopOrder> navi = orderbook.getStopOrders().iterator();
        //scorro la lista di stoporder per trovare gli ordini piazzati dall'utente
        while (navi.hasNext()) {
            StopOrder currentOrder = navi.next();
            if (!currentOrder.getUser().equals(genericTask.onlineUser))continue;
            prettyStopOrders+= currentOrder.toString()+"\n";
        }
        //se l'utente non ha piazzato alcuno stoporder allora glielo comunico
        if(prettyStopOrders.equals(""))prettyStopOrders = "No StopOrders Placed";
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
