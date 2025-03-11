package Commands.Orders;

import java.util.Map;

import Communication.Values;
import Communication.Messages.OrderResponseMessage;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Orderbook.Orderbook;
import ServerTasks.GenericTask;
import Utils.AnsiColors;
import Utils.OrderSorting;


public class ShowOrderBook implements Values {
    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        Orderbook orderbook = (Orderbook) data;
        if(user.equals(""))return new OrderResponseMessage(-1,"Per consultare l'orderbook bisogna creare un account o accedervi");
        StringBuilder output = new StringBuilder();
        // Intestazione con i prezzi di mercato
        output.append(AnsiColors.BLUE_DARK+"\"+=======================================================================+\n");
        output.append(String.format("|  PREZZI DI MERCATO:  ASK: %-14d  BID: %-14d         |\n", 
        orderbook.getAskMarketPrice(), orderbook.getBidMarketPrice()));
        output.append("+=======================================================================+\n");
    
        // Intestazione della tabella
        output.append("| User        Exchange   Bitcoin    Prezzo per     Prezzo       ID      |\n");
        output.append("|             Type       Size       Bitcoin        Totale               |\n");
        output.append("+=======================================================================+\n");
        if (orderbook.mapLen() != 0) {
            // ASK Orders
            output.append(AnsiColors.GREEN_DARK+"|                           ASK ORDERS                                  |\n");
            output.append("+=======================================================================+\n");
            appendPrettyOrders(output, orderbook, "ask");
        
            // BID Orders
            output.append(AnsiColors.BRIGHT_RED+"+=======================================================================+\n");
            output.append("|                           BID ORDERS                                  |\n");
            output.append("+=======================================================================+\n");
            appendPrettyOrders(output, orderbook, "bid");
        } else {
            output.append("|                  NESSUN ORDINE DISPONIBILE                    |\n");
        }
        output.append("+=======================================================================+\n"+AnsiColors.RESET);
        return new ServerMessage(output.toString(), 100);
    }

    private void appendPrettyOrders(StringBuilder output, Orderbook orderbook, String requestedMap) {
        Map<OrderSorting, Limitorder> orders = orderbook.getRequestedMap(requestedMap);
        if (orders.isEmpty()) {
            output.append("|              Nessun ordine di tipo " + requestedMap.toUpperCase() + "               |\n");
            return;
        }
        for (Map.Entry<OrderSorting, Limitorder> entry : orders.entrySet()) {

            Limitorder ord = entry.getValue();

            long price = ord.getPrice();
            long totalPrice = (long)(price * ord.getSize());
            output.append(String.format("| %-10s  %-9s  %-10s  %-13d  %-11d  %-6d |\n",
                          ord.getUser(), ord.getExchangeType(), ord.getSize(), ord.getPrice(), totalPrice, ord.getOrderId()));
        }
    }
    
    public String prettyPrinting(Orderbook orderbook, String requestedmap) {
        String prettyPrinting = new String();
        for(Map.Entry<OrderSorting,Limitorder> entry: orderbook.getRequestedMap(requestedmap).entrySet()){
            Order ord = entry.getValue();
            prettyPrinting+=ord.getUser()+"\t\t"+ord.getExchangeType()+"\t \t"+ord.getSize()+"\t \t"+ord.getPrice()+"\t\t    "+ord.getPrice()*ord.getSize()+"\t\t   "+ord.getOrderId()+"\n";
        }

        return prettyPrinting;
        
    }


    @Override
    public void setUsername(String user) {
        return;
    }


    @Override
    public String getUsername() {
        return "unused";
    }
}
