package ServerTasks;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import Communication.Messages.ServerMessage;
import Communication.Protocols.TCP;
import Executables.ServerMain;
import Utils.AnsiColors;

public class ClosingTask implements Runnable{
    private ServerMain generatorServer;
    public ClosingTask(ServerMain generatorServer){
        this.generatorServer = generatorServer;
    }
    public void run(){    
        System.out.println(AnsiColors.BRIGHT_RED+"[ClosingTask] Ctrl+C rilevato -> chiusura server in corso..."+AnsiColors.RESET);

        generatorServer.pool.shutdown();
        try {
            //Attende la terminazione dei thread attivi
            if (!generatorServer.pool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                System.out.println(AnsiColors.BRIGHT_RED+"[ClosingTask] Interruzione forzata dei thread attivi..."+AnsiColors.RESET);
                generatorServer.pool.shutdownNow();
                

            }
        } catch (InterruptedException e) {
            //Forza l'arresto in caso di interruzione
            generatorServer.pool.shutdownNow();
        }
        System.out.println(AnsiColors.BRIGHT_RED+"[ClosingTask] Threadpool terminato"+AnsiColors.RESET);
        generatorServer.getRegisteredUsers().getUserMap().forEach((username, user)-> user.setLogged(false));
        generatorServer.getRegisteredUsers().dataFlush();

        this.notifyActiveClients();

        System.out.println(AnsiColors.BRIGHT_RED+"[ClosingTask] Utenti correttamente sloggati"+AnsiColors.RESET);
        try {
            this.generatorServer.getServer().close();
        } catch (IOException e) {
            System.out.println(AnsiColors.BRIGHT_RED+"[ClosingTask] Unable to close serverSocket"+AnsiColors.RESET);
        }
        System.out.println(AnsiColors.BRIGHT_RED+"[ClosingTask] chiusura in sicurezza del server completata"+AnsiColors.RESET);
    }

    public void notifyActiveClients(){
        try{
            for(Socket client :this.generatorServer.getActiveClients()){
                TCP proto = new TCP();
                proto.setSender(client);
                proto.sendMessage(new ServerMessage("Stiamo Riscontrando dei problemi gravi per i quali non possiamo garantire il funzionamento del servizio, pertanto ci occuperemo della disconnessione",408));
                //client.close();
            }
        }catch(Exception e){
            System.out.println("[ClosingTask]"+e.getMessage() + " : "+e.getClass());
        }
    }
}
