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
import Utils.OrderCache;

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
            System.out.println("parti");
            OrderResponseMessage executionStatus = (OrderResponseMessage) new MarketOrder(this.getUser(),this.getExchangeType(), this.getSize()).execute(data, task.onlineUser, task);
            // System.out.println("Risposta: "+executionStatus.response);
            // if(executionStatus.response == -1){
            //     System.out.print("Mi pianto?");
            //     task.UDPsender.sendMessage(new UDPMessage(task.onlineUser,"stoporder couldn't be executed",new String[]{}));
            //     System.out.print("No\n Ora?"); 
            //     this.notifySuccessfullTrades(new OrderCache(), task.UDPsender, this.getOrderId(), task.onlineUser);
            //     System.out.print("No");
            // }
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
