package scanneur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Caroline
 */
/**
 * Scan TCP pour un port et une adresse donnée
 *
 * @author Caroline
 */
public class TCPscan extends Thread implements Observable {

    private InetAddress IP;
    protected int port;
    protected int portStatus;
    private Socket s;
    private Scanneur observateur;

    /**
     * Construit un nouveau Thread pour scanner l'hote sur le port TCP
     *
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
     *
     * @return adresse en cours de scannage
     */
    public InetAddress getIP() {
        return IP;
    }

    /**
     * Return le port qui est en train d'être scanné
     *
     * @return port en cours de scannage
     */
    public int getPort() {
        return port;
    }

    public int getPortStatus() {
        return portStatus;
    }

    /**
     * Scans host/port using the TCP protocol
     */
    @Override
    public void run() {
        try {
            this.portStatus = this.scanTCP();
            System.out.println(this.port + "\ttcp\t" + this.portStatus);
            this.notifierObservateurs();
            //notifier le statut du port
        } catch (NoRouteToHostException e) {
        }
    }

    public int scanTCP() throws NoRouteToHostException {
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
            return 2;
        } catch (IOException e) {
            return 0;

        }
        //Ici, c'est ouvert 
        return 1;
    }

    public void stopConnection() {
        try {
            if (s != null) {
                s.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TCPscan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void ajouterObservateur(Scanneur s) {
        this.observateur = s;
    }

    @Override
    public void supprimerObservateur() {
        this.observateur = null;
    }

    @Override
    public void notifierObservateurs() {
        observateur.actualiser(this);
    }
}
