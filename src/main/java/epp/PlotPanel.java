package epp;

import javax.swing.*;
import java.awt.*;

public class PlotPanel extends JPanel {
    public PlotPanel(){
        super();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);//Très important ! Provoque de nombreux bugs si absent !

    }
}
