package ServerTasks;

import java.util.Iterator;

import Commands.Orders.StopOrder;
import JsonAccessedData.Orderbook.Orderbook;

public class StopOrderCheckerTask implements Runnable{
    private Orderbook ordb;
    private GenericTask stubTask;
    public StopOrderCheckerTask(Orderbook orderbook, GenericTask task){
        this.ordb = orderbook;
        this.stubTask = task;
    }

    public void run(){
        int oldAskMarketPrice = ordb.getAskMarketPrice();
        int oldBidMarketPrice = ordb.getBidMarketPrice();
        String oldAskMarketPriceOwner = ordb.getMarketPriceOwner("ask");
        String oldBidMarketPriceOwner = ordb.getMarketPriceOwner("bid");
        
        System.out.println("[StopOrderChecker] sono vivo");
        while(true){//potrei implementare un controllo per attivare il thread solo quando necessario
            //controllo se sono avvenute delle variazioni nei marketprice
            if(ordb.getAskMarketPrice()>oldAskMarketPrice && ordb.getBidMarketPrice()< oldBidMarketPrice)continue;
            //se sono avvenute aggiorno i marketprice ed i rispettivi owner
            oldAskMarketPrice = ordb.getAskMarketPrice();
            oldAskMarketPriceOwner = ordb.getMarketPriceOwner("ask");
            oldBidMarketPrice = ordb.getBidMarketPrice();
            oldBidMarketPriceOwner = ordb.getMarketPriceOwner("bid");
            //cerco nella lista 
            Iterator<StopOrder> navi = this.ordb.getStopOrders().iterator();
            while(navi.hasNext()){
                StopOrder order = navi.next();
                //controllo se l'ordine che sto analizzando deve essere eseguito
                if( (order.getExchangeType().equals("ask") && order.getPrice()<=oldAskMarketPrice && !order.getUser().equals(oldAskMarketPriceOwner)) ||
                    (order.getExchangeType().equals("bid") && order.getPrice() >= oldBidMarketPrice)&& !order.getUser().equals(oldBidMarketPriceOwner)
                    ){
                    ordb.getStopOrders().remove(order);
                    this.stubTask.onlineUser = order.getUser(); 
                    order.execute(ordb, "stopprice met", this.stubTask);
                } 
            }
            
        }
        

    }
}
