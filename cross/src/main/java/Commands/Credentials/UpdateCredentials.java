package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.User;
import JsonUtils.Userbook;
import ServerTasks.GenericTask;

public class UpdateCredentials implements Values{
    private String username;
    private String password;
    private String newPassword;

    public UpdateCredentials(String username, String password, String newPassword){
        this.username = username;
        this.password = password;
        this.newPassword = newPassword;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        Userbook userbook = (Userbook)data;
        //Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        if(!this.username.equals(user))return new ServerMessage("[401]: Autorizzazione richiesta!",401);
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

    @Override
    public void setUsername(String user) {
        this.username = user;
    }

    @Override
    public String getUsername() {
        return this.username;    
    }
}
