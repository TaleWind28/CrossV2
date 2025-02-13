package Config;

public class ServerConfig {
    private int TCPport;
    private int UDPport;
    private String UDPaddress;
    private String Userbook;
    private String OrderBook;
    private String TCPaddress;
    
    public ServerConfig(){

    }

    public int getUDPport(){
        return this.UDPport;
    }

    public int getTCPport(){
        return this.TCPport;
    }

    public String getUDPaddres(){
        return this.UDPaddress;
    }

    public void setUDPaddress(String address){
        this.UDPaddress = address;
    }

    public void setTCPport(int port){
        this.TCPport = port;
    }

    public void setUDPport(int port){
        this.UDPport = port;
    }

    public void setOrderBook(String orderBook) {
        OrderBook = orderBook;
    }

    public void setUserbook(String userbook) {
        Userbook = userbook;
    }

    public String getOrderBook() {
        return OrderBook;
    }
    
    public String getUDPaddress() {
        return UDPaddress;
    }

    public String getUserbook() {
        return Userbook;
    }

    public String getTCPaddress() {
        return TCPaddress;
    }
}
