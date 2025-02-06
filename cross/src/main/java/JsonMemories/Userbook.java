package JsonMemories;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import Users.User;

public class Userbook implements JsonAccessedData{
    protected ConcurrentHashMap<String,User> userMap = new ConcurrentHashMap<>();
    private String jsonFilePath;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public Userbook(String jsonFilePath){
        this.jsonFilePath = jsonFilePath;
    }

    //cerco sulla mappa
    @Override
    public int accessData(String name) {
        //controllo che l'username non sia già presente nel database
        if (this.userMap.containsKey(name))return 200;
        //System.out.println(this.userMap.get(name));
        //utente non presente -> NOT FOUND
        return 404;
    }

    public int checkCredentials(User user){
        //if(!this.userMap.containsKey(user.getUsername()))return 404;
        if(this.accessData(user.getUsername())==404)return 404;
        User usr = this.userMap.get(user.getUsername());
        //controllo se la password criptata corrisponde
        if(!BCrypt.checkpw(user.getPassword(), usr.getPassword()))return 400;
        //System.out.println("password mappa:"+usr.getPassword()+" password utente:"+user.getPassword());
        // if(!user.getPassword().equals(usr.getPassword()))return 400;
        return 200;
    }

    //carico la mappa in una struttura dati
    @Override
    public void loadData() {
        //creo un JsonReader per leggere dal Json
        try (JsonReader reader = new JsonReader(new FileReader(this.jsonFilePath)))  {
            //inizio la lettura
            reader.beginObject();
            System.out.println("oggetto iniziato");
            //ciclo su tutti gli elementi del Json
            while(reader.hasNext()){
                //consumo la key della hasmap
                reader.nextName();
                //creo l'utente da inserire
                User us = this.gson.fromJson(reader,User.class);
                ////stampa di debug
                //System.out.println(us.getPassword());
                //carico l'utente nella mappa
                this.userMap.put(us.getUsername(), us);
            }
            //this.printConcurrentHashMap(this.userMap);
            
        }
        catch(EOFException e){
            System.out.println("File utenti vuoto");
            //this.printConcurrentHashMap(this.userMap);
            return;
        }
        catch (Exception e) {
            System.out.println(e.getClass());
            System.exit(0);
        }
    }

    //aggiungo dati alla struttura dati ed al file Json
    public void addData(User user){
        //inserisco nella mappa
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        this.userMap.put(user.getUsername(), user);
        //aggiorno il Json per farlgi rispecchiare la mappa
        this.dataFlush();
        
        return;
    }

    public void dataFlush(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.jsonFilePath))){
            writer.write(this.gson.toJson(this.userMap));
        }catch(Exception e){
            System.out.println(e.getClass()+ "," + e.getCause());
            System.exit(0);
        }
    }

    public void updateData(User user, String newPasswd){
        //controllo se esiste l'username
        this.accessData(user.getUsername());
        //aggiorno la password
        user.setPassword(BCrypt.hashpw(newPasswd,BCrypt.gensalt()));
        //inserisco nella mappa
        this.userMap.put(user.getUsername(),user);
        //aggiorno il json
        this.dataFlush();
    }

    public static <K, V> void printConcurrentHashMap(ConcurrentHashMap<K, V> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("La ConcurrentHashMap è vuota o non inizializzata.");
            return;
        }

        System.out.println("Contenuto della ConcurrentHashMap:");
        map.forEach((key, value) -> System.out.println("Chiave: " + key + ", Valore: " + value));
    }

    public ConcurrentHashMap<String, User> getUserMap() {
        return userMap;
    }

}
