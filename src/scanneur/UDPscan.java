package scanneur;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mejor
 */
public class UDPscan extends Thread implements Observable{

    /**
     * adresse de connexion
     */
    private InetAddress adresse;
    /**
     * statut final du port
     */
    protected int portStatus;
    /**
     * port de connexion
     */
    protected int port;
    /**
     * observateur à notifier
     */
    private Scanneur observateur;
    
    DatagramSocket ds;

    /**
     * constructeur 
     * @param adresse
     * @param port 
     */
    UDPscan(InetAddress adresse, int port) {
        this.adresse = adresse;
        this.port = port;
    }

    public int getPortStatus() {
        return portStatus;
    }

    /**
     * effectue le scan udp
     * @return 
     */
    public int scanUDP() {
        try {
            byte[] tampon = new byte[128];
            DatagramPacket dp = new DatagramPacket(tampon, tampon.length, adresse, port);
            ds = new DatagramSocket();
            ds.setSoTimeout(100);
            ds.connect(adresse, port);
            ds.send(dp);
            dp = new DatagramPacket(tampon, tampon.length);

            ds.receive(dp);
            ds.disconnect();
            ds.close();
            return 1;

        } catch (PortUnreachableException ex) {
            return 0;

        } catch (SocketTimeoutException ex) {
            return 2;

        } catch (InterruptedIOException e) {
            return 0;
        } catch (IOException ex) {
            return 0;

        } catch (Exception e) {
            return 0;
        }
        
    }

    @Override
    public void run() {
        this.portStatus = this.scanUDP();
        System.out.println(this.port + "\tudp\t" + this.portStatus);
        this.notifierObservateurs();

    }
    
        public void stopConnection() {
       
            if (!ds.isClosed())
            ds.close();
            
      
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
