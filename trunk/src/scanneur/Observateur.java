/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanneur;

/**
 *
 * @author mejor
 */
// Interface implémentée par tous les observateurs.
public interface Observateur
{
        // Méthode appelée automatiquement lorsque l'état (position ou précision) du GPS change.
        public void actualiser(Observable o);
}
