package Users.Commands.CommandBehaviours;

import Communication.Message;
import JsonMemories.Userbook;
import ServerTasks.GenericTask;
import Users.User;
import Users.Commands.UserCommand;

public class UpdateCredentials implements CommandBehaviour{

    @Override
    public Message executeOrder(UserCommand cmd,GenericTask context) {
        String[] credentialsInfo = cmd.getInfo();
        Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        if(!context.onlineUser.equals(cmd.getInfo()[1]))return new Message("[401]: Autorizzazione richiesta!",401);
        //controllare che le due password non coincidano
        if(credentialsInfo[2].equals(credentialsInfo[3]))return new Message("[400]: La nuova password DEVE essere diversa da quella precedente");
        //controllare che username e password corrispondano
        int retvalue = userbook.checkCredentials(new User(credentialsInfo[1],credentialsInfo[2]));
        if(retvalue == 400)return new Message("[400]: Password non valida",400);
        else if(retvalue == 404)return new Message("[404]: Utente non registrato",404);
        //si -> sostituire password esistente con nuova password
        User usr = new User(credentialsInfo[1],credentialsInfo[2]);
        usr.setLogged(false);
        context.onlineUser = "";
        userbook.updateData(usr, credentialsInfo[3]);
        //userbook.getUserMap().get(credentialsInfo[1]).setPassword(credentialsInfo[3]);
        userbook.dataFlush();
        return new Message("[200]: Credenziali aggiornate, esegui nuovamente l'accesso", 200);
    }
    @Override
    public int getUnicode() {
        return 107;    
    }
}

