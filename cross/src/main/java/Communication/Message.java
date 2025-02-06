package Communication;
/*
 * Codici HTTP
 * 200 OK
 * 201 CREATED
 * 204 NO CONTENT -> richiesta completata ma ritorno void
 * 400 Bad Request -> richiesta malformata
 * 401 Unauthorized -> Autenticazione richiesta
 * 403 Forbidden -> Accesso Negato
 * 404 Not Found -> Risorsa inesistente
 * 408 Request Timeout -> Timeout Inattivita
 * 500 Internal Server Error
 * 502 Bad Gateway -> risposta non valida ricevuta dal server
 * 503 Service Unavailable -> Servizio non disponibile
 */
public class Message {
    public String payload;
    public int code;
    //costruttore di default
    public Message(){

    }
    public Message(String payload){
        this.payload = payload;
        this.code = 200;
    }
    //costruttore per risposte HTTP
    public Message(String payload, int code){
        this.payload = payload;
        this.code = code;
    }

    public String toString(){
        return "Message{code = "+code + " ,payload = "+payload+"}";
    }
    
}
