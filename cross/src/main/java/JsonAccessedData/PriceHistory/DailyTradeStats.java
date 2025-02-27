package JsonAccessedData.PriceHistory;

public class DailyTradeStats {
    private int openingPrice;
    private int closingPrice;
    private int maxAskPrice = -1;
    private int maxBidPrice = -1;
    private int minAskPrice = -1;
    private int minBidPrice = -1;

    public DailyTradeStats(Trade trade){
        if(trade.getType().equals("ask")){
            this.maxAskPrice = trade.getPrice();
            this.minAskPrice = trade.getPrice();
        }
        if(trade.getType().equals("bid")){
            this.minBidPrice = this.maxBidPrice = trade.getPrice();
        }
        this.closingPrice = trade.getPrice();
        this.openingPrice = trade.getPrice();
        

    }

    public void updateStats(DailyTradeStats other){
        
        // Aggiorna i maxPrice
        this.maxAskPrice = Math.max(this.maxAskPrice, other.maxAskPrice);
        this.maxBidPrice = Math.max(this.maxBidPrice, other.maxBidPrice);
        // Aggiorna minAskPrice
        if(this.minAskPrice == -1)this.minAskPrice = other.minAskPrice;
        else if(other.minAskPrice!=-1)this.minAskPrice = Math.min(this.minAskPrice, other.minAskPrice);
        //aggiorno minBidPrice
        if(this.minBidPrice == -1)this.minBidPrice = other.minBidPrice;
        else if(other.minBidPrice!=-1)this.minBidPrice = Math.min(this.minBidPrice, other.minBidPrice);

        this.closingPrice = other.closingPrice;
            
    }

    public void setClosingPrice(int closingPrice) {
        this.closingPrice = closingPrice;
    }
    public void setMaxAskPrice(int maxAskPrice) {
        this.maxAskPrice = maxAskPrice;
    }
    public void setMaxBidPrice(int maxBidPrice) {
        this.maxBidPrice = maxBidPrice;
    }
    public void setMinAskPrice(int minAskPrice) {
        this.minAskPrice = minAskPrice;
    }
    public void setMinBidPrice(int minBestBidPrice) {
        this.minBidPrice = minBestBidPrice;
    }
    public void setOpeningPrice(int openingPrice) {
        this.openingPrice = openingPrice;
    }
    public int getClosingPrice() {
        return closingPrice;
    }
    public int getMaxAskPrice() {
        return maxAskPrice;
    }
    public int getMaxBidPrice() {
        return maxBidPrice;
    }
    public int getMinAskPrice() {
        return minAskPrice;
    }
    public int getMinBidPrice() {
        return minBidPrice;
    }
    public int getOpeningPrice() {
        return openingPrice;
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════╗\n");
        sb.append("║     DAILY TRADING STATISTICS     ║\n");
        sb.append("╠══════════════════════════════════╣\n");
        
        // Prezzi di apertura e chiusura
        sb.append(String.format("║ Opening Price:  %11d      ║\n", this.openingPrice));
        sb.append(String.format("║ Closing Price:  %11d      ║\n", this.closingPrice));
        sb.append("╠══════════════════════════════════╣\n");
        
        // Prezzi Ask
        sb.append("║ ASK PRICES                       ║\n");
        sb.append(String.format("║ Maximum:        %11s      ║\n", 
                  (this.maxAskPrice == -1) ? "N/A" : String.valueOf(this.maxAskPrice)));
        sb.append(String.format("║ Minimum:        %11s      ║\n", 
                  (this.minAskPrice == -1) ? "N/A" : String.valueOf(this.minAskPrice)));
        sb.append("╠══════════════════════════════════╣\n");
        
        // Prezzi Bid
        sb.append("║ BID PRICES                       ║\n");
        sb.append(String.format("║ Maximum:        %11s      ║\n", 
                  (this.maxBidPrice == -1) ? "N/A" : String.valueOf(this.maxBidPrice)));
        sb.append(String.format("║ Minimum:        %11s      ║\n", 
                  (this.minBidPrice == -1) ? "N/A" : String.valueOf(this.minBidPrice)));
        sb.append("╚══════════════════════════════════╝\n");
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return prettyPrint();
    }

    
}
