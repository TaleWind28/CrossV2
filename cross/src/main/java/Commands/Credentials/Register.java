package Commands.Credentials;

import Commands.Values;
import Communication.Messages.ServerMessage;
import JsonAccessedData.JsonAccessedData;
import JsonAccessedData.Users.User;
import JsonAccessedData.Users.Userbook;
import Server.ServerTasks.GenericTask;
import Utils.AnsiColors;

public class Register implements Values{
    private String username;
    private String password;

    public Register(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public ServerMessage execute(JsonAccessedData data,String user,GenericTask task) throws ClassCastException{
        Userbook userbook = (Userbook)data;
        //controllo che l'utente non sia già loggato
        if(!task.onlineUser.equals(""))return new ServerMessage("Non puoi registrarti mentre sei loggato",103);
        //controllare che username non esista già
        if(userbook.accessData(username) == 200)return new ServerMessage(" Utente già presente nel database",102);
        //memorizzare username e password
        userbook.addData(new User(username, password));
        return new ServerMessage(AnsiColors.GREEN_MEDIUM+"Utente correttamente registrato col nome "+username+AnsiColors.RESET ,100);
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
