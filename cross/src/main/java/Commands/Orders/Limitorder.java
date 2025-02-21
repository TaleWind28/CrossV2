package Commands.Orders;

import java.time.Instant;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Orderbook.Orderbook;
import ServerTasks.GenericTask;
import Utils.OrderCache;

public class Limitorder extends Order implements Values{
    private String exchangeType;

    public Limitorder(String exchangeType,int size, int price){
        super();
        this.exchangeType = exchangeType;
        super.setSize(size);
        super.setPrice(price);
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia loggato
        if(user.equals(""))return new OrderResponseMessage(-1,"Per effettuare ordini bisogna creare un account o accedervi");
        //imposto l'orderId
        this.setOrderId(task.getProgressiveOrderNumber());
        //memorizzo l'orario in cui è avvennuto l'ordine
        this.setGmt(Instant.now().getEpochSecond());
        //aumento l'orderId
        task.increaseProgressiveOrderNumber();
        //recupero l'orderbook
        Orderbook orderbook = (Orderbook)data;
        //Inizializzo cache e result
        OrderCache cache = new OrderCache();String result = new String();
        //ricavo la mappa opposta a quella richiesta per cercare offerte compatibili
        String reverseType = this.findOppositeMap(this.exchangeType);
        //ciclo finchè non esauirisco tutte le entry dell'orederbook oppure evado completamente l'ordine
        while(result!=null && this.getSize()!=0){
            result = this.evadeOrder(reverseType, user, orderbook, cache, result);
        }
        //stampa di debug
        System.out.println("[Limitorder] dopo evade"+this.getSize());
        //notifico i client dei trade avvenuti
        this.notifySuccessfullTrades(cache, task.UDPsender, this.getOrderId(), user);
        //se ho evaso tutto l'ordine lo comunico, altrimenti inserisco una nuova entry nell'orderbook
        if (this.getSize() == 0)return new OrderResponseMessage(this.getOrderId(),"Order Fully Executed successfully!");
        else orderbook.addData(this, this.exchangeType);
        //restituisco l'esito al client
        return new OrderResponseMessage(this.getOrderId(),"Order Partially Executed, the remnants are placed as a limitorder in the orderbook");
    }

    @Override
    public String toString() {
        return "Limitorder{" +
        "\nexchangeType="+this.exchangeType
        +"\n size="+this.getSize()+
        "\n price="+this.getPrice()+
        "\n orderID="+super.getOrderId()+
        "\n Utente="+super.getUser()+
        "\n timestamp="+super.getGmt()+
        "\n}";    
    }

    @Override
    public String getExchangeType() {
        return this.exchangeType;
    }

    public void addSize(int size) {
        int sub = super.getSize()+size;
        super.setSize(sub);    
    }

    @Override
    public String getUsername() {
        return super.getUser();    
    }

    @Override
    public void setUsername(String user) {
        super.setUser(user);    
    }

}
