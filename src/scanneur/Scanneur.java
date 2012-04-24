/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
    /**
     * port le plus petit de la plage de port
     */
    private int lowestPort;
    /**
     * port le plus elevé de la plage de port
     */
    private int highestPort;
    /**
     * adresse de connexion
     */
    private InetAddress host;
    /**
     * vrai si le scan udp doit être réalisé
     */
    private boolean udp;
    /**
     * vrai si le scan tcp doit être réalisé
     */
    private boolean tcp;
    /**
     * nombre de threads à lancer simultanément
     */
    private int nbThread;
    /**
     * arraylist de tous les thread déjà lancé
     */
    private ArrayList<Thread> arrayListT = new ArrayList<Thread>();
    /**
     * jlabel pour afficher les messages du scanneur
     */
    protected JLabel msgSystem;
    /**
     * permet de modifier la barre de progression
     */
    protected JProgressBar progressBar;
    /**
     * passe à faux si les paramètres ont été mal initialisés
     */
    public boolean paramOK = true;
    /**
     * correspond à la fenêtre des ports ouverts
     */
    protected JTextArea ouvert;
    /**
     * correspond à la fenêtre des ports fermés
     */
    protected JTextArea ferme;
    /**
     * correspond à la fenêtre des ports filtrés
     */
    protected JTextArea filtre;
    /**
     * permet de calculer le pourcentage
     */
    private int nbScanTotal;
    /**
     * permet de calculer le pourcentage
     */
    private int nbScanFini = 0;

    /**
     * contructeur d'un scanneur pour une plage donnée
     * @param adresse de connexion
     * @param lowestPort port le plus bas de la plage
     * @param highestPort port le plus haut de la plage
     * @param udp si vrai alors aeffectue scan udp
     * @param tcp si vrai alors effectue le scan tcp
     * @param nbThread nombre de thread à lancé en parralele
     */
    public Scanneur(String adresse, int lowestPort, int highestPort, boolean udp,
            boolean tcp, int nbThread) {

        this(adresse, lowestPort, highestPort, udp, tcp, nbThread, null, null);
    }

    /**
     * contructeur pour un port donnée
     * @param adresse de connexion
     * @param lowestPort port à scanner
     * @param udp si vrai alors aeffectue scan udp
     * @param tcp si vrai alors effectue le scan tcp
     * @param nbThread nombre de thread à lancé en parralele
     * @param jLabelMsgSysteme correspond au label des msg systemes
     * @param jProgressBar correspond à la progress bar
     */
    public Scanneur(String adresse, int lowestPort, boolean udp,
            boolean tcp, int nbThread, JLabel jLabelMsgSysteme, JProgressBar jProgressBar) {

        this(adresse, lowestPort, lowestPort, udp, tcp, nbThread, jLabelMsgSysteme, jProgressBar);
    }

    /**
     * contructeur d'un scanneur pour une plage donnée
     * @param adresse de connexion
     * @param lowestPort port le plus bas de la plage
     * @param highestPort port le plus élévé de la plage
     * @param udp si vrai alors aeffectue scan udp
     * @param tcp si vrai alors effectue le scan tcp
     * @param nbThread nombre de thread à lancé en parralele
     * @param jLabelMsgSysteme correspond au label des msg systemes
     * @param jProgressBar correspond à la progress bar
     */
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

    /**
     * execute le sacnneur dans un nouveau processus
     */
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
    
    /**
     * effectue les scans de tcp et ou udp
     * @param port le port à scanner
     * @param tcp vrai s tcp
     * @param udp vrai si udp
     */
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
     * permet d'arreter un scan en parcourant l'arraylist
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

    /**
     * permet de tester la validité de l'adresse
     * @param adresse adresse de connexion
     * @return 
     */
    private InetAddress testHost(String adresse) {
        try {
            return InetAddress.getByName(adresse);
        } catch (UnknownHostException e) {
            msgSystem.setText("Host: " + adresse + " unknown :(");
            paramOK = false;
        }
        return null;
    }

    /**
     * permet de tester la plage de port
     * @param lPort
     * @param hPort 
     */
    private void testPort(int lPort, int hPort) {
        if (lPort > hPort || lPort < 0 || hPort > 65535) {
            paramOK = false;
            msgSystem.setText("les ports sont mal configurés");
        }
    }
    
    /**
    * permet de tester que au moins des deux protocoles soit validé
    * @param udp
    * @param tcp 
    */
    private void testProtocol(boolean udp, boolean tcp) {
        if (!udp && !tcp) {
            paramOK = false;
            msgSystem.setText("Veuillez choisir au moins un protocol de connexion");
        }
    }

    /**
     * le nombre de thread doit etre positif
     * @param nbThread 
     */
    private void testnbThread(int nbThread) {
        if (nbThread < 1) {
            paramOK = false;
            msgSystem.setText("nombre de thread mal configuré");
        }
    }

    /**
     * initialise les zones d'affichages
     * @param ouvert
     * @param ferme
     * @param filtre 
     */
    public void setJTextArea(JTextArea ouvert, JTextArea ferme, JTextArea filtre) {
        this.ouvert = ouvert;
        this.ferme = ferme;
        this.filtre = filtre;
    }

    

    /**
     * methodes appelé à chaque fin de thread (pattern observer) 
     * @param o contient les onfis à afficher
     */
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
