package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Users.User;
import JsonAccessedData.Users.Userbook;
import Server.ServerTasks.GenericTask;

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
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task)throws ClassCastException{
        Userbook userbook = (Userbook)data;
        if (!task.onlineUser.equals(""))return new ServerMessage("Non puoi cambiare le credenziali mentre sei loggato",104);
        System.out.println("[UpdateCredentials]"+this.toString());
        //controllare che le due password non coincidano
        if(this.password.equals(this.newPassword))return new ServerMessage("La nuova password DEVE essere diversa da quella precedente",103);
        //controllare che username e password corrispondano
        User usr = new User(this.username,this.password);
        //
        int retvalue = userbook.checkCredentials(usr);
        if(retvalue == 400)return new ServerMessage("Password non valida",101);
        else if(retvalue == 404)return new ServerMessage("Utente non registrato",102);
        //si -> sostituire password esistente con nuova password
        if(userbook.getUserMap().get(this.username).getLogged()==true)return new ServerMessage("Non puoi cambiare la password di un utente loggato",104);
        //aggiorno l'userbook
        userbook.updateData(usr, this.newPassword);
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

    @Override
    public String toString() {
        return "UpdateCredentials{username='"+this.username+"', password='"+this.password+"', newpassword='"+this.newPassword+"'}";
    }
}
