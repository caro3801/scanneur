package scanneur;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 *
 * @author mejor
 */
public class UDPscan extends Thread implements Observable{

    private InetAddress adresse;
    protected int portStatus;
    protected int port;
    private Scanneur observateur;

    UDPscan(InetAddress adresse, int port) {
        this.adresse = adresse;
        this.port = port;
    }

    public int getPortStatus() {
        return portStatus;
    }

    public int scanUDP() {
        try {
            byte[] tampon = new byte[128];
            DatagramPacket dp = new DatagramPacket(tampon, tampon.length, adresse, port);
            DatagramSocket ds = new DatagramSocket();
            ds.setSoTimeout(1000);
            ds.connect(adresse, port);
            ds.send(dp);
            dp = new DatagramPacket(tampon, tampon.length);

            ds.receive(dp);
            ds.disconnect();
            ds.close();

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
        return 1;
    }

    @Override
    public void run() {
        this.portStatus = this.scanUDP();
        System.out.println(this.port + "\tudp\t" + this.portStatus);
        this.notifierObservateurs();

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
