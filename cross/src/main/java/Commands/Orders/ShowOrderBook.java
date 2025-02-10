package Commands.Orders;

import java.util.Map;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import ServerTasks.GenericTask;


public class ShowOrderBook implements Values {
    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        Orderbook orderbook = (Orderbook) data;
        String prettyPrintedString = "------------------------------------------------------------------------------------------\n" + //
                        "User\t  ExchangeType\tBitcoin Size\tPrice per Bitcoin\tTotal Price\tOrder ID\n";
        //sta cosa mi fa schifo, dovrei usare strategy ma non so se ne ho voglia -> spoiler non ne ho voglia
        if(orderbook.mapLen()!= 0){        
            prettyPrintedString = prettyPrinting(orderbook, "ask", prettyPrintedString);
            prettyPrintedString = prettyPrinting(orderbook, "bid", prettyPrintedString);
        }else{
            prettyPrintedString +="\t\t------------ NO ORDER AVAILABLE! -------------\n";    
        }
        prettyPrintedString +="----------------------------------------------------------------------------------------";
        return new ServerMessage(prettyPrintedString, 100);
    }


    public String prettyPrinting(Orderbook orderbook, String requestedmap, String prettyPrinting) {
        
        for(Map.Entry<String,Limitorder> entry: orderbook.getRequestedMap(requestedmap).entrySet()){
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
