package Commands.Credentials;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import Users.User;

public class UpdateCredentials implements Values{
    private String username;
    private String password;
    private String newpassword;

    @Override
    public ServerMessage execute(JsonAccessedData data){
        Userbook userbook = (Userbook)data;
        //Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        //if(!context.onlineUser.equals(cmd.getInfo()[1]))return new ServerMessage("[401]: Autorizzazione richiesta!",401);
        //controllare che le due password non coincidano
        if(this.password.equals(this.newpassword))return new ServerMessage("[400]: La nuova password DEVE essere diversa da quella precedente",400);
        //controllare che username e password corrispondano
        int retvalue = userbook.checkCredentials(new User(this.username,this.password));
        if(retvalue == 400)return new ServerMessage("[400]: Password non valida",400);
        else if(retvalue == 404)return new ServerMessage("[404]: Utente non registrato",404);
        //si -> sostituire password esistente con nuova password
        User usr = new User(this.username,this.password);
        usr.setLogged(false);
        //context.onlineUser = "";
        userbook.updateData(usr, this.newpassword);
        //userbook.getUserMap().get(this.username).setPassword(this.newpassword);
        userbook.dataFlush();
        return new ServerMessage("[200]: Credenziali aggiornate, esegui nuovamente l'accesso", 200);
    }
}
