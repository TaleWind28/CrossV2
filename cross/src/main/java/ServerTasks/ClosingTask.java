package ServerTasks;

import java.util.concurrent.TimeUnit;

import Executables.ServerMain;

public class ClosingTask implements Runnable{
    private ServerMain generatorServer;
    public ClosingTask(ServerMain generatorServer){
        this.generatorServer = generatorServer;
    }
    public void run(){    
        System.out.println("[ClosingTask] Ctrl+C rilevato -> chiusura server in corso...");
        //Arresta il thread pool
        generatorServer.pool.shutdown();
        try {
                //Attende la terminazione dei thread attivi
            if (!generatorServer.pool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                System.out.println("[ClosingTask] Interruzione forzata dei thread attivi...");
                generatorServer.pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            //Forza l'arresto in caso di interruzione
            generatorServer.pool.shutdownNow();
        }
        System.out.println("[ClosingTask] Threadpool terminato");
        generatorServer.getRegisteredUsers().getUserMap().forEach((username, user)-> user.setLogged(false));
        generatorServer.getRegisteredUsers().dataFlush();
        System.out.println("[ClosingTask] Utenti correttamente sloggati");
    }
}
