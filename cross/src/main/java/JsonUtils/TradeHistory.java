package JsonUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import okio.BufferedSource;
import okio.Okio;

public class TradeHistory implements JsonAccessedData{
    private String jsonFilePath;
    private TreeMap<Integer, TreeMap<DayTime, Trade>> historicalData = new TreeMap<>();
    private Moshi moshi = new Moshi.Builder().build();
    private JsonAdapter<List<Trade>> tradeAdapter = moshi.adapter(Types.newParameterizedType(List.class, Trade.class));

    public TradeHistory(String jsonFilePath){
        this.jsonFilePath = jsonFilePath;
    }

    public TreeMap<Integer,TreeMap<DayTime,Trade>> monthlyTrades(String filePath)throws Exception{
        //TreeMap<Integer, TreeMap<DayTime, Trade>> historicalData = new TreeMap<>();
        
        File file = new File(this.jsonFilePath);
        BufferedSource source = Okio.buffer(Okio.source(file));
        JsonReader reader = JsonReader.of(source);
        
        // Crea adapter per Trade
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Trade> tradeAdapter = moshi.adapter(Trade.class);

        try {
            // Inizio del JSON
            reader.beginObject();
            
            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("trades".equals(name)) {
                    // Inizio dell'array trades
                    reader.beginArray();
                    
                    while (reader.hasNext()) {
                        // Usa l'adapter per leggere automaticamente il trade
                        Trade trade = tradeAdapter.fromJson(reader);
                        
                        if (trade != null) {
                            // Converti timestamp in LocalDateTime
                            LocalDateTime tradeDate = LocalDateTime.ofInstant(
                                    Instant.ofEpochSecond(trade.getTimestamp()), 
                                    ZoneId.systemDefault());
                            
                            int year = tradeDate.getYear();
                            // int month = tradeDate.getMonthValue();
                            // int day = tradeDate.getDayOfMonth();
                            
                            // Crea la chiave DayTime che include il mese
                            DayTime dayTime = new DayTime(trade.getTimestamp());
                            
                            // Ottieni o crea la TreeMap per l'anno
                            TreeMap<DayTime, Trade> yearData = 
                                    historicalData.computeIfAbsent(year, k -> new TreeMap<>());
                            
                            // Aggiungi il trade alla TreeMap dell'anno
                            yearData.put(dayTime, trade);
                        }
                    }
                    
                    reader.endArray();
                } else {
                    // Salta altri campi se presenti
                    reader.skipValue();
                }
            }
            
            reader.endObject();
        } finally {
            reader.close();
        }
        
        return historicalData;
    }

    @Override
    public int accessData(String keyword) {
        throw new UnsupportedOperationException("Unimplemented method 'accessData'");
    }

    @Override
    public void loadData() {
        try {
            this.monthlyTrades(jsonFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.printTradeMap(this.historicalData.get(2024), 0);
    }

    //stampa della mappa
    public static void printTradeMap(TreeMap<DayTime, Trade> tradeMap, int limit) {
        if (tradeMap == null || tradeMap.isEmpty()) {
            System.out.println("La mappa è vuota.");
            return;
        }
        
        System.out.println("\n=== CONTENUTO DELLA TREEMAP ===");
        System.out.println("Totale elementi: " + tradeMap.size());
        System.out.println("==================================");
        System.out.printf("%-12s | %-15s | %-8s | %-8s | %-10s | %-15s | %-12s%n", 
                "GIORNO", "TIMESTAMP", "ORDER ID", "SIZE", "PRICE", "TYPE", "ORDER TYPE");
        System.out.println("--------------------------------------------------------------------------------------------------");
        
        int count = 0;
        for (Map.Entry<DayTime, Trade> entry : tradeMap.entrySet()) {
            DayTime key = entry.getKey();
            Trade trade = entry.getValue();
            
            // Formatta il timestamp come data leggibile
            String formattedDate = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(trade.getTimestamp()), 
                    ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Formatta il prezzo per migliorare la leggibilità (se in centesimi o altra unità)
            String formattedPrice = String.format("%,.2f", trade.getPrice() / 100.0);
                    
            System.out.printf("Giorno %-6d | %-15s | %-8d | %-8d | %-10s | %-15s | %-12s%n",
                    key.getDay(),
                    formattedDate,
                    trade.getOrderId(),
                    trade.getSize(),
                    formattedPrice,
                    trade.getTimestamp(),
                    trade.getOrderType());
            
            count++;
            if (limit > 0 && count >= limit) {
                System.out.println("...");
                System.out.println("[Mostrati " + count + " elementi su " + tradeMap.size() + " totali]");
                break;
            }
        }
        
        System.out.println("==================================");
    }

}
