package Executables;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import Config.ServerConfig;
import Server.ServerClass;
import Server.ServerTasks.ClosingTask;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        ServerConfig configuration = getServerConfig();
        ServerClass server = new ServerClass(configuration);
        //Aggiungi uno shutdown hook alla JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new ClosingTask(server)));
        server.initialConfig();
        server.dial();
        return;
    }

    public static ServerConfig getServerConfig(){
        try{
            JsonAdapter<ServerConfig> jsonAdapter = new Moshi.Builder().build().adapter(ServerConfig.class);
            // Carica il file dalle risorse
            InputStream inputStream = ServerClass.class.getClassLoader().getResourceAsStream("ServerConfig.json");
            //controllo di aver caricato il file
            if (inputStream == null) {
                throw new RuntimeException("File di configurazione non trovato nelle risorse");
            }
            
            // Leggi il contenuto del file
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            return jsonAdapter.fromJson(jsonContent.toString());
        }
        catch(Exception e){
            System.out.println("no");
        }
        return null;
    }

}
