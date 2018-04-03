/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epp;


import javafx.scene.chart.ValueAxis;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
/**
 * Cette classe est la classe ou on créé la grille une première fois avant de vérifier si elle répond correctement au critère indoor/outdoor
 * @see epp.GridProcess
 * @author Paul Estano
 */

public class Grid extends JPanel {

    private int height=500;
    private int width=500;
    private int nbBlocks, streetWidthY, streetWidthX;
    private int nbBuildings;
    private double xpixelpermeter, ypixelpermeter;
    private boolean processDone = false;
    private int buildingX = 1;
    private int buildingY;
    private int blockSizeW;
    private int blockSizeH;
    private int streetWidth;
    private int vicX;
    private int vicY;
    private boolean iO;
    private char[][] matrix = new char[height][width];
    private Random r = new Random();
    private Color color;
    private int[] delta = new int[2];
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
    private int nColor;
    private GridProcess process;


    /*

     */

    /**
     * C'est dans cette classe que la grille est dessinée (dans la méthode paintComponent). La grille est à la fois painte sur le
     * JPanel et stockée dans une matrice. Si elle ne convient pas au paramètre indoor/outdoor fixé par l'utilisateur
     * elle est découpée dans GridProcess. Il faudra trouver un moyen d'automatiser la définition de la dimension de la grille
     * @param inputPanel permet de récupérer les paramètres utilisateur
     * @param xpixelpermeter nb pixels/m en X
     * @param ypixelpermeter nb pixels/m en Y
     * @param vicX coordonnée de la victime en X
     * @param vicY nb pixels/m en Y
     */
    public Grid(EppUIInput inputPanel, double xpixelpermeter, double ypixelpermeter, int vicX, int vicY) {
        super();

        //A DISCUTER

        this.setPreferredSize(new Dimension(this.width,this.height));

        setBackground(Color.blue);
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                matrix[i][j] = '0';
            }
        }
        this.nColor = 0;
        this.nbBlocks = inputPanel.nb_blocks();
        this.streetWidth=inputPanel.road_width();

        this.xpixelpermeter=xpixelpermeter;
        this.ypixelpermeter=ypixelpermeter;
        System.out.println("largeur rue demandee="+inputPanel.road_width()+"nb pixels_largeur_rue="+inputPanel.road_width()*xpixelpermeter+"nb pixels_hauteur_rue="+inputPanel.road_width()*ypixelpermeter);

        this.vicX = vicX;
        this.vicY = vicY;

        this.iO = false;
        if (inputPanel.vLT_position() == EppUIInput.VLT_position.Indoor) this.iO=true;
        /*
        this.streetWidthY = (int) streetWidth * this.height / (this.height + this.width);
        this.streetWidthX = (int) streetWidth * this.width / (this.height + this.width);
        this.blockSizeW = (int) ((this.width - (nbBlocks + 1) * streetWidthX) / nbBlocks);
        this.blockSizeH = (int) ((this.height - (nbBlocks + 1) * streetWidthY) / nbBlocks);
        this.nbBuildings = r.nextInt(5) + 1;
        this.buildingY = (int) (this.height - (nbBlocks + 1) * streetWidthY) / (nbBlocks * nbBuildings);
        */
    }

    /**
     * Cette méthode trace la grille.
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        //this.streetWidthY = (int) streetWidth * this.height / (this.height + this.width);
        //this.streetWidthX = (int) streetWidth * this.width / (this.height + this.width);
        this.streetWidthX = (int) Math.round(streetWidth * xpixelpermeter);
        this.streetWidthY = (int) Math.round(streetWidth * ypixelpermeter);

        this.blockSizeW = (int) ((this.width - (nbBlocks + 1) * streetWidthX) / nbBlocks);
        this.blockSizeH = (int) ((this.height - (nbBlocks + 1) * streetWidthY) / nbBlocks);
        this.nbBuildings = r.nextInt(5) + 1;
        this.buildingY = (int) (this.height - (nbBlocks + 1) * streetWidthY) / (nbBlocks * nbBuildings);
        int i,j;
        int k = 0;

        int c = 0;
        for (i = 0; i < this.height; i++) {
            for (j = 0; j < this.width; j++) {
                matrix[i][j] = '0';
            }
        }
        i = streetWidthY;
        j = 0;
        for (int m = 0; m < nbBlocks; m++) {

            for (int n = 0; n < nbBuildings; n++) {
                //while(n != blockSizeH){
                j = streetWidthX;
                for (int l = 0; l < nbBlocks; l++) {
                    //for(int k=0; k<blockSizeW;k++){
                    while (k < 3 * blockSizeW / 4) {//choit de blockSize/4 arbitraire pour que le dernier building ne soit pas trop petit
                        buildingX = r.nextInt(blockSizeW - k);//on génère une longueur aléatoire pour chaque building (on le fait en respectant la taille du block cf k)
                        choiceColor();//On choisit le nombre d'étage du batiment

                        g2.setColor(color);
                        g2.fillRect(j, i, buildingX, buildingY);
                        for (int h = j; h < (j + buildingX); h++) {
                            for (int q = i; q < (i + buildingY); q++) {
                                matrix[q][h] = (char) nColor;
                                //System.out.println(matrix[q][h]);
                            }
                        }
                        j += buildingX;
                        c += 1;
                        k += buildingX;
                    }
                    buildingX = blockSizeW - k;//La taille en abscisse du dernier building dépend de la place restante dans le bloc
                    choiceColor();
                    g2.setColor(this.color);
                    g2.fillRect(j, i, buildingX, buildingY);
                    for (int h = j; h < (j + buildingX); h++) {
                        for (int q = i; q < (i + buildingY); q++) {
                            matrix[q][h] = (char) nColor;
                        }
                    }
                    j += buildingX;
                    j += streetWidthX;
                    k = 0;
                }
                i += buildingY;
            }
            i += streetWidthY;

        }
        //g2.setColor(Color.black);
        //g2.fillOval(vicX - 5, vicY, 10, 10);
        process = new GridProcess(this.matrix, this.iO, this.vicX, this.vicY, this.width, this.height,
                this.blockSizeW, this.blockSizeH, this.streetWidthX, this.streetWidthY, this.nbBlocks);
        /*
        On test si la victime est dans le bon environnement tel que défini par l'utilisateur. Sinon on redécoupe la grille
        dans gridPrcess
         */
        g2.setColor(Color.white);

        //pour paintMatrix = true on affiche la matrice à la place du dessin initial
        if (process.getStateProcess() == true) {//On test si la grille a été modifiée dans gridProcess. Si oui on la retrace dans le JPanel
            this.removeAll();
            for (i = 0; i < height; i++) {
                for (j = 0; j < width; j++) {
                    switch (process.getNewMatrix()[i][j]) {
                        case 0:
                            this.color = blue3;

                            break;
                        case 1:
                            this.color = red1;

                            break;
                        case 2:
                            this.color = blue2;

                            break;
                        case 3:
                            this.color = blue1;

                            break;
                        case 4:
                            this.color = green3;

                            break;
                        case 5:
                            this.color = green2;

                            break;
                        case 6:
                            this.color = green1;

                            break;
                        case 7:
                            this.color = red4;

                            break;
                        case 8:
                            this.color = red3;

                            break;

                        case 9:
                            //System.out.println(matrix[i][j]);
                            this.color = red2;
                            break;

                        default:
                            this.color = blue3;
                            break;
                    }
                    //System.out.println(this.nColor);
                    g2.setColor(this.color);
                    g2.drawRect(j, i, 1, 1);

                }
            }
        }
        //g2.setColor(Color.MAGENTA);
        //g2.fillOval(vicX - 5, vicY, 10, 10);

    }


    private void choiceColor() {
        int choice = r.nextInt(9);
        switch (choice) {
            case 0:
                this.color = red1;
                this.nColor = 1;
                break;
            case 1:
                this.color = blue2;
                this.nColor = 2;
                break;
            case 2:
                this.color = blue1;
                this.nColor = 3;
                break;
            case 3:
                this.color = green3;
                this.nColor = 4;
                break;
            case 4:
                this.color = green2;
                this.nColor = 5;
                break;
            case 5:
                this.color = green1;
                this.nColor = 6;
                break;
            case 6:
                this.color = red4;
                this.nColor = 7;
                break;
            case 7:
                this.color = red3;
                this.nColor = 8;
                break;
            case 8:
                this.color = red2;
                this.nColor = 9;
                //System.out.println(choice);
                break;

            default:
                this.color = blue3;
                this.nColor = 0;
                break;
        }

    }



    public void setNbBlocks(int nbBlocks) {
        this.nbBlocks = nbBlocks;
    }

    public int getBlockSizeW() {
        return this.blockSizeW;
    }

    public int getBlockSizeH() {
        return this.blockSizeH;
    }

    public void setStreetWidth(int streetWidth) {
        this.streetWidth = streetWidth;
    }

    /**
     *
     * @return la matrice de la grille (tableau de caractères
     */
    public char[][] getMatrix() {
        return this.matrix;
    }

    /**
     *
     * @return taille des rues (int) en X
     */
    public int getStreetWidthX() {
        return this.streetWidthX;
    }

    /**
     *
     * @return la taille des rues (int) en Y
     */
    public int getStreetWidthY() {
        return this.streetWidthY;
    }


    public BufferedImage takeSnapShot() {
        this.setSize(this.width, this.height);
        BufferedImage bufImage = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        this.paint(bufImage.createGraphics());
        return bufImage;
    }
}
