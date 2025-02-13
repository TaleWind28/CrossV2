package Commands.Orders;

import java.util.ArrayList;
import java.util.List;

import Communication.Values;
import Communication.Messages.ServerMessage;
import Communication.Messages.UDPMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Orderbook;
import ServerTasks.GenericTask;
import Utils.OrderCache;
import Utils.OrderSorting;

public class MarketOrder extends Order implements Values {
    private String exchangeType;
    private int size;
    private int orderID;
    //private String user;

    public MarketOrder(String exchangeType,int size){
        this.exchangeType = exchangeType;
        this.size = size;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        super.setOrderId(task.getProgressiveOrderNumber());
        //recupero l'orderbook
        Orderbook orderbook = (Orderbook)data;
        //preparo string per richieder ela mappa
        String exchangetype = null;
        //preparo le stringhe per richiesta mappa Orderbook
        switch (this.exchangeType) {
            case "ask":
                exchangetype = "bid";
                break;
            case "bid":
                exchangetype = "ask";
                break;
        }
        //ha senso preparare il responsemessege adesse perchè se compro compro tutto dal solito utente, il quale verrà aggiunto successivamente al messaggio
        String responseMessage = "";
        OrderCache cache = new OrderCache();

        int resp_code = 200;
        while(this.size>0){
            responseMessage = evadeOrder(exchangetype, user, orderbook, cache, responseMessage);
            if (responseMessage.contains("[104]")){
                this.restoreOrders(cache,orderbook);
                this.setOrderId(-1);
                responseMessage = "[104] Non sono stati trovati ordini per le tue esigenze";
                resp_code = 104;
                break;
            }
        }
        //memorizzo le transazioni per poi mandarle
        List<String> tradeNotify = new ArrayList<>();
        //mando messaggio UDP
        while(cache.getSize()!=0){
            //rimuovo l'ordine dalla cache
            Limitorder order = cache.removeOrder();
            tradeNotify.add(order.toString()+"\n");
            String[] trades= {order.toString()};
            task.UDPsender.sendMessage(new UDPMessage("Prova Multicast",order.getUsername(),"closedTrades",trades));
        }

        if(tradeNotify.size()!=0){
            //invio il messaggio
            task.UDPsender.sendMessage(new UDPMessage("Prova Multicast",task.onlineUser,"closedTrades",tradeNotify.toArray(new String[0])));
        }
        
        responseMessage = ""+this.orderID;
        //responseMessage = responseMessage.stripTrailing();
        return new ServerMessage(responseMessage,resp_code);
    }

    public String evadeOrder(String exchangetype,String user,Orderbook orderbook, OrderCache cache,String responseMessage){
        System.out.println("[MarketOrder] entro in evaded con size= "+this.size);
        //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
        OrderSorting orderbookEntry = orderbook.getBestPriceAvailable(exchangetype,user);
        //controllo che esista una entry per il mio ordine
        if(orderbookEntry == null){System.out.println("[Marketorder]mamma");return "[104] Non sono stati trovati ordini per le tue esigenze";}
        //rimuovo l'ordine dall'orderbook
        Limitorder evadedOrder = (Limitorder)orderbook.removeData(exchangetype,orderbookEntry);
        //salvo l'ordine rimosso dall'ordebook in caso non si possa evadere completamente il marketorder
        cache.addOrder(evadedOrder);
        //controllo che l'ordine sia stato evaso
        if(evadedOrder == null){
            System.out.println("[Marketorder]ordine inevdibile");
            return "[104] Non sono stati trovati ordini per le tue esigenze";
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
        //System.out.println("[Marketorder]evadedOrder size="+evadedOrder.getSize());
        this.size -= evadedOrder.getSize();
        //System.out.println("[Marketorder]myorder size="+this.size);
        return responseMessage;
    }

    public void restoreOrders(OrderCache cache, Orderbook orderbook){
        while(cache.getSize()!=0){
            Limitorder ord = cache.removeOrder();
            orderbook.addData(ord, ord.getExchangeType());
        }
        
    }

    @Override
    public String toString() {
        return "Marketorder{ exchangeType="+this.exchangeType+" size="+this.size+" orderID="+this.orderID+"}";    
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    @Override
    public String getExchangeType() {
        return this.exchangeType;    
    }

    @Override
    public int getPrice() {
        return 0;
    }

    @Override
    public int getSize() {
        return this.size;
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
