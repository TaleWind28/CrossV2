package Executables;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import Client.ClientClass;
import Config.ClientConfig;
import okio.BufferedSource;
import okio.Okio;

public class ClientMain {
    public static void main(String args[]) throws Exception{
        ClientConfig config = getClientConfig();
        ClientClass client = new ClientClass(config.getTCPaddress(),config.getTCPport());
        client.multiDial();
    }

    public static ClientConfig getClientConfig(){
        JsonAdapter<ClientConfig> jsonAdapter = new Moshi.Builder().build().adapter(ClientConfig.class);
           try (BufferedSource source = Okio.buffer(Okio.source(ClientClass.class.getClassLoader().getResourceAsStream("ClientConfig.json")))) {
                return jsonAdapter.fromJson(source);
            } catch (Exception e) {
                System.out.println(e.getMessage()+" : "+e.getClass());
            }
        return null;
    }
}
