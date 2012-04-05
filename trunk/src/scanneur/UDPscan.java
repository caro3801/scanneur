package scanneur;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 *
 * @author mejor
 */
public class UDPscan implements Runnable {

    private InetAddress adresse;
    private int port;

    UDPscan(InetAddress adresse, int port) {
        this.adresse = adresse;
        this.port = port;
    }

    

    public String scanUDP() {
        try {
            byte[] tampon = new byte[128];
            DatagramPacket dp = new DatagramPacket(tampon, tampon.length, adresse, port);
            DatagramSocket ds = new DatagramSocket();
            int pp = ds.getLocalPort();
            ds.setSoTimeout(1000);
            ds.send(dp);
            System.out.println("msg envoyé sur le port " + port + " du port " + pp);
            dp = new DatagramPacket(tampon, tampon.length);

            ds.receive(dp);
            String t = new String(dp.getData());
            System.out.println(t);
            ds.close();

        } catch (SocketTimeoutException ex) {
            return "OUVERT | FILTRE";

        } catch (InterruptedIOException e) {
            return "FERME";
        } catch (IOException ex) {
            return "FERME";

        } catch (Exception e) {
            return "FERME";
        }
        return "OUVERT";
    }

    @Override
    public void run() {
        String portStatus = this.scanUDP();
        System.out.println(portStatus);
    }

    public static void main(String[] args) {

        InetAddress ia = null;
        try {
            ia = InetAddress.getByName("prevert.upmf-grenoble.fr");
        } catch (UnknownHostException ex) {
            System.out.println("hote inconnu");
        }

        UDPscan udpscan = new UDPscan(ia, 2049);
        udpscan.run();
    }
}
