/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanneur;

/**
 *
 * @author mejor
 */

// Interface implémentée par toutes les classes souhaitant avoir des observateurs à leur écoute.
public interface Observable
{
    
        // Méthode permettant d'ajouter (abonner) un observateur.
        public void ajouterObservateur(Scanneur o);
        // Méthode permettant de supprimer (résilier) un observateur.
        public void supprimerObservateur();
        // Méthode qui permet d'avertir tous les observateurs lors d'un changement d'état.
        public void notifierObservateurs();
}
