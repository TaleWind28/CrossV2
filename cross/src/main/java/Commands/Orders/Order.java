package Commands.Orders;

import java.util.ArrayList;
import java.util.List;

import Communication.Messages.UDPMessage;
import Communication.Protocols.UDP;
import JsonAccessedData.Orderbook.Orderbook;
import Utils.OrderCache;
import Utils.OrderSorting;


public abstract class Order {
    private String user;
    private int orderId;
    private long gmt;
    private int size;
    private int price = 0;
    
    public String getUser(){
        return this.user;
    }

    public void setSize(int size) {
        this.size = size;
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

    public void setPrice(int price) {
        this.price = price;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setGmt(long gmt) {
        this.gmt = gmt;
    }

    
    public long getGmt() {
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
            udp.sendMessage(new UDPMessage(order.getUser(),"closedTrades",trades));
            responseMessage = ""+orderId;
        }
        if(tradeNotify.size()!=0)udp.sendMessage(new UDPMessage(user,"closedTrades",tradeNotify.toArray(new String[0])));
        else responseMessage = "Order not fully executed";
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
        //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
        OrderSorting orderbookEntry = orderbook.getBestPriceAvailable(exchangetype,user,this.getPrice());
        //controllo che esista una entry per il mio ordine
        if(orderbookEntry == null)return null;
        //controllare utilità responsemessage
        responseMessage = ""+this.getOrderId();
        //rimuovo l'ordine dall'orderbook
        Limitorder evadedOrder = (Limitorder)orderbook.removeData(exchangetype,orderbookEntry);
        //controllo che l'ordine sia stato evaso
        if(evadedOrder == null){
            System.out.println("[Order-evadeOrd]ordine inevdibile");
            return null;
        }
        //salvo l'ordine rimosso dall'ordebook in caso non si possa evadere completamente il marketorder
        cache.addOrder(evadedOrder);
        //controllo quanti btc sono stati comprati
        if(evadedOrder.getSize()>this.size){
            //sottraggo la taglia di bitcoin comprata
            evadedOrder.addSize(-(this.size));
            //rimetto l'offerta sul mercato
            orderbook.addData(evadedOrder, exchangetype);
            this.size = 0;
        }
        else{
            this.size -= evadedOrder.getSize();
        }
        return responseMessage;
    }
    public abstract String getExchangeType();
    
    public int getPrice(){
        return this.price;
    };
    
    @Override
    public String toString() {
        return "user='"+this.user+"', orderId='"+this.orderId+"', timestamp='"+this.gmt+"', size='"+this.size+"', price='"+this.price+"', tradeType='"+this.getExchangeType()+"'";
    }
    
}
