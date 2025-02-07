package Commands.Credentials;

import Commands.Values;
import Communication.ServerMessage;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import Users.User;

public class Login extends Values{
    private String username;
    private String password;

    public Login(String username, String password){
        this.password = password;
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data){
        Userbook userbook = (Userbook)data;
        // if(userbook.getUserMap().containsKey(this.onlineUser))
        User user = new User(this.username, this.password);
        //if(!context.onlineUser.equals(""))return new ServerMessage("[400]: Login effettuato in precedenza con nome utente: "+context.onlineUser,400);
        if(!userbook.getUserMap().containsKey(this.username))return new ServerMessage("[404]: Utente non registrato",404);
        if(userbook.getUserMap().get(this.username).getLogged() == true)return new ServerMessage("[400]: Utente già loggato",400);
        //invoco checkCredentials e memorizzo il codice di ritorno
        int retcode = userbook.checkCredentials(user);
        //discerno i codici di ritorno
        if(retcode == 400)return new ServerMessage("[400]: Password non valida",400);
        else if(retcode == 404)return new ServerMessage("[404]: Utente non registrato",404);
        //se il codice di ritorno non è uno dei codici di errore allora username e password sono corretti
        //loggo l'utente
        //context.onlineUser = user.getUsername();
        user.setLogged(true);
        //aggiorno lo status sulla mappa
        userbook.getUserMap().get(this.username).setLogged(true);
        //aggiorno il jsonOriginale
        userbook.dataFlush();
        return new ServerMessage("[200]: Utente correttamente loggato col nome: "+this.username,200);
    }

    @Override
    public String toString() {
        return "Login{" +
               "username='" + username + '\'' +
               ", password='" + password + '\'' +
               '}';
    }
    
}   
