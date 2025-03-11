package Utils;

public class SystemMessage {
    private final String message;
    private final int code;
    
    public SystemMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getCode() {
        return code;
    }
}
