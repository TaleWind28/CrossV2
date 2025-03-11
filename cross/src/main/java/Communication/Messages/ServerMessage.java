package Communication.Messages;

import Utils.AnsiColors;

public class ServerMessage implements Message{
    public String errorMessage;
    public int response;
    private String color;
   // private String color;

    public ServerMessage(){

    }
    
    public ServerMessage(String message, int response){
        this.errorMessage = message;
        this.response = response;
        this.setMessageColor(AnsiColors.GREEN_DARK);
    }

    public String toString(){
        return this.color+"response: "+this.response+",errorMessage:\n"+this.errorMessage.toString()+AnsiColors.RESET;
    }

    @Override
    public void setMessageColor(String color) {
        this.color = color;    
    }

    @Override
    public String getMessageColor() {
        return this.color;
    }
}
