package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.User;
import JsonUtils.Userbook;
import ServerTasks.GenericTask;

public class Login implements Values{
    private String username;
    private String password;

    public Login(String username, String password){
        this.password = password;
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        Userbook userbook = (Userbook)data;
        System.out.println("[Login]"+this.toString());
        // if(userbook.getUserMap().containsKey(this.onlineUser))
        User userClass = new User(this.username, this.password);
        if(!user.equals(""))return new ServerMessage("[400]: Login effettuato in precedenza con nome utente: "+user,400);
        if(!userbook.getUserMap().containsKey(this.username))return new ServerMessage("[404]: Utente non registrato",404);
        if(userbook.getUserMap().get(this.username).getLogged() == true)return new ServerMessage("[400]: Utente già loggato",400);
        //invoco checkCredentials e memorizzo il codice di ritorno
        int retcode = userbook.checkCredentials(userClass);
        //discerno i codici di ritorno
        if(retcode == 400)return new ServerMessage("[400]: Password non valida",400);
        else if(retcode == 404)return new ServerMessage("[404]: Utente non registrato",404);
        //se il codice di ritorno non è uno dei codici di errore allora username e password sono corretti
        //loggo l'utente
        //context.onlineUser = userClass.getUsername();
        userClass.setLogged(true);
        //aggiorno lo status sulla mappa
        userbook.getUserMap().get(this.username).setLogged(true);
        //aggiorno il jsonOriginale
        userbook.dataFlush();
        return new ServerMessage("[200]: Utente correttamente loggato col nome: "+this.username,200);
    }
    @Override
    public String getUsername() {
        return this.username;    
    }
    @Override
    public String toString() {
        return "Login{" +
               "username='" + username + '\'' +
               ", password='" + password + '\'' +
               '}';
    }

    @Override
    public void setUsername(String user) {
        //this.username = user;    
    }
    
}   
