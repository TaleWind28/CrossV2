package Commands.Credentials;

import Communication.ServerMessage;
import Communication.Values;
import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;

public class Logout implements Values{
    private String username;
    public Logout(String username){
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data){
        //codice duplicato -> cambiare -> mettere nel costruttore / chiamata di funzione per inizializzare
        //String[] credentialsInfo = cmd.getInfo();
        Userbook userbook = (Userbook)data;
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
}
