package Commands.Orders;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import Communication.Messages.UDPMessage;
import Communication.Protocols.UDP;
import Utils.OrderCache;


public abstract class Order {
    private String user;
    private int orderId;
    private String gmt;
    
    public String getUser(){
        return this.user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public int getOrderId(){
        return this.orderId;
    }
    
    public void setOrderId(int orderId) {
        System.out.println("[Order]"+orderId);
        this.orderId = orderId;
        System.out.println("[Order]"+this.orderId);
    }

    public void setGmt(ZonedDateTime gmt) {
        this.gmt = gmt.format(DateTimeFormatter.ISO_DATE_TIME);;
    }

    public String getGmt() {
        return gmt;
    }

    public String notifySuccessfullTrades(OrderCache cache,UDP udp,int orderId,String user){
        //memorizzo le transazioni per poi mandarle
        List<String> tradeNotify = new ArrayList<>();
        String responseMessage = "";
        //mando messaggio UDP
        while(cache.getSize()!=0){
            //rimuovo l'ordine dalla cache
            Limitorder order = cache.removeOrder();
            tradeNotify.add(order.toString()+"\n");
            String[] trades= {order.toString()};
            udp.sendMessage(new UDPMessage("Prova Multicast",order.getUsername(),"closedTrades",trades));
            responseMessage = ""+orderId;
        }
        if(tradeNotify.size()!=0)udp.sendMessage(new UDPMessage("Prova Multicast",user,"closedTrades",tradeNotify.toArray(new String[0])));
        else responseMessage = "-1: Order not fully executed";
        return responseMessage;
    }
    
    public String findOppositeMap(String exchangeType){
        switch (exchangeType) {
            case "ask":
                exchangeType = "bid";
                break;
            case "bid":
                exchangeType = "ask";
                break;
        }
        return exchangeType;
    }
    public abstract String getExchangeType();
    public abstract int getPrice();
    public abstract int getSize();
    
    
}
