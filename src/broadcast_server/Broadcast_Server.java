package broadcast_server;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
public class Broadcast_Server {
    public static void main(String[] args) {
        Runnable r;//new thread
            r = new Runnable(){
            @Override 
            public void run(){startserver();}
        };
        new Thread(r).start();
        DatagramSocket socket;
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(23445, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            while (true) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                System.out.println(">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_FUIFSERVER_REQUEST")){
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();
                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    System.out.println(">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
              }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    public static String readbinfile(String filename){
        String a="";
        try {
            Path path = Paths.get(filename);
            a = String.valueOf(Files.readAllLines(path));
        } catch (IOException ex) {
            Logger.getLogger(Broadcast_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return a;
    }
    public static void startserver(){
        DatagramSocket socket2;
        //String configfile = readbinfile("../config/environment.json");
        //String[] config = jsondec(configfile);
        
        //System.out.println(config[0]);
        try {
            socket2 = new DatagramSocket(23447, InetAddress.getByName("0.0.0.0"));
            while(true){
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket2.receive(packet);
                String message = new String(packet.getData()).trim();
                if (message.equals("dir")){
                    byte[] sendData = "thesisphp".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket2.send(sendPacket);
              }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    public static String[] jsondec(String s){
        JSONParser parser = new JSONParser();
        List<String> lst = new ArrayList<>();
        try { 
            Object obj = parser.parse(s);
            JSONArray jray = (JSONArray)obj;
            //obj = parser.parse(s);
            if (jray != null) { 
                int len = jray.size();
                for (int i=0;i<len;i++){
                    lst.add(jray.get(i).toString());
                } 
             }
        } catch (org.json.simple.parser.ParseException ex) {}
        String[] ret = new String[lst.size()];
        lst.toArray(ret);
        return ret;
    }
}
