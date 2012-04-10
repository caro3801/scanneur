package scanneur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Caroline
 */
/**
 * Scan TCP pour un port et une adresse donnée
 * @author Caroline
 */
public class TCPscan extends Thread {

    private InetAddress IP;
    private int port;
    private String portStatus;
    private Socket s;

    /**
     * Construit un nouveau Thread pour scanner l'hote sur le port TCP
     * @param InetAdress IP L'adresse IP de l'hote a scanner
     * @param port Numero de port a scanner
     */
    public TCPscan(InetAddress IP, int port) {
        this.IP = IP;
        this.port = port;
    }

    /**
     * 
     * Retourne l'adresse qui est en train d'être scanné 
     * @return adresse en cours de scannage
     */
    public InetAddress getIP() {
        return IP;
    }

    /**
     * Return le port qui est en train d'être scanné
     * @return port en cours de scannage
     */
    public int getPort() {
        return port;
    }

    public String getPortStatus() {
        return portStatus;
    }

    /**
     *Scans host/port using the TCP protocol
     */
    @Override
    public void run() {
        try {
            this.portStatus = this.scanTCP();
            System.out.println(this.port+"\ttcp\t" + this.portStatus);
            //notifier le statut du port
        } catch (NoRouteToHostException e) {

           
        }
    }

    public String scanTCP() throws NoRouteToHostException {
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(IP, port));
            s.close();
        } catch (NoRouteToHostException e) {
            throw e; //throw to calling
        } catch (SocketTimeoutException e) {
            /*
             * Lorsqu'on a mis trop de temps
             */
            return "FILTRE";
        } catch (IOException e) {
            return "FERME";

        }
        //Ici, c'est ouvert 
        return "OUVERT";
    }
    
    public void stopConnection(){
        try {
            if (s != null)
              s.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPscan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
