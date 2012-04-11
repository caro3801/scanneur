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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author mejor
 */
public class Scanneur extends Thread implements Observateur {

    /**
     * @param args the command line arguments
     */
    private int lowestPort;
    private int highestPort;
    private InetAddress host;
    private boolean udp;
    private boolean tcp;
    private int nbThread;
    private ArrayList<Thread> arrayListT = new ArrayList<Thread>();
    protected JLabel msgSystem;
    protected JProgressBar progressBar;
    public boolean paramOK = true;
    protected JTextArea ouvert;
    protected JTextArea ferme;
    protected JTextArea filtre;
    private int nbScanTotal;
    private int nbScanFini = 0;

    public Scanneur(String adresse, int lowestPort, int highestPort, boolean udp,
            boolean tcp, int nbThread) {

        this(adresse, lowestPort, highestPort, udp, tcp, nbThread, null, null);
    }

    public Scanneur(String adresse, int lowestPort, boolean udp,
            boolean tcp, int nbThread, JLabel jLabelMsgSysteme, JProgressBar jProgressBar) {

        this(adresse, lowestPort, lowestPort, udp, tcp, nbThread, jLabelMsgSysteme, jProgressBar);
    }

    public Scanneur(String adresse, int lowestPort, int highestPort, boolean udp,
            boolean tcp, int nbThread, JLabel jLabelMsgSysteme, JProgressBar jProgressBar) {

        this.msgSystem = jLabelMsgSysteme;
        this.progressBar = jProgressBar;
        host = testHost(adresse);
        testPort(lowestPort, highestPort);
        testProtocol(udp, tcp);
        testnbThread(nbThread);

        this.lowestPort = lowestPort;
        this.highestPort = highestPort;
        this.udp = udp;
        this.tcp = tcp;
        this.nbThread = nbThread + Thread.activeCount();
    }

    @Override
    public void run() {
        this.arrayListT.add(this);
        nbScanTotal = highestPort - lowestPort;
        if (tcp && udp) {
            nbScanTotal *= 2;
        }
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
            tcpscan.ajouterObservateur(this);
            tcpscan.start();
        }
        if (udp) {
            while (Thread.activeCount() > this.nbThread);
            UDPscan udpscan = new UDPscan(host, port);
            arrayListT.add(udpscan);
            udpscan.ajouterObservateur(this);
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

    public void setJTextArea(JTextArea ouvert, JTextArea ferme, JTextArea filtre) {
        this.ouvert = ouvert;
        this.ferme = ferme;
        this.filtre = filtre;
    }

    

    @Override
    synchronized public void actualiser(Observable o) {
        nbScanFini++;
        int a = nbScanTotal;
        if (udp && tcp) {
            a += 2;
        } else if (udp || udp) {
            a += 1;
        }
        double pourcentage = Math.floor(nbScanFini * 100 / a);
        this.progressBar.setValue((int) pourcentage);
        if (o instanceof TCPscan) {
            TCPscan oTCP = (TCPscan) o;
            if (oTCP.portStatus == 0) {
                ferme.setText(ferme.getText() + "port : " + oTCP.port + " en TCP\n");
            }
            if (oTCP.portStatus == 1) {
                ouvert.setText(ouvert.getText() + "port : " + oTCP.port + " en TCP\n");
            }
            if (oTCP.portStatus == 2) {
                filtre.setText(filtre.getText() + "port : " + oTCP.port + " en TCP\n");
            }

        } else {
            UDPscan oUDP = (UDPscan) o;
            if (oUDP.portStatus == 0) {
                ferme.setText(ferme.getText() + "port : " + oUDP.port + " en UDP\n");
            }
            if (oUDP.portStatus == 1) {
                ouvert.setText(ouvert.getText() + "port : " + oUDP.port + " en UDP\n");
            }
            if (oUDP.portStatus == 2) {
                filtre.setText(filtre.getText() + "port : " + oUDP.port + " en UDP\n");
            }

        }

    }
}
