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
        System.out.println("[StopOrderChecker] sono vivo");
        while(true){
            //System.out.println("[StopOrderChecker]"+ordb.getStopOrders().size());
            //controllo se sono avvenute delle variazioni nei marketprice
            if(ordb.getAskMarketPrice()>oldAskMarketPrice && ordb.getBidMarketPrice()< oldBidMarketPrice)continue;
            //se sono avvenute aggiorno i marketprice
            oldAskMarketPrice = ordb.getAskMarketPrice();
            oldBidMarketPrice = ordb.getBidMarketPrice();
            //cerco nella lista 
            Iterator<StopOrder> navi = this.ordb.getStopOrders().iterator();
            while(navi.hasNext()){
                StopOrder order = navi.next();
                if((order.getExchangeType().equals("ask") && order.getPrice()<=oldAskMarketPrice) || (order.getExchangeType().equals("bid") && order.getPrice() >= oldBidMarketPrice)){
                    ordb.getStopOrders().remove(order);
                    this.stubTask.onlineUser = order.getUser(); 
                    order.execute(ordb, "stopprice met", this.stubTask);
                } 
            }
            
        }
        

    }
}
