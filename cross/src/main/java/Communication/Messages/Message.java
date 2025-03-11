package Communication.Messages;

public interface Message {
    @Override
    String toString();
    String getMessageColor();
    void setMessageColor(String color);

}
