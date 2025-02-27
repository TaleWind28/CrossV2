package JsonUtils.PriceHistory;


import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;

import JsonUtils.JsonAccessedData;
import okio.Okio;

public class TradeHistory implements JsonAccessedData{
    private String jsonFilePath;
    private TreeMap<Integer, TreeMap<DayTime, Trade>> historicalData = new TreeMap<>();
    private Moshi moshi = new Moshi.Builder().build();
    private JsonAdapter<Trade> tradeAdapter = moshi.adapter( Trade.class);

    public TradeHistory(String jsonFilePath){
        this.jsonFilePath = System.getProperty("user.dir")+jsonFilePath;
    }

    public TreeMap<Integer,DailyTradeStats> monthlyTradesStat(int year,int month){
        //TreeMap<Integer, TreeMap<DayTime, Trade>> historicalData = new TreeMap<>();
        TreeMap<DayTime,Trade>months = this.historicalData.get(year);
        TreeMap<Integer,DailyTradeStats> stats = new TreeMap<>();
        for(DayTime key : months.keySet()){
            if(key.getMonth() == month){
                //creo la entry
                DailyTradeStats stat = new DailyTradeStats(months.get(key));
                //se non ho ancora registrato quel giorno aggiungo chiave e valore
                if(!stats.containsKey(key.getDay()))stats.put(key.getDay(), stat);
                //altrimenti confronto i dati nuovi
                else stats.get(key.getDay()).updateStats(stat);
            }
        }
        return stats;
    }

    @Override
    public int accessData(String keyword) {
        throw new UnsupportedOperationException("Unimplemented method 'accessData'");
    }

    @Override
    public void loadData() {
        try(JsonReader reader = JsonReader.of(Okio.buffer(Okio.source(new File(this.jsonFilePath))))) {
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

                            // Crea la chiave DayTime che include il mese
                            DayTime dayTime = new DayTime(trade.getTimestamp());
                            
                            // Ottieni o crea la TreeMap per l'anno
                            TreeMap<DayTime, Trade> yearData = historicalData.computeIfAbsent(year, k -> new TreeMap<>());
                            
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        //this.printStatsMap(this.monthlyTradesStat(2024,9),"Settembre");
        //this.printTradeMap(this.historicalData.get(2024), 0);
    }

    public void printStatsMap(TreeMap<Integer, DailyTradeStats> statsMap, String month) {
        if (statsMap == null || statsMap.isEmpty()) {
            System.out.println("La mappa è vuota o non inizializzata.");
            return;
        }
        
        System.out.println("╔═════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     REGISTRO STATISTICHE GIORNALIERE                ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════╝");
        
        for (Map.Entry<Integer, DailyTradeStats> entry : statsMap.entrySet()) {
            Integer key = entry.getKey();
            DailyTradeStats stats = entry.getValue();
            
            System.out.println("┌──────────────────────────────────┐");
            System.out.println("│   GIORNO: " + key +String.format(" %-20s",month) + "│");
            System.out.println("└──────────────────────────────────┘");
            
            // Stampa l'oggetto DailyTradeStats
            if (stats != null) {
                System.out.println(stats.prettyPrint());
            } else {
                System.out.println("Valore nullo per questa chiave.");
            }
            
            System.out.println(); // Riga vuota per separare le entries
        }
    }
    //stampa della mappa
    public void printTradeMap(TreeMap<DayTime, Trade> tradeMap, int limit) {
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
