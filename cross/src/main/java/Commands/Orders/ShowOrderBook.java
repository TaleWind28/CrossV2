package Commands.Orders;

import java.util.Map;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import Users.Commands.Order;


public class ShowOrderBook implements Values{
    @Override
    public ServerMessage execute(JsonAccessedData data){
        Orderbook orderbook = (Orderbook) data;
        String prettyPrintedString = "------------------------------------------------------------------------------------------\n" + //
                        "User\t  ExchangeType\tBitcoin Size\tPrice per Bitcoin\tTotal Price\tOrder ID\n";
        //sta cosa mi fa schifo, dovrei usare strategy ma non so se ne ho voglia -> spoiler non ne ho voglia
        prettyPrintedString = prettyPrinting(orderbook, "ask", prettyPrintedString);
        prettyPrintedString = prettyPrinting(orderbook, "bid", prettyPrintedString);
        prettyPrintedString +="----------------------------------------------------------------------------------------";
        return new ServerMessage(prettyPrintedString, 100);
    }


    public String prettyPrinting(Orderbook orderbook, String requestedmap, String prettyPrinting) {
        
        for(Map.Entry<String,Order> entry: orderbook.getRequestedMap(requestedmap).entrySet()){
            Order ord = entry.getValue();
            prettyPrinting+=ord.getUser()+"\t\t"+ord.getExchangeType()+"\t \t"+ord.getSize()+"\t \t"+ord.getPrice()+"\t\t    "+ord.getPrice()*ord.getSize()+"\t\t   "+ord.getorderID()+"\n";
        }

        return prettyPrinting;
        
    }

    
}
