package Communication.Protocols;
import java.net.*;

import Communication.Messages.Message;
import Communication.Messages.UDPMessage;

import java.io.IOException;

public class UDP implements Protocol {
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private NetworkInterface networkInterface;

    public UDP(String multicastAddress, int port, String networkInterfaceName) throws IOException {
        this.port = port;
        this.group = InetAddress.getByName(multicastAddress);
        //System.out.println("[UDP-Constructor] "+this.group.getHostAddress());
        this.socket = new MulticastSocket(port);

        socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
    }

    @Override
    public int sendMessage(Message mess) {
        try {
            UDPMessage message = (UDPMessage)mess;
            byte[] data = message.getFullMessage().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
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
            UDPMessage message = new UDPMessage().buildFromPackage(packet);;
            return message;
        } catch (IOException e) {
            System.out.println("morto receiver udp");
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
        try {
            return new UDP(group, port,null);    
        } catch (Exception e) {
            System.out.println("[UDP]"+e.getMessage());
            return null;
        }
        
    }
    @Override
    public String toString() {
        return "UDP{group='"+this.group+"', port='"+this.port+"',networkInterface='"+this.networkInterface+"'}";    
    }

    public String toBuilderString(){
        return this.group.getHostAddress()+":"+this.port;
    }

}
