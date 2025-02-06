package Users.Commands.CommandBehaviours;

import Communication.Message;
import JsonMemories.Userbook;
import ServerTasks.GenericTask;
import Users.User;
import Users.Commands.UserCommand;

public class Login implements CommandBehaviour {

    @Override
    public Message executeOrder(UserCommand cmd,GenericTask context) {
        String[] credentialsInfo = cmd.getInfo();
        //System.out.println("aaa");
        Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        // if(userbook.getUserMap().containsKey(this.onlineUser))
        User user = new User(credentialsInfo[1], credentialsInfo[2]);
        if(!context.onlineUser.equals(""))return new Message("[400]: Login effettuato in precedenza con nome utente: "+context.onlineUser,400);
        if(!userbook.getUserMap().containsKey(credentialsInfo[1]))return new Message("[404]: Utente non registrato",404);
        if(userbook.getUserMap().get(credentialsInfo[1]).getLogged() == true)return new Message("[400]: Utente già loggato",400);
        //invoco checkCredentials e memorizzo il codice di ritorno
        int retcode = userbook.checkCredentials(user);
        //discerno i codici di ritorno
        if(retcode == 400)return new Message("[400]: Password non valida",400);
        else if(retcode == 404)return new Message("[404]: Utente non registrato",404);
        //se il codice di ritorno non è uno dei codici di errore allora username e password sono corretti
        //loggo l'utente
        context.onlineUser = user.getUsername();
        user.setLogged(true);
        //aggiorno lo status sulla mappa
        userbook.getUserMap().get(credentialsInfo[1]).setLogged(true);
        //aggiorno il jsonOriginale
        userbook.dataFlush();
        return new Message("[200]: Utente correttamente loggato col nome: "+credentialsInfo[1],200);

    }
    @Override
    public int getUnicode() {
        return 105;    
    }


}
