package Users.Commands.CommandBehaviours;

import java.util.Map;

import Communication.Message;
import JsonMemories.Orderbook;
import ServerTasks.GenericTask;
import Users.Commands.Order;
import Users.Commands.UserCommand;

public class ShowOrderBook implements CommandBehaviour{

    @Override
    public Message executeOrder(UserCommand cmd, GenericTask context) {
        Order ord = (Order)cmd;
        Orderbook orderbook = ord.getOrderbook();
        String prettyPrintedString = "------------------------------------------------------------------------------------------\n" + //
                        "User\t  ExchangeType\tBitcoin Size\tPrice per Bitcoin\tTotal Price\tOrder ID\n";
        //sta cosa mi fa schifo, dovrei usare strategy ma non so se ne ho voglia -> spoiler non ne ho voglia
        prettyPrintedString = prettyPrinting(orderbook, "ask", prettyPrintedString);
        prettyPrintedString = prettyPrinting(orderbook, "bid", prettyPrintedString);
        prettyPrintedString +="----------------------------------------------------------------------------------------";
        return new Message(prettyPrintedString,200);    
    }

    public String prettyPrinting(Orderbook orderbook, String requestedmap, String prettyPrinting) {
        
        for(Map.Entry<String,Order> entry: orderbook.getRequestedMap(requestedmap).entrySet()){
            Order ord = entry.getValue();
            prettyPrinting+=ord.getUser()+"\t\t"+ord.getExchangeType()+"\t \t"+ord.getSize()+"\t \t"+ord.getPrice()+"\t\t    "+ord.getPrice()*ord.getSize()+"\t\t   "+ord.getorderID()+"\n";
        }

        return prettyPrinting;
        
    }

    @Override
    public int getUnicode() {
        return 110;
    }
    
}
