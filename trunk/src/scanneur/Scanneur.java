/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//commentaire a 2 balles
package scanneur;

import java.net.*;
import java.util.ArrayList;
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
    private int lowestPort;
    private int highestPort;
    private InetAddress host;
    private boolean udp;
    private boolean tcp;
    private int nbThread;
    private ArrayList<Thread> arrayListT = new ArrayList<>();
    protected JLabel msgSystem;
    protected JLabel progress;
    public boolean paramOK = true;
    
    public Scanneur(String adresse,int lowestPort, int highestPort, boolean udp,
            boolean tcp, int nbThread) {
        
        this(adresse, lowestPort, highestPort, udp, tcp, nbThread, null, null);
    }

    public Scanneur(String adresse, int lowestPort, boolean udp,
            boolean tcp, int nbThread, JLabel jLabelMsgSysteme, JLabel jLabelProgess) {

        this(adresse, lowestPort, lowestPort, udp, tcp, nbThread, jLabelMsgSysteme, jLabelProgess);
    }

    public Scanneur(String adresse, int lowestPort, int highestPort, boolean udp,
            boolean tcp, int nbThread, JLabel jLabelMsgSysteme, JLabel jLabelProgess) {

        this.msgSystem = jLabelMsgSysteme;
        this.progress = jLabelProgess;
        host = testHost(adresse);
        testPort(lowestPort, highestPort);
        testProtocol(udp, tcp);
        testnbThread(nbThread);

        this.lowestPort = lowestPort;
        this.highestPort = highestPort;
        this.udp = udp;
        this.tcp = tcp;
        this.nbThread = nbThread;
    }

    int nbScanTotal;
    @Override
    public void run() {
        this.arrayListT.add(this);
        nbScanTotal = highestPort - lowestPort;
        int i = lowestPort;
        while (i <= highestPort) {
            scan(i++, tcp, udp);
        }
    }

    private void scan(int port, boolean tcp, boolean udp) {
        if (tcp) {
            while (Thread.activeCount() > this.nbThread);
            TCPscan tcpscan = new TCPscan(host, port);
            arrayListT.add(tcpscan);
            tcpscan.start();
        }
        if (udp) {
            while (Thread.activeCount() > this.nbThread);
            UDPscan udpscan = new UDPscan(host, port);
            arrayListT.add(udpscan);
            udpscan.start();
        }
    }

//    public static void main(String[] args) throws UnknownHostException, ScanneurException {
//        Scanneur s = new Scanneur("localhost", 0, 120,true,true,20);
//        s.start();
//
//    }
    
    /**
     *
     */
    public void ArretScan() {
        int l = arrayListT.size();
        for (int i = 0; i < l; i++) {

            //Permet de fermer la connection TCP proprement
            if (arrayListT.get(i) instanceof TCPscan) {
                TCPscan tcpTemp = (TCPscan) arrayListT.get(i);
                tcpTemp.stopConnection();
            }
            arrayListT.get(i).stop();

        }
    }

    private InetAddress testHost(String adresse) {
        try {
            return InetAddress.getByName(adresse);
        } catch (UnknownHostException e) {
            msgSystem.setText("Host: " + adresse + " unknown :(");
            paramOK = false;
        }
        return null;
    }

    private void testPort(int lPort, int hPort) {
        if (lPort > hPort || lPort < 0 || hPort > 65535) {
            paramOK = false;
            msgSystem.setText("les ports sont mal configurés");
        }
    }

    private void testProtocol(boolean udp, boolean tcp) {
        if (!udp && !tcp) {
            paramOK = false;
            msgSystem.setText("Veuillez choisir au moins un protocol de connexion");
        }
    }

    private void testnbThread(int nbThread) {
        if (nbThread < 1) {
            paramOK = false;
            msgSystem.setText("nombre de thread mal configuré");
        }
    }
}
