package Commands.Credentials;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import Users.User;

public class Register implements Values{
    private String username;
    private String password;

    @Override
    public ServerMessage execute(JsonAccessedData data) {
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
    
}
