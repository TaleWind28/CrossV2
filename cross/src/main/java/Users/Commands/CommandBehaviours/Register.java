package Users.Commands.CommandBehaviours;

import Communication.Message;
import JsonMemories.Userbook;
import ServerTasks.GenericTask;
import Users.User;
import Users.Commands.UserCommand;

public class Register implements CommandBehaviour{
    //private Userbook userbook;
    
    @Override
    public Message executeOrder(UserCommand cmd,GenericTask context) {
        System.out.println("Primo Controllo");
        String[] credentialsInfo = cmd.getInfo();
        Userbook userbook = (Userbook)cmd.getJsonAccessedData();
        //controllare che username non esista già
        if(userbook.accessData(credentialsInfo[1]) == 200)return new Message("[400]: Utente già presente nel database",400);//sostituire con eccezzione
        //System.out.println("controllo dati utente esistente superato");
        
        //memorizzare username e password
        userbook.addData(new User(credentialsInfo[1], credentialsInfo[2]));
        //System.out.println("entro");
        return new Message("[200]: Utente correttamente registrato col nome "+credentialsInfo[1],201);
    }
    @Override
    public int getUnicode() {
        return 104;    
    }
}
