package epp;

import javax.swing.*;
import java.awt.*;

public class buffPanel extends JPanel {
    private char[][] matrix;
    private char[][] newMatrix;
    private int matHeight;
    private int matWidth;
    private final Color red1 = new Color(255, 51, 51);
    private final Color red2 = new Color(255, 113, 51);
    private final Color red3 = new Color(255, 211, 51);
    private final Color red4 = new Color(255, 245, 51);
    private final Color green1 = new Color(243, 255, 51);
    private final Color green2 = new Color(51, 255, 68);
    private final Color green3 = new Color(51, 255, 186);
    private final Color blue1 = new Color(51, 213, 255);
    private final Color blue2 = new Color(51, 167, 255);
    private final Color blue3 = Color.blue;

    private float xRat;
    private int xBegin;
    private int xEnd;

    private float yRat;
    private int yBegin;
    private int yEnd;

// on recréer une image pour le zoom
    public buffPanel(char[][] matrix,int matWidth,int matHeight, float x_min_rat, float x_max_rat, float y_min_rat, float y_max_rat){
        super();
        this.setPreferredSize(new Dimension(2000,2000));
        this.setBackground(Color.blue);
        this.matrix = matrix;
        this.matWidth=matWidth;
        this.matHeight=matHeight;
        this.xBegin=(int)x_min_rat;
        this.xEnd=(int)x_max_rat;
        this.yBegin=(int)y_min_rat;
        this.yEnd=(int)y_max_rat;
        this.xRat = ((this.matWidth-(x_max_rat-x_min_rat))/(x_max_rat-x_min_rat));
        this.yRat = ((this.matHeight-(y_max_rat-y_min_rat))/(y_max_rat-y_min_rat));
        if(xRat<0.5){
            xRat=1;
        }
        if(yRat<0.5){
            yRat=1;
        }
        newMatrix = new char[((int)(yRat)*(yEnd-yBegin))][(int)(xRat)*(xEnd-xBegin)];
        for(int i=yBegin;i<=yEnd;i++){
            for(int j=xBegin;j<=xEnd;j++){
                for(int k=0;k<(int)xRat;k++){
                    newMatrix[i][j+k]=matrix[i][j];
                }
                for(int k=0;k<(int)yRat;k++){
                    newMatrix[i][j+k]=matrix[i][j];
                }
            }
        }
        this.setPreferredSize(new Dimension((int)(xRat)*(xEnd-xBegin),(int)(yRat)*(yEnd-yBegin)));
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        for(int i=0;i<((int)(yRat)*(yEnd-yBegin));i++){
            for(int j=0;j<(int)(xRat)*(xEnd-xBegin);j++){
                g.setColor(selectedColor(newMatrix[i][j]));
                g.fillRect(j,i,1,1);
            }
        }
    }

    public char[][] getNewMatrix(){return this.newMatrix; }
    private Color selectedColor(char colorNum){
        Color c;
        switch (colorNum){
            default:
                c = blue3;
                break;
            case 1:
                c = red1;
                break;
            case 2:
                c = blue2;
                break;
            case 3:
                c = blue1;
                break;
            case 4 :
                c =  green3;
                break;
            case 5 :
                c = green2;
                break;
            case 6 :
                c = green1;
                break;
            case 7 :
                c = red4;
                break;
            case 8 :
                c = red3;
                break;
            case 9:
                c = red2;
        }
        return c;
    }
}
