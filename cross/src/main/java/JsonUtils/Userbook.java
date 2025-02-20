package JsonUtils;

import java.io.EOFException;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.crypto.bcrypt.BCrypt;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import okio.Okio;


public class Userbook implements JsonAccessedData{
    protected ConcurrentHashMap<String,User> userMap = new ConcurrentHashMap<>();
    private String jsonFilePath;
    private Moshi moshi = new Moshi.Builder().build();
    private JsonAdapter<UserMap> adapter = moshi.adapter(UserMap.class);
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
    //aggiungo dati alla struttura dati ed al file Json
    public void addData(User user){
        //inserisco nella mappa
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        this.userMap.put(user.getUsername(), user);
        //aggiorno il Json per farlgi rispecchiare la mappa
        this.dataFlush();
        
        return;
    }

    //carico la mappa in una struttura dati
    @Override
    public void loadData() {
        //creo un JsonReader per leggere dal Json
        try (JsonReader reader = JsonReader.of(Okio.buffer(Okio.source(new File(this.jsonFilePath))))){
            UserMap loadedMap = adapter.fromJson(reader);
            this.userMap.putAll(loadedMap.usermap);
        }
        catch(EOFException e){
            System.out.println("[UserBook-loadData] File utenti vuoto");
            //this.printConcurrentHashMap(this.userMap);
            return;
        }
        catch (Exception e) {
            System.out.println("[UserBook-loadData]"+e.getClass()+e.getMessage()+e.getCause());
            System.exit(0);
        }
    }

    public void dataFlush(){
        UserMap mappa = new UserMap(this.userMap);
        try(JsonWriter writer = JsonWriter.of(Okio.buffer(Okio.sink(new File(this.jsonFilePath))))){
            writer.setIndent(" ");
            this.adapter.toJson(writer,mappa);
        }catch(Exception e){
            System.out.println("[UserBook-dataFlush]"+e.getClass()+ "," + e.getCause());
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
