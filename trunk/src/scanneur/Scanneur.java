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
    boolean udp;
    boolean tcp;
    int portDebut;
    int portFin;
    int nbThread;
    boolean configOK = false;

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
    
    @Override
    public void run(){
        
    }
    
}
