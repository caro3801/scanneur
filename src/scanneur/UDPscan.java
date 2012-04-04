/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanneur;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mejor
 */


public class UDPscan extends Thread{
    
    byte [] tampon = "salut!".getBytes();
    int l = tampon.length;
    
    byte [] tampon2 = new byte[256];
    int l2 = tampon.length;
    
    
    private InetAddress adresse;
    private int portDebut;
    private int portFin;

    UDPscan(InetAddress adresse, int port){
        this.adresse = adresse;
        this.portDebut = port;
        this.portFin = port;
    }
    
    UDPscan(InetAddress adresse, int portDebut, int portFin){
        this.adresse = adresse;
        this.portDebut = portDebut;
        this.portFin = portFin;
    }
    
    
    @Override
    public void run(){
        for(int i=portDebut;i<=portFin;i++){
            try {
                DatagramPacket dp = new DatagramPacket(tampon,l,adresse,i);
                DatagramSocket ds = new DatagramSocket();
                int pp = ds.getLocalPort();
                ds.send(dp);
                ds.close();
                System.out.println("msg envoyé"+pp);
                DatagramPacket dpR = new DatagramPacket(tampon2, l2);
                
                System.out.println("1");
                DatagramSocket dsR = new DatagramSocket(pp);
                
                System.out.println("2");
                dsR.receive(dpR);
                String t = new String(dpR.getData());
                System.out.println(t);
            } 
            catch (SocketTimeoutException timeout){
               System.out.println("msg ");
                 
            }
            catch (SocketException socket){
                System.out.println("envoyé" + socket);
                
            }
            catch (IOException ex){
                System.out.println("meoyé");
                 
            }
        }
    }
    
    public static void main (String[] args){
        
        InetAddress ia = null;
        try {
            ia = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            System.out.println("hote inconnu");
        }
        
        UDPscan udpscan = new UDPscan(ia,7);
        udpscan.start();
   }
    
}
