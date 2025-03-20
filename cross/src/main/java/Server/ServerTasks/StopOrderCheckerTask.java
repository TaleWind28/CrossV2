package Server.ServerTasks;

import java.util.Iterator;

import Commands.Orders.Order;
import Commands.Orders.StopOrder;
import JsonAccessedData.Orderbook.Orderbook;

public class StopOrderCheckerTask implements Runnable{
    private Orderbook ordb;
    private GenericTask stubTask;
    int askMarketPrice;
    int bidMarketPrice;
    String askMarketPriceOwner;
    String bidMarketPriceOwner;
    
    public StopOrderCheckerTask(Orderbook orderbook, GenericTask task){
        this.ordb = orderbook;
        this.stubTask = task;
        this.askMarketPrice = ordb.getAskMarketPrice();
        this.bidMarketPrice = ordb.getBidMarketPrice();
        this.askMarketPriceOwner = ordb.getMarketPriceOwner("ask");
        this.bidMarketPriceOwner = ordb.getMarketPriceOwner("bid");
    }

    public void run(){
        while(true){
            //controllo se sono avvenute delle variazioni nei marketprice
            this.askMarketPrice = ordb.getAskMarketPrice();
            this.askMarketPriceOwner = ordb.getMarketPriceOwner("ask");
            this.bidMarketPrice = ordb.getBidMarketPrice();
            this.bidMarketPriceOwner = ordb.getMarketPriceOwner("bid");
            //cerco nella lista 
            Iterator<StopOrder> navi = this.ordb.getStopOrders().iterator();
            while(navi.hasNext()){
                StopOrder order = navi.next();
                //controllo se l'ordine che sto analizzando deve essere eseguito tramite la funzione apposita
                if(needsToBeExecuted(order)){
                    ordb.getStopOrders().remove(order);
                    this.stubTask.onlineUser = order.getUser(); 
                    order.execute(ordb, "stopprice met", this.stubTask);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        

    }

    /* LEVARE PRIMA DI CONSEGNARE
     *  (order.getExchangeType().equals("ask") && order.getPrice()<=askMarketPrice && !order.getUser().equals(askMarketPriceOwner)) ||
        (order.getExchangeType().equals("bid") && order.getPrice() >= bidMarketPrice)&& !order.getUser().equals(bidMarketPriceOwner))
    */

    public boolean needsToBeExecuted(Order order){
        String[] marketData = getRequestedMarketData(order.getExchangeType());
        if(marketData == null || marketData[0] == null || marketData[1] == null)return false;
        if(order.getUser().equals(marketData[0]))return false;
        return checkMarketPrice(marketData[2],order.getPrice());
    }

    public boolean checkMarketPrice(String interestedMarket, int marketPrice){
        if(interestedMarket.equals("ask") && marketPrice <= this.askMarketPrice)return true;
        else if(interestedMarket.equals("bid") && marketPrice >= this.bidMarketPrice)return true;
        else return false;
    }

    public String[] getRequestedMarketData(String requestedMarket){
        if(requestedMarket.equals("ask"))return new String[]{this.bidMarketPriceOwner,""+this.bidMarketPrice,"ask"};
        else if(requestedMarket.equals("bid"))return new String[]{this.askMarketPriceOwner,""+this.askMarketPrice,"bid"};
        else return null;
    }
}
