package Users.Commands.CommandBehaviours;
import Communication.Message;
import JsonMemories.Orderbook;
import ServerTasks.GenericTask;
import Users.Commands.Order;
import Users.Commands.UserCommand;

public class LimitOrder implements CommandBehaviour{
    public Message executeOrder(UserCommand cmd,GenericTask context){
        if(context.onlineUser.equals(""))return new Message("401: Per effettuare ordini bisogna creare un account o accedervi",401);
        System.out.println("limitorder: "+cmd.getInfo());
        Order ord = (Order)cmd;
        Orderbook orderbook = ord.getOrderbook();
        //la faccio semplice per vedere se funziona
        //non so come funziona l'algoritmo richiesto dalla ricci quindi lo lascio così
        orderbook.addData(ord, ord.getExchangeType());
        //System.out.println("fatto");
        return new Message("[200] Limitorder Correttamente inserito, OrderCode["+ord.getorderID()+"]",200);
    }

    @Override
    public int getUnicode() {
        return 102;    
    }
}
