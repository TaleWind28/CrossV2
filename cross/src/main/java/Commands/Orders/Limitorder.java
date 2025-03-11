package Commands.Orders;

import java.time.Instant;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
import ServerTasks.GenericTask;
import Utils.AnsiColors;
import Utils.OrderCache;

public class Limitorder extends Order implements Values{

    public Limitorder(String user,String exchangeType,int size, int price){
        super(user,size,price,exchangeType);
        this.setColor(AnsiColors.BLUE_LIGHT);
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //controllo che l'utente sia loggato
        if(task.onlineUser.equals(""))return new OrderResponseMessage(-1,"Per effettuare ordini bisogna creare un account o accedervi");
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
        String reverseType = this.findOppositeMap(this.getExchangeType());
        //ciclo finchè non esauirisco tutte le entry dell'orederbook oppure evado completamente l'ordine
        while(result!=null && this.getSize()!=0){
            result = this.evadeOrder(reverseType, user, orderbook, cache, result);
        }
        //notifico i client dei trade avvenuti
        this.notifySuccessfullTrades(cache, task.UDPsender, this.getOrderId(), user);
        //se ho evaso tutto l'ordine lo comunico, altrimenti inserisco una nuova entry nell'orderbook
        if (this.getSize() == 0)return new OrderResponseMessage(this.getOrderId(),"Order Fully Executed successfully!");
        else orderbook.addData(this, this.getExchangeType());
        //restituisco l'esito al client
        return new OrderResponseMessage(this.getOrderId(),"Order Partially Executed, the remnants are placed as a limitorder in the orderbook");
    }

    @Override
    public String toString() {
        return "Limitorder{" +
        "exchangeType='"+this.getExchangeType()
        +"', size='"+this.getSize()+
        "', price='"+this.getPrice()+
        "', orderID='"+this.getOrderId()+
        "', utente='"+this.getUser()+
        "', timestamp='"+this.getGmt()+
        "'}";    
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
