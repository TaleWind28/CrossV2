package Commands;


import Commands.Credentials.Login;
import Commands.Credentials.Logout;
import Commands.Credentials.Register;
import Commands.Credentials.UpdateCredentials;
import Commands.Internal.ErrorMessage;
import Commands.Internal.Help;
import Commands.Internal.getPriceHistory;
import Commands.Orders.CancelOrder;
import Commands.Orders.Limitorder;
import Commands.Orders.MarketOrder;
import Commands.Orders.ShowOrderBook;
import Commands.Orders.ShowStopOrder;
import Commands.Orders.StopOrder;
import Communication.Values;
import Utils.CustomExceptions.UnrecognizedOrderException;

public class CommandFactory{

    public Values createValue(String[] command) {
        try {
            ////Stampa di debug
            // for(String data:command){
            //     System.out.print("[CommandFactory] comando: "+data);
            // }
            //System.out.println();
            //sistemo il tipo di ordine per avere solo la parte significativa
            String valueType = command[0].toLowerCase();
            valueType = valueType.replace("insert", "");
            valueType = valueType.replace("order", "");
            //creo il comando in base al tipo di operazione
            switch (valueType) {    
                case "cancel":
                    return new CancelOrder(Integer.parseInt(command[1]),"unset");    

                case "market":   
                    return new MarketOrder(command[1],Integer.parseInt(command[2]));
                
                case "limit":
                    return new Limitorder(command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]));
                
                case "stop":
                    return new StopOrder(command[1],Integer.parseInt(command[2]),Integer.parseInt(command[3]));
                
                case "showbook":
                    return new ShowOrderBook();
                
                case "showstop":
                    return new ShowStopOrder();

                case "register":
                    return new Register(command[1],command[2]);

                case "login":
                    return new Login(command[1],command[2]);

                case "updatecredentials":
                    return new UpdateCredentials(command[1],command[2],command[3]);

                case "logout":
                    return new Logout("unset");
                
                case "getpricehistory":
                    return new getPriceHistory(command[1]);
                default:
                    throw new UnrecognizedOrderException("comando non gestito");        
            }
        }//potrei generare delle eccezioni specifiche per marketorder e cancelorder -> devo valutare se ho voglia
        catch(UnrecognizedOrderException e){
            return new Help("unset");
        }
        catch (Exception e) {
            System.out.println("[ORDERFACTORY] "+e.getClass() +" "+e.getCause());
            return new ErrorMessage("parametri non corretti, digitare aiuto per una lista di comandi");
        }
    }
}