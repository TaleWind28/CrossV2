package Commands.Credentials;

import Commands.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Users.Userbook;
import Server.ServerTasks.GenericTask;

public class Disconnect implements Values{
    private String username;

    public Disconnect(String username){
        this.username = username;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data, String user,GenericTask task) throws ClassCastException{
        if(this.username.equals("volontaria") && !task.onlineUser.equals(""))return new ServerMessage("Non puoi usare questo comando dopo aver effettuato il login, utilizza logout",101);
        //se l'utente non è loggato posso semplicemente chiudere la connessione
        if(task.onlineUser.equals(""))return new ServerMessage("Disconnessione avvenuta con successo",100);    
        //altrimenti devo recuperare la entry dell'userbook corrispondente e mettere logged a false
        Userbook userbook = (Userbook)data;
        userbook.getUserMap().get(task.onlineUser).setLogged(false);
        //faccio un flush dei dati per aggiornarli
        userbook.dataFlush();
        //ritorno il successo dell'operazione
        return new ServerMessage("[Server]Disconnessione avvenuta con successo", 100);
    }

    @Override
    public void setUsername(String user) {
        return;    
    }

    @Override
    public String getUsername() {
        return this.username;    
    }
}
