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
        this.socket = new MulticastSocket(port);

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
            UDPMessage message = (UDPMessage)mess;
            byte[] data = message.getData().getBytes();
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
            return new UDPMessage(new String(packet.getData(), 0, packet.getLength()));
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
        System.out.println(udpString);
        // Estrazione dei campi
        String group = udpString.split("'")[1];       // '230.0.0.1'
                        int port = Integer.parseInt(udpString.split("'")[3]);  // '5000'
                        String networkInterface = udpString.split("'")[5];
                        System.out.println(networkInterface);
                        String[]pino = group.split("/");
                        System.out.println("[ClientMain] "+pino[1]);
                        String[]mino = networkInterface.split(" ");
                        System.out.println("[ClientMain] "+port);
                        mino = mino[0].split(":");
                        System.out.println("[ClientMain] "+mino[1]);
        try {
            return new UDP(pino[1], port, mino[1]);    
        } catch (Exception e) {
            System.out.println("[UDP]"+e.getMessage());
            return null;
        }
        
    }
    @Override
    public String toString() {
        return "UDP{group='"+this.group+"', port='"+this.port+"',networkInterface='"+this.networkInterface+"}";    
    }

}
