package Communication.Protocols;

import java.util.Scanner;

public abstract class ClientProtocol{
    public int port;
    public String ip;
    public Scanner userInput;
    public Thread receiverThread;
    public TCP protocol;


    public ClientProtocol(String IP,int PORT){
        this.port = PORT;
        this.ip = IP;
        this.userInput = new Scanner(System.in);
    }

    public void setReceiverThread(){
        this.receiverThread = new Thread(){public void run(){receiveBehaviour();}};
    }

    public abstract void multiDial();

    public abstract void sendBehaviour();

    public abstract void receiveBehaviour();

    public void setProtocol(TCP protocol){
        this.protocol = protocol;
    }

}
