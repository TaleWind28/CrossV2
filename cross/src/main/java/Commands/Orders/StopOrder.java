package Commands.Orders;

import java.time.Instant;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Orderbook;
//import JsonMemories.Orderbook;
import ServerTasks.GenericTask;

public class StopOrder extends Order implements Values {
    private String exchangeType;
    //private int size;
    //private int price;
    //private String user;

    public StopOrder(String exchangeType,int size, int price){
        this.exchangeType = exchangeType;
        super.setSize(size);
        super.setPrice(price);
        
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new OrderResponseMessage(-1,"User not logged in");
        //aggiorno orderId
        super.setOrderId(task.getProgressiveOrderNumber());
        this.setGmt(Instant.now().getEpochSecond());
        Orderbook orderbook = (Orderbook)data;
        if(user.equals("stopprice met"))return new MarketOrder(user, getSize()).execute(data, user, task);
        this.setUser(user);
        orderbook.getStopOrders().add(this);
        return new OrderResponseMessage(this.getOrderId(),"StopORder successfully placed");
    }

    @Override
    public String getExchangeType() {
        return this.exchangeType;    
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
