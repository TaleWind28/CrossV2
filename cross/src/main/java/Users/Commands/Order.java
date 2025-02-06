package Users.Commands;

import java.util.concurrent.ConcurrentHashMap;
import Communication.Message;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import ServerTasks.GenericTask;
import Users.User;
import Users.Commands.CommandBehaviours.CommandBehaviour;


public class Order implements UserCommand{
    protected int orderID;//CODICE UNIVOCO DI RICONOSCIMENTO DELL'ORDINE
    protected String type;//tipo di ordine(market/limit/stop)
    protected String exchangeType;//modalità ordine: ask/bid
    protected int size;//qtà di bitcoin
    protected int price;//soglia di prezzo
    protected String user;//username di chi ha piazzato l'ordine
    ///campi non visibili nel Json
    transient protected CommandBehaviour myBehaviour;//comportamento ordine
    transient protected ConcurrentHashMap<String, User> map;//interessante
    transient protected Orderbook orderbook;//orderbook dove "piazzare" gli ordini
    transient protected int unicode;//unicode degli ordini -> NON è IL CODICE UNIVOCO DELL'ORDINE SINGOLO

    public Order(String orderType, String type, int size, int price,int orderNumber, Orderbook orderbook, CommandBehaviour behaviour){
        this.type = orderType;
        this.exchangeType = type;
        this.size = size;
        this.price = price;
        this.orderID = orderNumber;
        this.orderbook = orderbook;
        setBehaviour(behaviour);
    }   

    @Override
    public Message execute(GenericTask context) {
        this.user = context.onlineUser;
        System.out.println(this.user);
        return myBehaviour.executeOrder(this,context);
    }
    
    public String toString() {
        return "Order{" +
               "orderType='" + this.type + '\'' +
               ", type='" + this.exchangeType + '\'' +
               ", size=" + this.size +
               ", price=" + this.price +
               ", myBehaviour=" + (this.myBehaviour != null ? this.myBehaviour.toString() : "null") +
               '}';
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public JsonAccessedData getJsonAccessedData() {
        return this.orderbook;
    }

    public void setBehaviour(CommandBehaviour behaviour){
        this.myBehaviour = behaviour;
        this.unicode = behaviour.getUnicode();
        return;
    }

    @Override
    public void setUser(String username) {
        this.user = username;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public void addSize(int newSize){
        System.out.println("[addSize] taglia nuova:"+newSize);
        this.size += newSize;
    }

    @Override
    public String[] getInfo(){
        String[] info = new String[4];
        info[0] = this.exchangeType;
        info[1] = ""+this.size;
        info[2] = ""+this.price;
        return info;
    }
    
    @Override
    public int getUnicode() {
        return this.unicode;
    }

    public int getorderID() {
        return orderID;
    }
    
    public Orderbook getOrderbook() {
        return orderbook;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public String getUser() {
        return user;
    }
    
    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

}   
