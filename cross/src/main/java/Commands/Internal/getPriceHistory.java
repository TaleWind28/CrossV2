package Commands.Internal;

import java.util.Map;
import java.util.TreeMap;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.PriceHistory.DailyTradeStats;
import JsonAccessedData.PriceHistory.TradeHistory;
import ServerTasks.GenericTask;

public class getPriceHistory implements Values{
    private int month;
    private int year;
    
    public getPriceHistory(String monYear) throws Exception{
        if(monYear.length()!=6)throw new Exception("mi piace l'uccello");
        String[] parsing = monYear.split("");
        this.month = Integer.parseInt(parsing[0]+parsing[1]);
        this.year = Integer.parseInt(parsing[2]+parsing[3]+parsing[4]+parsing[5]);
    }
    
    @Override
    public ServerMessage execute(JsonAccessedData data, String user, GenericTask genericTask) {
        TradeHistory storico = (TradeHistory) data;
        if(user.equals(""))return new ServerMessage("Per consultare lo storico bisogna creare un account o accedervi",101);
        return new ServerMessage(this.stringify(storico.monthlyTradesStat(year, month)),100);
    }

    public String stringify(TreeMap<Integer,DailyTradeStats>stat){
        StringBuilder output = new StringBuilder();
    
        if (stat == null || stat.isEmpty()) {
            output.append("non è stato trovato uno storico");
            return output.toString();
        }
        
        output.append("+====================================================+\n");
        output.append("|            REGISTRO STATISTICHE MENSILI        |\n");
        output.append("+====================================================+\n");
        
        for (Map.Entry<Integer, DailyTradeStats> entry : stat.entrySet()) {
            Integer key = entry.getKey();
            DailyTradeStats stats = entry.getValue();
            
            output.append("+----------------------------------+\n");
            output.append("|   GIORNO: " + key + String.format(" %-20s", convertIntToMonthName(this.month)) + "|\n");
            output.append("+----------------------------------+\n");
            
            // Aggiungi l'oggetto DailyTradeStats
            if (stats != null) {
                // Sostituiamo i caratteri Unicode con ASCII semplici
                String prettyPrint = stats.prettyPrint();
                output.append(prettyPrint).append("\n");
            } else {
                output.append("Valore nullo per questa chiave.\n");
            }
            
            output.append("\n"); // Riga vuota per separare le entries
        }
        return output.toString();
    }

    public String convertIntToMonthName(int month) {
        if (month < 1 || month > 12) {
            return "Mese non valido";
        }
        
        String[] mesi = {
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
        };
        
        return mesi[month - 1];
    }

    @Override
    public void setUsername(String user) {

    }

    @Override
    public String getUsername() {
        return "unused";    
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "GetPriceHistory{ month='"+this.month+"', year='"+this.year+"}";    
    }

}
