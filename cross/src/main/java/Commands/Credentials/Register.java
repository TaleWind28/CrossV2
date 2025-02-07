package Commands.Credentials;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import Users.User;

public class Register implements Values{
    private String username;
    private String password;

    public Register(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user) {
        System.out.println("Primo Controllo");
        Userbook userbook = (Userbook)data;
        //controllare che username non esista già
        if(userbook.accessData(username) == 200)return new ServerMessage("[400]: Utente già presente nel database",400);//sostituire con eccezzione
        //System.out.println("controllo dati utente esistente superato");
        
        //memorizzare username e password
        userbook.addData(new User(username, password));
        //System.out.println("entro");
        return new ServerMessage("[101]: Utente correttamente registrato col nome "+username,101);
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
