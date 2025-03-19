package JsonAccessedData.Orderbook;

import java.io.EOFException;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import Commands.Orders.Limitorder;
import Commands.Orders.Order;
import Commands.Orders.StopOrder;
import Communication.Values;
import JsonAccessedData.JsonAccessedData;
import Utils.OrderCache;
import Utils.OrderSorting;
import Utils.OrderSortingAdapter;
import okio.Okio;

public class Orderbook implements JsonAccessedData{
    private String jsonFilePath;
    //snippet che forse non uso
    //.add(PolymorphicJsonAdapterFactory.of(Order.class,"Order").withSubtype(Limitorder.class, "Limitorder"))
    //private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Moshi moshi = new Moshi.Builder().add(new OrderSortingAdapter()).add(PolymorphicJsonAdapterFactory.of(ZonedDateTime.class,"GMT")).add(PolymorphicJsonAdapterFactory.of(Order.class,"Order").withSubtype(Limitorder.class, "Limitorder")).build();
    private JsonAdapter<OrderClass> adapter = moshi.adapter(OrderClass.class);
    private ConcurrentSkipListMap<OrderSorting, Limitorder> askOrders = new ConcurrentSkipListMap<>(OrderSorting.PRICE_ASCENDING); // Prezzi crescenti
    private ConcurrentSkipListMap<OrderSorting, Limitorder> bidOrders = new ConcurrentSkipListMap<>(OrderSorting.PRICE_DESCENDING); // Prezzi decrescenti
    private ConcurrentLinkedQueue<StopOrder> stopOrders = new ConcurrentLinkedQueue<>();//devo ancora capire cosa sono
    private int askMarketPrice;// miglior prezzo di vendita -> prima entry dei bid ossia prezzo maggiore
    private int bidMarketPrice;// miglior prezzo d'acquisto -> prima entry degli ask ossia prezzo minore
    private String currentScope = "[ORDERBOOK]";
        
    public Orderbook(String jsonFilePath){
        this.jsonFilePath = System.getProperty("user.dir")+jsonFilePath;
        //System.out.println(this.currentScope+"Stoporders "+this.stopOrders);
    }
    
    @Override
    public int accessData(String keyword) {
        System.out.println(this.jsonFilePath);
        throw new UnsupportedOperationException("Unimplemented method 'accessData'");
    }
    
    @Override
    public synchronized void loadData() {
        try (JsonReader reader =JsonReader.of(Okio.buffer(Okio.source(new File(this.jsonFilePath)))))  {
            OrderClass orderData = adapter.fromJson(reader);
            this.askOrders.putAll(orderData.askMap);
            this.bidOrders.putAll(orderData.bidMap);
            this.updateMarketPrice();
        }
        catch(EOFException e){
            System.out.println(this.currentScope+"NO AVAILABLE ORDERS!");
        }
        catch(Exception e){
            System.out.println("[ORDERBOOK] LOADDATA: "+e.getMessage()+" "+e.getClass());
        }
    }

    public synchronized void updateMarketPrice(){
        if(this.getAskOrders().isEmpty())this.bidMarketPrice = 0;
        else this.bidMarketPrice = this.getAskOrders().firstEntry().getValue().getPrice();
        
        //else this.bidMarketPrice = 0;
        if(this.getBidOrders().isEmpty())this.askMarketPrice = 0;
        else this.askMarketPrice = this.getBidOrders().firstEntry().getValue().getPrice();
    }

    public synchronized void addData(Values val,String mapType) {
        Limitorder ord = (Limitorder)val;
        String orderbookEntry = ord.getUser()+":"+ord.getPrice();
        System.out.println(this.currentScope+"entry:"+orderbookEntry);
        ConcurrentSkipListMap<OrderSorting, Limitorder> ordermap = this.getRequestedMap(mapType);
        ordermap.put(new OrderSorting(ord.getGmt(),ord.getPrice(),ord.getOrderId()), ord);
        this.dataFlush();
        this.updateMarketPrice();
        return;
    }

    public synchronized void dataFlush(){
        OrderClass oc = new OrderClass(this.askOrders, this.bidOrders);
        try (JsonWriter writer = JsonWriter.of(Okio.buffer(Okio.sink(new File(this.jsonFilePath))))) {
            writer.setIndent(" ");
            adapter.toJson(writer, oc);
        } catch (Exception e) {
            System.out.println("Aiuto");
        }
        //this.updateMarketPrice();
        return;
    }

    public synchronized Order removeData(String mapType, OrderSorting orderbookEntry){
        Order ord = null;
        ConcurrentSkipListMap<OrderSorting, Limitorder> requestedMap = getRequestedMap(mapType);
        ord = requestedMap.remove(orderbookEntry);
        dataFlush();
        this.updateMarketPrice();
        return ord;
    }

    public void restoreOrders(OrderCache cache, Orderbook orderbook){
        while(cache.getSize()!=0){
            Limitorder ord = cache.removeOrder();
            orderbook.addData(ord, ord.getExchangeType());
        }
    }
    
    public OrderSorting getBestPriceAvailable(String tradeType, String myUsername,int price){
        ConcurrentSkipListMap<OrderSorting, Limitorder> requestedMap = getRequestedMap(tradeType);
        //System.out.println("[Orderbook] price="+price+", tradetype="+tradeType);
        if(requestedMap == null)return null;
        if(requestedMap.isEmpty())return null;
        Iterator<OrderSorting> navi =requestedMap.navigableKeySet().iterator();
        while(navi.hasNext()){
            OrderSorting currentKey = navi.next();
            if(requestedMap.get(currentKey).getUser().equals(myUsername))continue;
            if(price == 0 && !requestedMap.get(currentKey).getUser().equals(myUsername))return currentKey;
            if(tradeType.equals("ask") && requestedMap.get(currentKey).getPrice()>=price)return currentKey;
            if(tradeType.equals("bid") && requestedMap.get(currentKey).getPrice()<=price)return currentKey;
        }
        return null;    
    }

    public ConcurrentSkipListMap<OrderSorting, Limitorder> getRequestedMap(String request){
        if(request.equals("ask"))return this.askOrders;
        if(request.equals("bid"))return this.bidOrders;
        return null;
    }

    public ConcurrentSkipListMap<OrderSorting, Limitorder> getAskOrders() {
        return askOrders;
    }

    public ConcurrentSkipListMap<OrderSorting, Limitorder> getBidOrders() {
        return bidOrders;
    }
    
    public int mapLen() {
        return this.askOrders.size() +this.bidOrders.size();
    }

    public int getAskMarketPrice() {
        return this.askMarketPrice;
    }
    
    public int getBidMarketPrice() {
        return this.bidMarketPrice;
    }

    public ConcurrentLinkedQueue<StopOrder> getStopOrders() {
        return stopOrders;
    }

    public String getMarketPriceOwner(String marketType){
        ConcurrentSkipListMap<OrderSorting, Limitorder> requestedMap = getRequestedMap(marketType);
        if(requestedMap == null)return null;
        if(requestedMap.isEmpty())return null;
        
        
        return requestedMap.firstEntry().getValue().getUser();
    }

}