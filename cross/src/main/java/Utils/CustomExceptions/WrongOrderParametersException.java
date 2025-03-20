package Utils.CustomExceptions;

public class WrongOrderParametersException extends Throwable{
    private String message;

    public WrongOrderParametersException(){
        this.message = "Tipo di scambio non corretto, sono accettati solo ask e bid";
    }

    public WrongOrderParametersException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }

}
