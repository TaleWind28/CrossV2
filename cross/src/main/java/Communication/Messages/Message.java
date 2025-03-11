package Communication.Messages;

import Utils.AnsiColors;

public interface Message {
    @Override
    String toString();
    String getMessageColor();
    void setMessageColor(String color);

}
