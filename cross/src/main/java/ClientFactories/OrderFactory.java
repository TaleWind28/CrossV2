package ClientFactories;


import Commands.Orders.CancelOrder;
import Commands.Orders.Limitorder;
import Commands.Orders.MarketOrder;
import Commands.Orders.StopOrder;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import Users.Commands.UserCommand;
import Utils.CustomExceptions.UnrecognizedOrderException;

public class OrderFactory{

    public Values createValue(String[] command) {
        try {
            //sistemo il tipo di ordine per avere solo la parte significativa
            String orderType = command[0].toLowerCase().replace("insert", "").replace("Values", "");
            System.out.println(orderType);
            /*RIARRANGIARE LA FACTORY */
            switch (orderType) {    
                case "cancel":
                    return new CancelOrder();    
                    //break;
                case "market":   
                    return new MarketOrder(orderType,command[1],Integer.parseInt(command[2]),0);
                case "limit":
                    return new Limitorder(orderType,command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]));
                case "stop":
                    return new StopOrder(orderType,command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]));
                case "showbook":
                    return null;
                default:
                    throw new UnrecognizedOrderException("Ordine non disponibile");
            }
        }//potrei generare delle eccezioni specifiche per marketorder e cancelorder
        catch(UnrecognizedOrderException e){
            //gestire eccezione
            System.out.println("Ordine non gestito");
            return null;
        }
        catch (Exception e) {
            //if ()return new Values(null, null, 0, 0, 0, orderbook, new ShowOrderBook());
            System.out.println("[ORDERFACTORY] "+e.getClass() +" "+e.getCause());
            return null;
        }
    }
}