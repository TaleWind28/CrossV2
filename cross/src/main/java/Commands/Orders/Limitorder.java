package Commands.Orders;

import java.time.ZonedDateTime;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Orderbook;
import ServerTasks.GenericTask;
import Utils.OrderCache;

public class Limitorder extends Order implements Values{
    private String exchangeType;
    //private int size;
    //private int price;

    public Limitorder(String exchangeType,int size, int price){
        super();
        this.exchangeType = exchangeType;
        super.setSize(size);
        super.setPrice(price);
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        if(user.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        super.setOrderId(task.getProgressiveOrderNumber());
        task.increaseProgressiveOrderNumber();
        Orderbook orderbook = (Orderbook)data;
        //la faccio semplice per vedere se funziona
        super.setGmt(ZonedDateTime.now());
        String result = "";
        String reverseType = "";
        OrderCache cache = new OrderCache();
        reverseType = super.findOppositeMap(this.exchangeType);
        //int i = 0;
        while(result!=null && this.getSize()!=0){
            //if(i == 2)break;
            result = this.evadeOrder(reverseType, user, orderbook, cache, result);
            //i++;
        }
        //System.out.println("[Limitorder] cicles"+i);
        
        // while(count>0){
        //     Limitorder ord = cache.removeOrder();
        //     System.out.println("[Limitorder-cachesizeadder]");
        //     switch(reverseType){
        //         case "ask":
        //             if(ord.getPrice()>this.price){
        //                 //restore orders
        //                 this.setSize(this.getSize()+ord.getSize());
        //                 cache.addOrder(ord);
        //             }
        //         case "bid":
        //             if(ord.getPrice()<this.price){
        //                 //restore orders
        //                 this.setSize(this.getSize()+ord.getSize());
        //                 cache.addOrder(ord);
        //             }
        //         }
        //     count--;
        //     System.out.println("[Limitorder-cache_orderevader] "+ord.toString()+", size="+this.getSize());
        // }
        // orderbook.restoreOrders(cache, orderbook);
        if (this.getSize() == 0)return new OrderResponseMessage(this.getOrderId(),"Order executed successfully!");
        else orderbook.addData(this, this.exchangeType);
        
        return new OrderResponseMessage(100,"Order executed successfully!");
    }

    // @Override
    // public String evadeOrder(String exchangetype,String user,Orderbook orderbook, OrderCache cache,String responseMessage){
    //     System.out.println("[Order-evadeOrd] entro in evaded con size= "+this.getSize()+",exchange type"+exchangetype+",utente"+user);
    //     //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
    //     OrderSorting orderbookEntry = orderbook.getBestPriceAvailable(exchangetype,user);
    //     System.out.println("[Order-evadeOrd] entry="+orderbookEntry);
    //     //controllo che esista una entry per il mio ordine
    //     if(orderbookEntry == null){System.out.println("[Order]mamma");return null;}
    //     responseMessage = ""+this.getOrderId();
        
    //     //rimuovo l'ordine dall'orderbook
    //     Limitorder evadedOrder = (Limitorder)orderbook.removeData(exchangetype,orderbookEntry);
    //     //salvo l'ordine rimosso dall'ordebook in caso non si possa evadere completamente il marketorder
    //     cache.addOrder(evadedOrder);
    //     //controllo che l'ordine sia stato evaso
    //     if(evadedOrder == null){
    //         System.out.println("[Order-evadeOrd]ordine inevdibile");
    //         return null;
    //     }
    //     //controllo quanti btc sono stati comprati
    //     if(evadedOrder.getSize()>this.getSize()){
    //         //bitcoinBought = this.getSize();
    //         //sottraggo la taglia di bitcoin comprata
    //         evadedOrder.addSize(-(this.getSize()));
    //         //rimetto l'offerta sul mercato
    //         orderbook.addData(evadedOrder, exchangetype);
    //         //imposto la size a 0 perchè ho sicuramente evaso tutto l'ordine
    //         this.setSize(0);
    //     }
    //     this.setSize(this.getSize() -evadedOrder.getSize());
    //     System.out.println("[Order-evadeOrd] size"+this.getSize());
    //     return responseMessage;
    // }

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
        //System.out.println("[Limitorder-addSize] size"+size);
        int sub = super.getSize()+size;
        //System.out.println("[limitorder] subtract"+sub);
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
