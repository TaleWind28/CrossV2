package Commands.Credentials;

import Communication.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Users.Userbook;
import ServerTasks.GenericTask;
import Utils.AnsiColors;

public class Logout implements Values{
    private String username;
    public Logout(String username){
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task){
        Userbook userbook = (Userbook)data;
        //controllo che esista l'utente
        if(userbook.accessData(user) == 404)return new ServerMessage("Utente non registrato",101);
        //controllo che l'utente sia effettivamente loggato
        if(userbook.getUserMap().get(user).getLogged() == false)return new ServerMessage("Utente non attualmente loggato",101);
        //sloggare
        userbook.getUserMap().get(user).setLogged(false);
        userbook.dataFlush();
        return new ServerMessage(AnsiColors.GREEN_LIGHT+"Disconnessione avvenuta con successo!"+ AnsiColors.RESET ,100);
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
