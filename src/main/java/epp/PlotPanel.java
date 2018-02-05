package epp;

import javax.swing.*;
import java.awt.*;

public class PlotPanel extends JPanel {

    private boolean back;

    public PlotPanel(){
        //super();
        back=false;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setBackground(Color.LIGHT_GRAY);
    }

    public void setBack(boolean t) {this.back=t;}

}
