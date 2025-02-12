package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonUtils.JsonAccessedData;
import JsonUtils.Userbook;
import ServerTasks.GenericTask;

public class Logout implements Values{
    private String username;
    public Logout(String username){
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        //codice duplicato -> cambiare -> mettere nel costruttore / chiamata di funzione per inizializzare
        //String[] credentialsInfo = cmd.getInfo();
        Userbook userbook = (Userbook)data;
        setUsername(user);
        //controllo che esista l'utente
        //if(!context.onlineUser.equals(this.username))return new Message("[401]: Non possiedi le autorizzazioni necessarie",401);
        if(userbook.accessData(this.username) == 404)return new ServerMessage("[404]: Utente non registrato",404);
        //controllo che l'utente sia effettivamente loggato
        if(userbook.getUserMap().get(this.username).getLogged() == false)return new ServerMessage("[400]: Utente non attualmente loggato",400);
        //ulteriore controllo su chi sta chiedendo il logout
        //sloggare
        //context.onlineUser = "";
        userbook.getUserMap().get(this.username).setLogged(false);
        userbook.dataFlush();
        return new ServerMessage("[200]: Disconnessione avenuta con successo!",408);
    }
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Logout{username= "+this.username+"}";
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
