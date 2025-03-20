package Utils.CustomExceptions;

public class UnrecognizedOrderException extends Throwable{
    private String message;
    
    public UnrecognizedOrderException(){
        this.message = "comando non disponibile, digitare help per una lista di comandi disponibili";
    }

    public UnrecognizedOrderException(String message) {  
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }

}
