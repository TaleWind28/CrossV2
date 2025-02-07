package Commands.Credentials;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import Users.User;

public class UpdateCredentials extends Values{
    private String username;
    private String password;
    private String newPassword;

    public UpdateCredentials(String username, String password, String newPassword){
        this.username = username;
        this.password = password;
        this.newPassword = newPassword;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data){
        Userbook userbook = (Userbook)data;
        //Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        //if(!context.onlineUser.equals(cmd.getInfo()[1]))return new ServerMessage("[401]: Autorizzazione richiesta!",401);
        //controllare che le due password non coincidano
        if(this.password.equals(this.newPassword))return new ServerMessage("[400]: La nuova password DEVE essere diversa da quella precedente",400);
        //controllare che username e password corrispondano
        int retvalue = userbook.checkCredentials(new User(this.username,this.password));
        if(retvalue == 400)return new ServerMessage("[400]: Password non valida",400);
        else if(retvalue == 404)return new ServerMessage("[404]: Utente non registrato",404);
        //si -> sostituire password esistente con nuova password
        User usr = new User(this.username,this.password);
        usr.setLogged(false);
        //context.onlineUser = "";
        userbook.updateData(usr, this.newPassword);
        //userbook.getUserMap().get(this.username).setPassword(this.newPassword);
        userbook.dataFlush();
        return new ServerMessage("[200]: Credenziali aggiornate, esegui nuovamente l'accesso", 200);
    }
}
