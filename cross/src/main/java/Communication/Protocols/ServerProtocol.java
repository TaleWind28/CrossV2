package Communication.Protocols;

import java.util.concurrent.*;
import java.util.*;
import java.net.Socket;

public abstract class ServerProtocol {
    public int PORT;
    public ExecutorService pool;
    private List<Socket> activeClients;
    public Protocol protocol;

    public ServerProtocol(int port, int numThreads){
        this.PORT = port;
        this.pool = Executors.newFixedThreadPool(numThreads);
        this.activeClients = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void onClientDisconnect(Socket client, String message){
        System.out.println(message);
        this.activeClients.remove(client);
        try{
            client.close();
        }            
        catch (Exception e) {
            System.out.println(e.getClass()+": "+e.getStackTrace());   
        }
    }

    public synchronized void addClient(Socket clientSocket){
        activeClients.add(clientSocket);
        System.out.println("[ServerProtocol]Client connesso. Client attivi: " + activeClients.size());
    }

    public List<Socket> getActiveClients() {
        return activeClients;
    }

    public void setProtocol(Protocol protocol){
        this.protocol = protocol;
    }

    //apre il socket e passa al threadpool i vari client
    public abstract void dial();
}
