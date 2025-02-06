package Users.Commands.CommandBehaviours;

import Communication.Message;
import JsonMemories.Userbook;
import ServerTasks.GenericTask;
import Users.Commands.UserCommand;

public class Disconnect implements CommandBehaviour{

    @Override
    public Message executeOrder(UserCommand cmd, GenericTask context) {
       //codice duplicato -> cambiare -> mettere nel costruttore / chiamata di funzione per inizializzare
        Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        if (context.onlineUser.equals(""))return new Message("408: Utente correttamente disconnesso dal server",408);
        //controllo che esista l'utente
        if(userbook.accessData(context.onlineUser) == 404)return new Message("404: Utente non registrato",404);
        //controllo che l'utente sia effettivamente loggato
        if(userbook.getUserMap().get(context.onlineUser).getLogged() == false)return new Message("400: Utente non attualmente loggato",400);
        //ulteriore controllo su chi sta chiedendo il logout
        //sloggare
        userbook.getUserMap().get(context.onlineUser).setLogged(false);
        userbook.dataFlush();
        return new Message("408: Utente correttamente disconnesso: "+context.onlineUser,408);
    }

    @Override
    public int getUnicode() {
        return 109;
    }

}
