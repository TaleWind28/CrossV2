package Commands.Orders;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Orderbook;
import ServerTasks.GenericTask;
import Utils.OrderCache;

public class MarketOrder extends Order implements Values {
    private String exchangeType;

    public MarketOrder(String exchangeType,int size){
        super.setSize(size);
        this.exchangeType = exchangeType;
        
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new OrderResponseMessage(-1,"User not logged in");
        super.setOrderId(task.getProgressiveOrderNumber());
        //recupero l'orderbook
        Orderbook orderbook = (Orderbook)data;
        //preparo le stringhe per richiesta mappa Orderbook
        String exchangetype = super.findOppositeMap(this.exchangeType);
        //ha senso preparare il responsemessege adesse perchè se compro compro tutto dal solito utente, il quale verrà aggiunto successivamente al messaggio
        String responseMessage = "";
        //creo una cache per memorizzare gli ordini
        OrderCache cache = new OrderCache();
        //predispongo un codice di risposta di default
        int resp_code = this.getOrderId();
        //ciclo finchè non evado completamente l'ordine
        while(super.getSize()>0){
            //invoco evadeORder per evadere l'ordine
            responseMessage = super.evadeOrder(exchangetype, user, orderbook, cache, responseMessage);
            System.out.println("[Marketorder-execute] response"+responseMessage+", size "+super.getSize());
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
            responseMessage = "Order fully Executed";            
            
        }
        super.notifySuccessfullTrades(cache, task.UDPsender, this.getOrderId(), this.getUser());
        return new OrderResponseMessage(resp_code,responseMessage);
    }

    

    @Override
    public String toString() {
        return "Marketorder{ exchangeType="+this.exchangeType+" size="+getSize()+" orderId="+super.getOrderId()+"}";    
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
    public void setUsername(String user) {
        super.setUser(user);    
    }

    @Override
    public String getUsername() {
        return super.getUser();    
    }
}
