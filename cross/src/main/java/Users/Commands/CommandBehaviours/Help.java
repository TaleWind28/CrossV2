package Users.Commands.CommandBehaviours;

import Communication.Message;
import ServerTasks.GenericTask;
import Users.Commands.UserCommand;

//public String helpMessage = "updateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\nlogout<username> -> permette di uscire dal servizio di trading";
    
public class Help implements CommandBehaviour{
    String helpMessage;
    @Override
    public Message executeOrder(UserCommand cmd, GenericTask context) {
        String nonLoggedUserMessage = "Comandi:\n" + 
                        "register<username,password> -> ti permette di registrarti per poter accedere al servizio di trading\n" + 
                        "login<username,password> -> permette di accedere ad un account registrato\n" +
                        "";
        String loggedUserMessage = "Comandi:\n"+
                        "updateCredentials<username,currentPasswd,newPasswd> -> permette di aggiornare le credenziali\n"+
                        "logout<username> -> permette di uscire dal servizio di trading\n"+
                        "showorderbook -> fa visualizzare l'orderbook\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> -> inserisce un marketorder\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> <limitprice> -> inserisce un limitorder\n"+
                        "insertmarketorder <ask/bid> <qtà di bitcoin da vendere/comprare> <stopprice> -> inserisce uno stoporder\n"+
                        "cancelorder <orderID>\n";

        if (!context.onlineUser.equals(""))setHelpMessage(loggedUserMessage);
        else setHelpMessage(nonLoggedUserMessage);
        
        return new Message(this.helpMessage,200);
    }
    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    @Override
    public int getUnicode() {
        return 121;
    }

}
