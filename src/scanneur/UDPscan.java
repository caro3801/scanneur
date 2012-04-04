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
    
    final byte [] tampon = "".getBytes();
    final int l = tampon.length;
    
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
                ds.send(dp);
                
            } 
            catch (SocketTimeoutException timeout){
                
            }
            catch (SocketException socket){
                
            }
            catch (IOException ex){
                 
            }
        }
        
    }
    
}
