package Users.Commands.Factory;

import JsonMemories.JsonAccessedData;
import JsonMemories.Userbook;
import Users.Commands.Credentials;
import Users.Commands.UserCommand;
import Users.Commands.CommandBehaviours.Disconnect;
import Users.Commands.CommandBehaviours.Login;
import Users.Commands.CommandBehaviours.Logout;
import Users.Commands.CommandBehaviours.Register;
import Users.Commands.CommandBehaviours.UpdateCredentials;

public class CredentialsFactory implements UserCommandFactory{
    private Userbook userbook;
    public void setJsonDataStructure(JsonAccessedData userbook) {
        this.userbook = (Userbook)userbook;
    }

    @Override
    public UserCommand createUserCommand(String[] command) throws ArrayIndexOutOfBoundsException{
        String type = command[0].toLowerCase();
        System.out.println(type);
        if(command.length<2 && type.toLowerCase().equals("exit"))return new Credentials(type, "",userbook ,new Disconnect());
        try {
            String username = command[1];   
            String password = null;
            String newPassword = null;
            switch (type) {
                case "logout":
                    return new Credentials(type,username,userbook,new Logout());
                case "register":
                    password = command[2];
                    System.out.println("Comando Register:\nusername: "+username+", password: "+password);
                    Credentials cred = new Credentials(type,username,password,userbook,new Register());
                    return cred;
                case "login":
                    password = command[2];
                    //System.out.println("passwd: "+password);
                    return new Credentials(type,username,password,userbook,new Login());
                case "updatecredentials":
                    password = command[2];
                    newPassword = command[3];
                    System.out.println("password:"+password+",newpassword: "+newPassword);
                    return new Credentials(type,username,password,newPassword,userbook,new UpdateCredentials());
            }
            return null;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("[CredentialsFactory] out of bounds");
            return null;
        }
    }

    @Override
    public void additionalInfo(String otherinfo) {
        return;    
    }
    
}
