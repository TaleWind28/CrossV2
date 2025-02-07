package Commands;


import Commands.Credentials.Login;
import Commands.Credentials.Logout;
import Commands.Credentials.Register;
import Commands.Credentials.UpdateCredentials;
import Commands.Orders.CancelOrder;
import Commands.Orders.Limitorder;
import Commands.Orders.MarketOrder;
import Commands.Orders.ShowOrderBook;
import Commands.Orders.StopOrder;
import Utils.CustomExceptions.UnrecognizedOrderException;

public class CommandFactory{

    public Values createValue(String[] command) {
        try {
            //sistemo il tipo di ordine per avere solo la parte significativa
            System.out.println("[ORDERFACTORY] prima stampa command[0] "+command[0]);
            String valueType = command[0].toLowerCase();
            valueType = valueType.replace("insert", "");
            valueType = valueType.replace("order", "");
            // System.out.println("[ORDERFACTORY]"+command[0]+command[1]);
            System.out.println("[ORDERFACTORY]"+valueType);
            /*RIARRANGIARE LA FACTORY */
            switch (valueType) {    
                case "cancel":
                    return new CancelOrder(Integer.parseInt(command[1]),"unset");    
                    //break;
                case "market":   
                    return new MarketOrder(command[1],Integer.parseInt(command[2]));
                case "limit":
                    System.out.println("limit"+command[1]+command[2]+command[3]);
                    return new Limitorder(command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]));
                case "stop":
                    return new StopOrder(command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]));
                case "showbook":
                    return new ShowOrderBook();
                
                case "register":
                    return new Register(command[1],command[2]);

                case "login":
                    return new Login(command[1],command[2]);

                case "updatecredentials":
                    return new UpdateCredentials(command[1],command[2],command[3]);

                case "logout":
                    return new Logout("unset");
                
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
            return new ErrorMessage("parametri non corretti, digitare aiuto per una lista di comandi");
        }
    }
}