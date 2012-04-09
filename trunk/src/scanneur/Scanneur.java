/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//commentaire a 2 balles
package scanneur;

import java.net.*;
import java.util.Vector;
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
    int lowestPort;
    int highestPort;
    InetAddress host;
    int[] portsToScan;
    boolean udp = true;
    boolean tcp = true;
    public static int nbThread = 30;
    public Vector<Thread> vectorT = new Vector<>();

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
    public Scanneur(String hostname, int port) {
        this(hostname, port, port);
    }

    /**
     * Constructeur du scanner pour une plage de port
     * @param hostname Adresse de l'hote
     * @param lowestPort port de début
     * @param highestPort port de fin
     */
    public Scanneur(String hostname, int lowestPort, int highestPort) {
        try {
            this.host = InetAddress.getByName(hostname);
            this.lowestPort = lowestPort;
            this.highestPort = highestPort;
            this.vectorT.add(this);

        } catch (UnknownHostException ex) {
            Logger.getLogger(Scanneur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Parcours de la plage de ports 
     * @throws ScanneurException 
     */
    private void parcours() throws ScanneurException {
        if (lowestPort <= highestPort && lowestPort >= 0) {
            portsToScan = new int[highestPort - lowestPort + 1];

            //definie les ports à scanner
            int i = 0;

            while (lowestPort <= highestPort) {
                portsToScan[i] = lowestPort++;

                //  System.out.println(portsToScan[i]);
                scan(portsToScan[i++], tcp, udp);
            }
        } else {
            //mauvais interval de ports
            throw new ScanneurException("Mauvais interval de ports");
        }
    }

    private void scan(int port, boolean tcp, boolean udp) {
      //  Thread[] t = new Thread[5000];
        
//        System.out.println("nb thread actif : "+Thread.enumerate(vectorT.toArray(t)));
            
        if (tcp) {

            while (Thread.activeCount() > this.nbThread);
            TCPscan tcpscan = new TCPscan(host, port);
            vectorT.add(tcpscan);
            tcpscan.start();
            
            //System.out.println(tcpscan.getPortStatus());

        }
        if (udp) {
            while (Thread.activeCount() > this.nbThread);
            //System.out.println(Thread.activeCount());
            

            UDPscan udpscan = new UDPscan(host, port);
            vectorT.add(udpscan);
            udpscan.start();

            // System.out.println(udpscan.getPortStatus());

        }

    }

    public static void main(String[] args) throws UnknownHostException, ScanneurException {
        Scanneur s = new Scanneur("localhost", 0, 54000);
        s.parcours();

    }

    @Override
    public void run() {
    }

    /**
     * 
     */
    public void ArretScan() {
        int l = vectorT.size();
        for (int i = 0; i < l; i++) {
           
            //Permet de fermer la connection TCP proprement
            if (vectorT.get(i) instanceof TCPscan){
                TCPscan tcpTemp = (TCPscan)vectorT.get(i);
                tcpTemp.stopConnection();
        }
            vectorT.get(i).stop();
            
        }
    }
}
