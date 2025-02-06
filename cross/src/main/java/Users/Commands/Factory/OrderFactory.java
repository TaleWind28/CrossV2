package Users.Commands.Factory;

import JsonMemories.JsonAccessedData;
import JsonMemories.Orderbook;
import Users.Commands.Order;
import Users.Commands.UserCommand;
import Users.Commands.CommandBehaviours.CancelOrder;
import Users.Commands.CommandBehaviours.LimitOrder;
import Users.Commands.CommandBehaviours.MarketOrder;
import Users.Commands.CommandBehaviours.ShowOrderBook;
import Users.Commands.CommandBehaviours.StopOrder;
import Utils.CustomExceptions.UnrecognizedOrderException;

public class OrderFactory implements UserCommandFactory{
    private int orderNumber = 0;
    private Orderbook orderbook;

    @Override
    public UserCommand createUserCommand(String[] command) {
        try {
            //sistemo il tipo di ordine per avere solo la parte significativa
            String orderType = command[0].toLowerCase().replace("insert", "").replace("order", "");
            System.out.println(orderType);
            //aumento l'order ID che DEVE essere unico
            orderNumber++;
            switch (orderType) {    
                case "cancel":
                    return new Order(orderType,"none",-1,-1,Integer.parseInt(command[1]),orderbook,new CancelOrder());    
                    //break;
                case "market":   
                    return new Order(orderType,command[1],Integer.parseInt(command[2]),0,orderNumber,orderbook,new MarketOrder());
                case "limit":
                    return new Order(orderType,command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]),orderNumber,orderbook,new LimitOrder());
                case "stop":
                    return new Order(orderType,command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]),orderNumber,orderbook,new StopOrder());
                case "showbook":
                    return new Order(orderType, null, 0, 0, 0, orderbook, new ShowOrderBook()); 
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
            //if ()return new Order(null, null, 0, 0, 0, orderbook, new ShowOrderBook());
            System.out.println("[ORDERFACTORY] "+e.getClass() +" "+e.getCause());
            return null;
        }
    }

    @Override
    public void setJsonDataStructure(JsonAccessedData data) {
        this.orderbook = (Orderbook)data;
        return;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return;
    }

    @Override
    public void additionalInfo(String otherinfo) {
        setOrderNumber(Integer.parseInt(otherinfo));    
        return;
    }
}
