package Commands.Orders;

import java.time.Instant;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
//import JsonMemories.Orderbook;
import ServerTasks.GenericTask;
import Utils.AnsiColors;

public class StopOrder extends Order implements Values {

    public StopOrder(String user,String exchangeType,int size, int price){
        super(user,size,price,exchangeType);
        this.setColor(AnsiColors.BLUE_LIGHT);
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new OrderResponseMessage(-1,"User not logged in");
        //aggiorno orderId
        this.setOrderId(task.getProgressiveOrderNumber());
        this.setGmt(Instant.now().getEpochSecond());
        Orderbook orderbook = (Orderbook)data;
        if(user.equals("stopprice met"))return new MarketOrder(this.getUser(),this.getExchangeType(), this.getSize()).execute(data, task.onlineUser, task);
        this.setUser(task.onlineUser);
        orderbook.getStopOrders().add(this);
        return new OrderResponseMessage(this.getOrderId(),"StopOrder successfully placed");
    }

    @Override
    public void setUsername(String user) {
        super.setUser(user);
    }

    @Override
    public String getUsername() {
        return super.getUser();   
    }

    @Override
    public String toString() {
        return "StopOrder{"+super.toString()+"}";
    }
}
