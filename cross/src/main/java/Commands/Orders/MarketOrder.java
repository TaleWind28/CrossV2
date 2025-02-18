package Commands.Orders;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Orderbook;
import ServerTasks.GenericTask;
import Utils.OrderCache;
import Utils.OrderSorting;

public class MarketOrder extends Order implements Values {
    private String exchangeType;
    private int size;
    //private String user;

    public MarketOrder(String exchangeType,int size){
        this.exchangeType = exchangeType;
        this.size = size;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new OrderResponseMessage(-1,"User not logged in");
        super.setOrderId(task.getProgressiveOrderNumber());
        //recupero l'orderbook
        Orderbook orderbook = (Orderbook)data;
        //preparo le stringhe per richiesta mappa Orderbook
        String exchangetype = super.findOppositeMap(this.getExchangeType());
        //ha senso preparare il responsemessege adesse perchè se compro compro tutto dal solito utente, il quale verrà aggiunto successivamente al messaggio
        String responseMessage = "";
        //creo una cache per memorizzare gli ordini
        OrderCache cache = new OrderCache();
        //predispongo un codice di risposta di default
        int resp_code = 200;
        //ciclo finchè non evado completamente l'ordine
        while(this.size>0){
            //invoco evadeORder per evadere l'ordine
            responseMessage = evadeOrder(exchangetype, user, orderbook, cache, responseMessage);
            System.out.println("[Marketorder-execute] response"+responseMessage+", size "+this.size);
            //controllo il risultato dell'evadeORder
            if (responseMessage == null){
                System.out.println("[Marketorder-evadeOrd.exception] response "+responseMessage);
                //ripristino l'orderbook
                orderbook.restoreOrders(cache,orderbook);
                //imposto orderId a -1 per indicare il fallimento
                super.setOrderId(-1);
                //System.out.println("[Marketorder]"+super.getOrderId());
                responseMessage = "Order not fully Executed!";
                resp_code = -1;
               
                break;
            }
            
        }
        super.notifySuccessfullTrades(cache, task.UDPsender, super.getOrderId(), this.getUser());
        return new OrderResponseMessage(resp_code,responseMessage);
    }

    public String evadeOrder(String exchangetype,String user,Orderbook orderbook, OrderCache cache,String responseMessage){
        System.out.println("[MarketOrder] entro in evaded con size= "+this.size);
        //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
        OrderSorting orderbookEntry = orderbook.getBestPriceAvailable(exchangetype,user);
        System.out.println("[Marketorder-evadeOrd] entry="+orderbookEntry);
        //controllo che esista una entry per il mio ordine
        if(orderbookEntry == null){System.out.println("[Marketorder]mamma");return null;}
        responseMessage = ""+this.getOrderId();
        
        //rimuovo l'ordine dall'orderbook
        Limitorder evadedOrder = (Limitorder)orderbook.removeData(exchangetype,orderbookEntry);
        //salvo l'ordine rimosso dall'ordebook in caso non si possa evadere completamente il marketorder
        cache.addOrder(evadedOrder);
        //controllo che l'ordine sia stato evaso
        if(evadedOrder == null){
            System.out.println("[Marketorder]ordine inevdibile");
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
        System.out.println("[Marketorder-evadeOrder] size"+this.size);
        return responseMessage;
    }

    @Override
    public String toString() {
        return "Marketorder{ exchangeType="+this.exchangeType+" size="+this.size+" orderId="+super.getOrderId()+"}";    
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
