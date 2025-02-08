package Commands.Orders;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;

public class MarketOrder implements Values,Order{
    private String exchangeType;
    private int size;
    private int orderID;
    private String user;

    public MarketOrder(String exchangeType,int size){
        this.exchangeType = exchangeType;
        this.size = size;
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data,String user){
        if(user.equals(""))return new ServerMessage("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        Orderbook orderbook = (Orderbook)data;
        //controllo che l'utente sia autenticato
        if(user.equals(""))return new ServerMessage("[401]: Per effettuare ordini bisogna creare un account o accedervi",401);
        //ricreo l'ordine
        //Order ord = (Order)cmd;
        //controllo che l'orderID sia un numero valido
        //if(ord.getorderID() == -1)return new Message("[400]: ord non correttamente formato");
        //recupero l'orderbook
        //preparo exchangetype e responsemessage per dopo
        String exchangetype = null;
        //ha senso preparare il responsemessege adesse perchè se compro compro tutto dal solito utente, il quale verrà aggiunto successivamente al messaggio
        String responseMessage = null;
        //preparo le stringhe per stampa e richiesta userbook
        switch (this.exchangeType) {
            case "ask":
                exchangetype = "bid";
                responseMessage = "[200]: OrderCode[" + this.orderID+"] n°"+this.size+" bitcoin venduti all'utente ";
                break;
            case "bid":
                exchangetype = "ask";
                responseMessage = "[200]: OrderCode[" +this.orderID +"] n°"+this.size+" bitcoin comprati dall'utente ";
                break;
        }
        //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
        String orderbookEntry = orderbook.getBestPriceAvailable(this.size, exchangetype,user);
        //controllo che esista una entry per il mio ordine
        if(orderbookEntry == null)return new ServerMessage("[404] Non sono stati trovati ordini per le tue esigenze",-1);
        //rimuovo l'ordine dall'orderbook
        Limitorder evadedOrder = (Limitorder)orderbook.removeData(exchangetype,orderbookEntry);
        //controllo che l'ordine sia stato evaso
        if(evadedOrder == null)return new ServerMessage("[404] Non sono stati trovati ordini per le tue esigenze",-1);
        //System.out.println("taglia ordine utente:"+ord.getSize()+", taglia ordine mercato:"+evadedOrder.getSize());
        //controllo quanti btc sono stati comprati
        if(evadedOrder.getSize()>this.size){
            //sottraggo la taglia di bitcoin comprata
            evadedOrder.addSize(-(this.size));
            //rimetto l'offerta sul mercato
            orderbook.addData(evadedOrder, exchangetype);
        }
        responseMessage+=evadedOrder.getUser()+" pagando "+(evadedOrder.getPrice()*this.size)+"$";
        return new ServerMessage(responseMessage,200);
        //return new ServerMessage("Ordine Correttamente Evaso",100);
    }

    @Override
    public String toString() {
        return "Marketorder{ exchangeType="+this.exchangeType+" size="+this.size+" orderID="+this.orderID+"}";    
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    @Override
    public String getUser() {
        return this.user;
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
    public int getOrderID() {
        return this.orderID;
    }

    @Override
    public void setUsername(String user) {
        this.user = user;    
    }

    @Override
    public String getUsername() {
        return this.user;    
    }
}
