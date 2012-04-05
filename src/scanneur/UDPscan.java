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
    
    byte [] tampon = new byte[4096];
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
                ds.setSoTimeout(10000);
                ds.send(dp);
                System.out.println("msg envoyé sur le port "+i+" du port "+pp);
               // DatagramPacket dpR = new DatagramPacket(tampon2, l2);
                //DatagramSocket dsR = new DatagramSocket(pp);
   
                ds.receive(dp);
                ds.close();
                String t = new String(dp.getData());
                
                System.out.println(t);
            } 
            catch (SocketTimeoutException timeout){
               System.out.println("timeout ");
                
            }
            
            catch (SocketException socket){
                System.out.println("socket NOK" + socket);
                
            }
            catch (IOException ex){
                System.out.println("IO"+ex);
                 
            }
        }
    }
    
    public static void main (String[] args){
        
        InetAddress ia = null;
        try {
            ia = InetAddress.getByName("prevert.upmf-grenoble.fr");
        } catch (UnknownHostException ex) {
            System.out.println("hote inconnu");
        }
        
        UDPscan udpscan = new UDPscan(ia,123);
        udpscan.start();
   }
    
}
