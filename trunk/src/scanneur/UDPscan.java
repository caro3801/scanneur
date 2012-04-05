package scanneur;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 *
 * @author mejor
 */
public class UDPscan extends Thread {

    private InetAddress adresse;
    private int port;
    private String portStatus;

    UDPscan(InetAddress adresse, int port) {
        this.adresse = adresse;
        this.port = port;
    }

    public String getPortStatus() {
        return portStatus;
    }

    public String scanUDP() {
        try {
            byte[] tampon = new byte[128];
            DatagramPacket dp = new DatagramPacket(tampon, tampon.length, adresse, port);
            DatagramSocket ds = new DatagramSocket();
            int pp = ds.getLocalPort();
            ds.setSoTimeout(1000);
            ds.connect(adresse, port);
            ds.send(dp);
            dp = new DatagramPacket(tampon, tampon.length);

            ds.receive(dp);
            ds.disconnect();
            ds.close();

        } catch (PortUnreachableException ex) {
            return "FERME";

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
        this.portStatus = this.scanUDP();
        System.out.println(this.port + "\tudp\t" + this.portStatus);

    }
}
