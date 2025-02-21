package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Users.User;
import JsonUtils.Users.Userbook;
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
        System.out.println("[UpdateCredentials]Utente:"+user);
        //if(!this.username.equals(user))return new ServerMessage("Autorizzazione richiesta!",105);
        //controllare che le due password non coincidano
        if(this.password.equals(this.newPassword))return new ServerMessage("La nuova password DEVE essere diversa da quella precedente",103);
        //controllare che username e password corrispondano
        int retvalue = userbook.checkCredentials(new User(this.username,this.password));
        if(retvalue == 400)return new ServerMessage("Password non valida",101);
        else if(retvalue == 404)return new ServerMessage("Utente non registrato",102);
        //si -> sostituire password esistente con nuova password
        if(userbook.getUserMap().get(user).getLogged()==true)return new ServerMessage("Non puoi cambiare la password mentre sei loggato",104);
        User usr = new User(this.username,this.password);
        //usr.setLogged(false);
        //context.onlineUser = "";
        userbook.updateData(usr, this.newPassword);
        //userbook.getUserMap().get(this.username).setPassword(this.newPassword);
        userbook.dataFlush();
        return new ServerMessage("Credenziali aggiornate con successo", 100);
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
