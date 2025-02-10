package JsonMemories;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import Commands.Values;
import Commands.Orders.Limitorder;
import Commands.Orders.Order;
import Commands.Orders.StopOrder;
import Utils.PriceComparator;
import okio.Okio;

public class Orderbook implements JsonAccessedData{
    private String jsonFilePath;
    //snippet che forse non uso
    //.add(PolymorphicJsonAdapterFactory.of(Order.class,"Order").withSubtype(Limitorder.class, "Limitorder"))
    //private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Moshi moshi = new Moshi.Builder().add(PolymorphicJsonAdapterFactory.of(ZonedDateTime.class,"GMT")).add(PolymorphicJsonAdapterFactory.of(Order.class,"Order").withSubtype(Limitorder.class, "Limitorder")).build();
    private JsonAdapter<OrderClass> adapter = moshi.adapter(OrderClass.class);
    private TreeMap<String, Limitorder> askOrders; // Prezzi crescenti
    private  TreeMap<String, Limitorder> bidOrders; // Prezzi decrescenti
    private  ConcurrentLinkedQueue<StopOrder> stopOrders;//devo ancora capire cosa sono
    private String currentScope = "[ORDERBOOK]";
        
    public Orderbook(String jsonFilePath){
        this.jsonFilePath = jsonFilePath;
        this.askOrders = new TreeMap<>(new PriceComparator());
        this.bidOrders = new TreeMap<>(new PriceComparator().reversed());
        this.stopOrders = new ConcurrentLinkedQueue<>(); 
        System.out.println(this.currentScope+"Stoporders"+this.stopOrders);
    }
    
    @Override
    public int accessData(String keyword) {
        System.out.println(this.jsonFilePath);
        throw new UnsupportedOperationException("Unimplemented method 'accessData'");
    }
    
    @Override
    public synchronized void loadData() {
        //System.out.println("copio");
        try (JsonReader reader =JsonReader.of(Okio.buffer(Okio.source(new File(this.jsonFilePath)))))  {
            OrderClass orderData = adapter.fromJson(reader);
            this.askOrders = new TreeMap<>(orderData.askMap);
            this.bidOrders = new TreeMap<>(orderData.bidMap);
            //System.out.println("copio");
        }
        catch(Exception e){System.out.println("[ORDERBOOK] LOADDATA: "+e.getMessage()+" "+e.getClass());}
        return;
    }

    public synchronized void addData(Values val,String mapType) {
        Limitorder ord = (Limitorder)val;
        String orderbookEntry = ord.getUser()+":"+ord.getPrice();
        System.out.println(this.currentScope+"entry:"+orderbookEntry);
        TreeMap<String,Limitorder> ordermap = this.getRequestedMap(mapType);
        if(ordermap.containsKey(orderbookEntry))ordermap.get(orderbookEntry).addSize(ord.getSize());
        else ordermap.put(orderbookEntry, ord);
        this.dataFlush();
        return;
    }

    public TreeMap<String,Limitorder> getRequestedMap(String request){
        if(request.equals("ask"))return this.askOrders;
        else return this.bidOrders;
    }

    public synchronized void dataFlush(){
        OrderClass oc = new OrderClass(this.askOrders, this.bidOrders);
        try (JsonWriter writer = JsonWriter.of(Okio.buffer(Okio.sink(new File(this.jsonFilePath))))) {
            writer.setIndent(" ");
            adapter.toJson(writer, oc);
            //System.out.println("scritto");
        } catch (Exception e) {
            System.out.println("Aiuto");
        }
        return;
    }

    public synchronized Order removeData(String mapType, String orderbookEntry){
        Order ord = null;
        TreeMap<String,Limitorder> requestedMap = getRequestedMap(mapType);
        System.out.println("entry "+orderbookEntry);
        ord = requestedMap.remove(orderbookEntry);
        dataFlush();
        return ord;
    }

    public synchronized String getBestPriceAvailable(int size,String tradeType, String myUsername){
        TreeMap<String,Limitorder> requestedMap = getRequestedMap(tradeType);
        for(String key: requestedMap.keySet()){
            if(key.split(":")[0].equals(myUsername))continue;
            if(requestedMap.get(key).getSize()<size)continue;
            else return key;
        }
        return null;
    }

    public synchronized TreeMap<String, Limitorder> getAskOrders() {
        return askOrders;
    }

    public synchronized TreeMap<String, Limitorder> getBidOrders() {
        return bidOrders;
    }
    
    public synchronized int mapLen() {
        return this.askOrders.size() +this.bidOrders.size();
    }

    public String pretty() {
        //System.out.println(this.askOrders.toString());
        
        String prettyPrinting = "   ExchangeType\t  Bitcoin Size\t Price per Bitcoin\n";
        prettyPrinting = prettyPrinting(this,"ask",prettyPrinting);
        prettyPrinting = prettyPrinting(this,"bid",prettyPrinting);
        return prettyPrinting;
        
    }

    public String prettyPrinting(Orderbook orderbook, String requestedmap, String prettyPrinting) {
        
        for(Map.Entry<String,Limitorder> entry: orderbook.getRequestedMap(requestedmap).entrySet()){
            Order ord = entry.getValue();
            prettyPrinting+="\t"+ord.getExchangeType()+"\t \t"+ord.getSize()+"\t \t"+ord.getPrice()+"\n";
        }

        return prettyPrinting;
        
    }

}