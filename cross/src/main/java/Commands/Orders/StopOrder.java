package Commands.Orders;

import java.time.Instant;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import Communication.Messages.UDPMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
//import JsonMemories.Orderbook;
import ServerTasks.GenericTask;
import Utils.AnsiColors;

public class StopOrder extends Order implements Values {

    public StopOrder(String user,String exchangeType,int size, int price){
        super(user,size,price,exchangeType);
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new OrderResponseMessage(-1,"User not logged in");
        //aggiorno orderId
        this.setOrderId(task.getProgressiveOrderNumber());
        this.setGmt(Instant.now().getEpochSecond());
        Orderbook orderbook = (Orderbook)data;
        if(user.equals("stopprice met")){
            OrderResponseMessage executionStatus = (OrderResponseMessage) new MarketOrder(this.getUser(),this.getExchangeType(), this.getSize()).execute(data, task.onlineUser, task);
            if (executionStatus.getOrderId() == -1)task.UDPsender.sendMessage(new UDPMessage(task.onlineUser,"stoporder couldn't be executed",new String[]{"the stopprice for a previously placed stoporder was met but the order couldn't be evaded\nOrderDetails:"+this.toString()}));
            return null;
        }
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
        return AnsiColors.MAGENTA+"StopOrder{"+super.toString()+"}"+AnsiColors.RESET;
    }
}
