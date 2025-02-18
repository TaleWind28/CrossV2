package Commands.Orders;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import Communication.Messages.UDPMessage;
import Communication.Protocols.UDP;
import JsonUtils.Orderbook;
import Utils.OrderCache;
import Utils.OrderSorting;


public abstract class Order {
    private String user;
    private int orderId;
    private String gmt;
    private int size;
    
    public String getUser(){
        return this.user;
    }

    public void setSize(int size) {
        
        this.size = size;
        System.out.println("[Order] size= "+this.size);
    }

    public int getSize() {
        return this.size;
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
            udp.sendMessage(new UDPMessage(order.getUsername(),"closedTrades",trades));
            responseMessage = ""+orderId;
        }
        if(tradeNotify.size()!=0)udp.sendMessage(new UDPMessage(user,"closedTrades",tradeNotify.toArray(new String[0])));
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

    public String evadeOrder(String exchangetype,String user,Orderbook orderbook, OrderCache cache,String responseMessage){
        System.out.println("[Order-evadeOrd] entro in evaded con size= "+this.size+",exchange type"+exchangetype+",utente"+user);
        //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
        OrderSorting orderbookEntry = orderbook.getBestPriceAvailable(exchangetype,user);
        System.out.println("[Order-evadeOrd] entry="+orderbookEntry);
        //controllo che esista una entry per il mio ordine
        if(orderbookEntry == null){System.out.println("[Order]mamma");return null;}
        responseMessage = ""+this.getOrderId();
        
        //rimuovo l'ordine dall'orderbook
        Limitorder evadedOrder = (Limitorder)orderbook.removeData(exchangetype,orderbookEntry);
        //salvo l'ordine rimosso dall'ordebook in caso non si possa evadere completamente il marketorder
        cache.addOrder(evadedOrder);
        //controllo che l'ordine sia stato evaso
        if(evadedOrder == null){
            System.out.println("[Order-evadeOrd]ordine inevdibile");
            return null;
        }
        //controllo quanti btc sono stati comprati
        if(evadedOrder.getSize()>this.size){
            //bitcoinBought = this.size;
            //sottraggo la taglia di bitcoin comprata
            evadedOrder.addSize(-(this.size));
            //rimetto l'offerta sul mercato
            orderbook.addData(evadedOrder, exchangetype);
            this.size = 0;
        }
        this.size -= evadedOrder.getSize();
        System.out.println("[Order-evadeOrd] size"+this.size);
        return responseMessage;
    }
    public abstract String getExchangeType();
    public abstract int getPrice();
    
    
}
