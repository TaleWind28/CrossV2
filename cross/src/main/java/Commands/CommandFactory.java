package Commands;


import java.util.zip.DataFormatException;

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
import Utils.AnsiColors;
import Utils.CustomExceptions.UnrecognizedOrderException;
import Utils.CustomExceptions.WrongOrderParametersException;

public class CommandFactory{

    public Values createValue(String[] command) {
        //controllare meglio i parametri
        //sistemo il tipo di ordine per avere solo la parte significativa
        String valueType = command[0].toLowerCase();
        
        try {
            // if(valueType.contains("order") && !valueType.contains("insert"))throw new UnrecognizedOrderException();
            System.out.println("[factory]valueType: "+valueType);
        
            //creo il comando in base al tipo di operazione
            switch (valueType) {    
                case "cancel":
                    return new CancelOrder(Integer.parseInt(command[2]),command[1]);    

                case "insertmarketorder":
                    if(!command[2].equals("ask") && !command[2].equals("bid"))throw new WrongOrderParametersException();
                    return new MarketOrder(command[1],command[2],Integer.parseInt(command[3]));
                
                case "insertlimitorder":
                    if(!command[2].equals("ask") && !command[2].equals("bid"))throw new WrongOrderParametersException();
                    return new Limitorder(command[1],command[2],Integer.parseInt(command[3]),Integer.parseInt(command[4]));
                
                case "insertstoporder":
                    if(!command[2].equals("ask") && !command[2].equals("bid"))throw new WrongOrderParametersException();    
                    return new StopOrder(command[1],command[2],Integer.parseInt(command[3]),Integer.parseInt(command[4]));
                
                case "showorderbook":
                    return new ShowOrderBook();
                
                case "showstoporder":
                    return new ShowStopOrder();

                case "register":
                    return new Register(command[2],command[3]);

                case "login":
                    return new Login(command[2],command[3]);

                case "updatecredentials":
                    return new UpdateCredentials(command[2],command[3],command[4]);

                case "logout":
                    return new Logout("unset");
                
                case "help":
                    return new Help("aiutami");
                
                case "getpricehistory":
                    return new getPriceHistory(command[2]);
                default:
                    throw new UnrecognizedOrderException("comando non gestito");        
            }
        }
        catch(UnrecognizedOrderException e){
            return new ErrorMessage(e.getMessage());
        }
        catch(WrongOrderParametersException e){
            return new ErrorMessage(e.getMessage());

        }
        catch(NumberFormatException e){
            if(valueType.equals("cancelorder"))return new ErrorMessage("Devi inserire l'order id dell'ordine che intendi cancellare");
            return new ErrorMessage("Sintassi sbagliata, devi inserire un numero subito dopo il tipo(ask/bid) di ordine che intendi piazzare");
        }
        catch(DataFormatException e){
            return new ErrorMessage(e.getMessage());
        }
        catch (Exception e) {
            System.out.println("[ORDERFACTORY] "+e.getClass() +" "+e.getMessage());
            return new ErrorMessage(AnsiColors.RED+"parametri non corretti, digitare aiuto per una lista di comandi"+AnsiColors.RESET);
        }
    }
}