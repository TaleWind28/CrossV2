package Utils.CustomExceptions;

public class UnrecognizedOrderException extends Throwable{
    private String message;
    public UnrecognizedOrderException(String message) {  
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }

}
