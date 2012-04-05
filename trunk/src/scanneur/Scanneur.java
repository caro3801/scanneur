/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//commentaire a 2 balles
package scanneur;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author mejor
 */
public class Scanneur extends Thread {

    /**
     * @param args the command line arguments
     */
    JLabel jlabel;
    InetAddress adresse;
    int portDebut;
    int portFin;
    int nbThread;
    boolean configOK = false;
    int lowestPort;
    int highestPort;
    InetAddress host;
    int[] portsToScan;
    boolean udp = true;
    boolean tcp = true;
    public static int NBTHREAD = 10;

    Scanneur(String adresse, boolean udp, boolean tcp, String portD, String portF, boolean plage, String nbThread, JLabel jlabel) {
        this.jlabel = jlabel;
        jlabel.setText("");
        try {
            this.adresse = InetAddress.getByName(adresse);
            if (!udp && !tcp) {
                jlabel.setText("ERREUR : veuillez choisir au moins un mode de connection");
            } else {
                this.udp = udp;
                this.tcp = tcp;
                portDebut = Integer.parseInt(portD);
                portFin = Integer.parseInt(portF);
                if ((portDebut > portFin && plage) || portDebut < 0 || portFin < 0 || portDebut > 65000 || portFin > 65000) {
                    jlabel.setText("ERREUR : veuillez configurer correctement les ports");
                } else {
                    if (!plage) {
                        this.portFin = this.portDebut;
                    }
                    this.nbThread = Integer.parseInt(nbThread);
                    if (this.nbThread < 0) {
                        jlabel.setText("ERREUR : nombre de thread négatif");
                    } else {
                        configOK = true;
                    }
                }
            }
        } catch (UnknownHostException ex) {
            jlabel.setText(adresse + " inconnue");
        }
    }

    public Scanneur(String hostname) throws ScanneurException {
        try {
            host = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            throw new ScanneurException("Host: " + hostname + " unknown :(");
        }
    }

    /**
     * Consctructeur de scanneur pour un port donne
     * @param hostname Adresse de l'hote
     * @param port Numero de port a scanner
     */
    public Scanneur(String hostname, int port)  {
        this(hostname, port, port);
    }

    /**
     * Constructeur du scanner pour une plage de port
     * @param hostname Adresse de l'hote
     * @param lowestPort port de début
     * @param highestPort port de fin
     */
    public Scanneur(String hostname, int lowestPort, int highestPort)  {
        try {
            this.host = InetAddress.getByName(hostname);
            this.lowestPort = lowestPort;
            this.highestPort = highestPort;

        } catch (UnknownHostException ex) {
            Logger.getLogger(Scanneur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Parcours de la plage de ports 
     * @throws ScanneurException 
     */
    private void parcours() throws ScanneurException {
        if (lowestPort <= highestPort && lowestPort > 0) {
            portsToScan = new int[highestPort - lowestPort + 1];

            //definie les ports à scanner
            int i = 0;
            while (lowestPort <= highestPort) {
                portsToScan[i] = lowestPort++;

                System.out.println(portsToScan[i]);
                scan(portsToScan[i++], tcp, udp);
            }
        } else {
            //mauvais interval de ports
            throw new ScanneurException("Mauvais interval de ports");
        }
    }

    private void scan(int port, boolean tcp, boolean udp) {
        if (tcp) {
            TCPscan tcpscan = new TCPscan(host, port);
            tcpscan.start();
            System.out.println(tcpscan.getPortStatus());
        }
        if (udp) {

            UDPscan udpscan = new UDPscan(host, port);
            udpscan.start();

            System.out.println(udpscan.getPortStatus());

        }

    }

    public static void main(String[] args) throws UnknownHostException, ScanneurException {
        Scanneur s = new Scanneur("prevert.upmf-grenoble.fr", 2048, 2055);
        s.parcours();
    }

    @Override
    public void run() {
    }
}
