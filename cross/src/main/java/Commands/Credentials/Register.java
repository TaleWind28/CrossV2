package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Users.User;
import JsonUtils.Users.Userbook;
import ServerTasks.GenericTask;

public class Register implements Values{
    private String username;
    private String password;

    public Register(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task) {
        System.out.println("Primo Controllo");
        Userbook userbook = (Userbook)data;
        System.out.println("User: "+user+"\tUsername: "+this.username);
        //controllare che username non esista già
        if(userbook.accessData(username) == 200)return new ServerMessage(" Utente già presente nel database",102);//sostituire con eccezzione
        //System.out.println("controllo dati utente esistente superato");
        //memorizzare username e password
        userbook.addData(new User(username, password));
        //System.out.println("entro");
        return new ServerMessage("[101]: Utente correttamente registrato col nome "+username,100);
    }

    @Override
    public String toString() {
        return"Register{username="+this.getUsername()+",password="+this.getPassword()+"}";
    }

    public String getPassword() {
        return password;
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
