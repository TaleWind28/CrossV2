package Users.Commands.CommandBehaviours;
import Communication.Message;
import JsonMemories.Orderbook;
import ServerTasks.GenericTask;
import Users.Commands.Order;
import Users.Commands.UserCommand;

public class MarketOrder implements CommandBehaviour {
    
    @Override
    public Message executeOrder(UserCommand cmd,GenericTask context){
        //controllo che l'utente sia autenticato
        if(context.onlineUser.equals(""))return new Message("[401]: Per effettuare ordini bisogna creare un account o accedervi",401);
        //ricreo l'ordine
        Order ord = (Order)cmd;
        //controllo che l'orderID sia un numero valido
        if(ord.getorderID() == -1)return new Message("[400]: ord non correttamente formato");
        //recupero l'orderbook
        Orderbook ordb = ord.getOrderbook();
        //preparo exchangetype e responsemessage per dopo
        String exchangetype = null;
        //ha senso preparare il responsemessege adesse perchè se compro compro tutto dal solito utente, il quale verrà aggiunto successivamente al messaggio
        String responseMessage = null;
        //preparo le stringhe per stampa e richiesta userbook
        switch (ord.getExchangeType()) {
            case "ask":
                exchangetype = "bid";
                responseMessage = "[200]: OrderCode[" + ord.getorderID()+"] n°"+ord.getSize()+" bitcoin venduti all'utente ";
                break;
            case "bid":
                exchangetype = "ask";
                responseMessage = "[200]: OrderCode[" + ord.getorderID()+"] n°"+ord.getSize()+" bitcoin comprati dall'utente ";
                break;
        }
        //cerco il miglior prezzo per la qtà di bitcoin che voglio comprare
        String orderbookEntry = ordb.getBestPriceAvailable(ord.getSize(), exchangetype,context.onlineUser);
        //controllo che esista una entry per il mio ordine
        if(orderbookEntry == null)return new Message("[404] Non sono stati trovati ordini per le tue esigenze",-1);
        //rimuovo l'ordine dall'orderbook
        Order evadedOrder = ordb.removeData(exchangetype,orderbookEntry);
        //controllo che l'ordine sia stato evaso
        if(evadedOrder == null)return new Message("[404] Non sono stati trovati ordini per le tue esigenze",-1);
        //System.out.println("taglia ordine utente:"+ord.getSize()+", taglia ordine mercato:"+evadedOrder.getSize());
        //controllo quanti btc sono stati comprati
        if(evadedOrder.getSize()>ord.getSize()){
            //sottraggo la taglia di bitcoin comprata
            evadedOrder.addSize(-(ord.getSize()));
            //rimetto l'offerta sul mercato
            ordb.addData(evadedOrder, exchangetype);
        }
        responseMessage+=evadedOrder.getUser()+" pagando "+(evadedOrder.getPrice()*ord.getSize())+"$";
        return new Message(responseMessage,200);
    }

    @Override
    public int getUnicode() {
        return 100;    
    }

}
