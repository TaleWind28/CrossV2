package Communication.Protocols;
import java.net.*;

import Communication.Messages.Message;
import Communication.Messages.UDPMessage;

import java.io.IOException;

public class UDP implements Protocol {
    private MulticastSocket socket;
    private InetAddress group;
    private String groupId;
    private int port;
    private NetworkInterface networkInterface;
    private String netIF;

    public UDP(String multicastAddress, int port, String networkInterfaceName) throws IOException {
        this.port = port;
        this.groupId = multicastAddress;
        this.group = InetAddress.getByName(multicastAddress);
        this.socket = new MulticastSocket(port);
        this.netIF = networkInterfaceName;

        // Ottieni l'interfaccia di rete
        this.networkInterface = NetworkInterface.getByName(networkInterfaceName);
        if (this.networkInterface == null) {
            throw new IOException("Interfaccia di rete non trovata: " + networkInterfaceName);
        }

        // Unirsi al gruppo multicast utilizzando l'interfaccia specificata
        socket.joinGroup(new InetSocketAddress(group, port), networkInterface);
    }

    @Override
    public void setReceiver(Socket input) {
        throw new UnsupportedOperationException("UDP non usa connessioni dirette con Socket.");
    }

    @Override
    public void setSender(Socket output) {
        throw new UnsupportedOperationException("UDP non usa connessioni dirette con Socket.");
    }

    @Override
    public int sendMessage(Message mess) {
        try {
            System.out.println("Minosse");
            UDPMessage message = (UDPMessage)mess;
            byte[] data = message.getFullMessage().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
            System.out.println("[UDP] invio messaggio= "+message.toString());
            return data.length;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Message receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            
            UDPMessage message = new UDPMessage(netIF, groupId).buildFromPackage(packet);;
            //System.out.println("[UDP]received message: "+packet.getData());
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            socket.leaveGroup(new InetSocketAddress(group, port), networkInterface);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UDP buildFromString(String udpString){
        String[] fields = udpString.split(":");
        String group = fields[0];       // '230.0.0.1'
        int port = Integer.parseInt(fields[1]);
        String networkInterface = fields[2];
        try {
            return new UDP(group, port, networkInterface);    
        } catch (Exception e) {
            System.out.println("[UDP]"+e.getMessage());
            return null;
        }
        
    }
    @Override
    public String toString() {
        return "UDP{group='"+this.groupId+"', port='"+this.port+"',networkInterface='"+this.netIF+"'}";    
    }

    public String toBuilderString(){
        return this.groupId+":"+this.port+":"+this.netIF;
    }

}
