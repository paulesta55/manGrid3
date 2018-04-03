package epp;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel customisé qui va être rajouté à l'intérieur de l'EppUI pour qu'il soit dynamique et se repaigne à chaque fois
 * que lui ou un des composants qu'il contient est modifié.
 * @see epp.EppUI
 * @see javax.swing.JPanel
 */
public class PlotPanel extends JPanel {


    public PlotPanel(BorderLayout borderLayout){
        super();
        this.setLayout(borderLayout);
    }

    /**
     * cette classe permet à ce que le JPanel soit paint lorsqu'on lui ajoute des éléments
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);//Très important ! Provoque de nombreux bugs si absent ! Sans ca le jpanel ne se repaint pas quand il est modifié

    }


}
