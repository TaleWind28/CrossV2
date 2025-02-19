package ServerTasks;

import java.util.Iterator;

import Commands.Orders.StopOrder;
import JsonUtils.Orderbook;

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
        while(true){
            if(ordb.getAskMarketPrice()==oldAskMarketPrice || ordb.getBidMarketPrice()!= oldBidMarketPrice)continue;
            //aggiorno i marketprice
            oldAskMarketPrice = ordb.getAskMarketPrice();
            oldBidMarketPrice = ordb.getBidMarketPrice();
            Iterator<StopOrder> navi = this.ordb.getStopOrders().iterator();
            int i = 0;
            while(navi.hasNext()){
                i++;
                StopOrder order = navi.next();
                if((order.getExchangeType().equals("ask") && order.getPrice()<=oldAskMarketPrice) || (order.getExchangeType().equals("bid") && order.getPrice() >= oldBidMarketPrice)){
                    order.execute(ordb, "stopprice met", this.stubTask);
                } 
            } 
            if(i!=0){
                System.out.println("[StopORderCheckerTask] controllo periodico completato");
            }
            
        }
        

    }
}
