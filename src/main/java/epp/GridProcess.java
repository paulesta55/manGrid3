
package epp;

/**
 * Cette classe est appelée une fois que la grille a été créée une première fois, elle permet à la fois de contrôler que
 * la victime est au bon endroit (indoor ou outdoor) et modifie la grille sinon, pour quelle réponde correctement à ce
 * paramètre.
 * @see epp.Grid
 * @author Paul Estano
 */

public class GridProcess {

    private char[][] newMatrix;
    private boolean gridIo = false;
    private boolean stateProcess = false;
    private boolean iO;
    private int vicX;
    private int vicY;
    private int xTar;
    private int yTar;
    private int height;
    private int width;
    private int[] delta = new int[2];
    private char[][] oldMatrix;
    private int blockSizeW, blockSizeH, streetWidthX, streetWidthY, nbBlocks;

    /**
     *
     * @param matrix matrice initiale
     * @param iO critère indoor/outdoor choisi par l'utilisateur
     * @param VicX coordonnée de la victime en X
     * @param VicY coordonnée de la victime en Y
     * @param width longueur de la grille
     * @param height largeur de la grille
     * @param blockSizeW taille d'un bloc en X
     * @param blockSizeH taille d'un bloc en Y
     * @param streetWidthX taille des rues en X
     * @param streetWidthY taille des rues en Y
     * @param nbBlocks nb de bloc/côté
     */
    public GridProcess(char[][] matrix, boolean iO, int VicX, int VicY, int width, int height, int blockSizeW,
                       int blockSizeH, int streetWidthX, int streetWidthY, int nbBlocks) {

        this.nbBlocks = nbBlocks;

        this.iO = iO;
        vicX = VicX;
        vicY = VicY;
        this.oldMatrix = matrix;
        this.height = height;
        this.width = width;
        this.blockSizeH = blockSizeH;
        this.blockSizeW = blockSizeW;
        this.streetWidthX = streetWidthX;
        this.streetWidthY = streetWidthY;
        newMatrix = new char[height][width];//getHeight et getWidth sont contenues dans la bibliothèque de JPanel
        System.out.println("iO=" + iO);

        if (this.oldMatrix[vicY][vicX] != '0') {//on détermine si la victime est in ou out sur la grille
            gridIo = true;

        }
        System.out.println("gridIo=" + gridIo);
        if (gridIo != iO) {//Si iO != gridIo alors il faut modifier la grille
            stateProcess = true;
            calculTar();
           calculDelta();
           this.copyMatrixPartLR();
        }

    }

    /**
     * Calcul des coordonnées de la cible sur laquelle on veut positionner la victime
     */
    private void calculTar() {
        yTar = (nbBlocks / 2) * (streetWidthY + blockSizeH) + streetWidthY + this.blockSizeH / 2;
        if (iO == true) {
            xTar = (streetWidthX + blockSizeW) * (nbBlocks - 1) / 2 + streetWidthX;
        } else {
            xTar = (this.streetWidthX + this.blockSizeW) * ((int) (this.nbBlocks) / 2);
        }
    }

    /*

     */

    /**
     * Calcul de la distance en X et en Y entre la cible et la position actuelle (au premier tracé de la grille) de la
     * victime
     * @return int[2] la distance delta (en X et en Y d'où l'utilisation d'un tableau) actuelle de la victime par rapport à la position où elle sera après process
     */
    public int[] calculDelta() {
        if (iO == false) {
            delta[0] = xTar - (vicX);
            delta[1] = yTar - (vicY);
        } else {
            delta[0] = (xTar) - vicX;
            delta[1] = (yTar ) - vicY;
        }
        return delta;
    }

    /**
     * la grille est modifiée dans cette fonction :
     * on découpe d'abord delta (en X et en Y) de l'image puis on translate le bout central après quoi on "recolle"
     * les morceaux découpés
     */
    public void copyMatrixPartLR() {//la grille est découpée et modifiée ici
        int k = 0;
        for (int j = 0; j < width - 1; j++) {//gestion des parties hautes et basses de la matrice
            k=0;
            if (delta[1] <= 0) {
                for (int i = streetWidthY; i < height - 1; i++) {
                    if (delta[1]+height -1 + i < height-streetWidthY) {
                        this.newMatrix[i][j] = this.oldMatrix[height - 1+delta[1] + i][j];//on repositionne la partie droite de la matrice à gauche
                        //System.out.println("j="+j);
                        //System.out.println("i="+i);
                    } else {
                        this.newMatrix[i][j] = this.oldMatrix[k][j];
                        k++;
                    }
                }
            }
            else{
                for(int i = streetWidthY; i < height - 1; i++){
                    if(height-1-delta[1]+i<height-2*streetWidthY){
                        this.newMatrix[height-1-delta[1]+i][j] = this.oldMatrix[i][j];
                    }
                    else{
                        this.newMatrix[k][j]=this.oldMatrix[i][j];
                        k++;
                    }
                }
            }
        }
        char [][] tempMatrix = new char[height][width];
        for(int i = 0; i<height-1;i++){//copie temporaire de la matrice pour pouvoir réutiliser le même algo pour les cotés de la matrice
            for(int j = 0; j<width-1;j++){
                tempMatrix[i][j]=newMatrix[i][j];
            }
        }
        for (int i=0;i<height-1;i++){//gestion des cotés
            k=0;
            if(delta[0]<=0){
                for(int j = streetWidthX;j<width;j++){
                    if(width+delta[0]-1+j<width-streetWidthX){
                        newMatrix[i][j]=tempMatrix[i][width-1+delta[0]+j];
                    }
                    else{
                        this.newMatrix[i][j] = tempMatrix[i][k];
                        k++;
                    }
                }
            }
            else{
                for(int j = streetWidthX;j<width;j++){
                    if(j-delta[0]-1+width<width){
                        newMatrix[i][width-1-delta[0]+j]=tempMatrix[i][j];
                    }
                    else{
                        newMatrix[i][k]=tempMatrix[i][j];
                        k++;
                    }
                }
                
            }
        }
    }
    //On utilisera copyArea et clearArea

    /**
     *
     * @return booleen qui indique si la grille va subir un process (ie si oui ou non la victime est au bon endroit
     * (indoor ou outdoor)
     */
    public boolean getStateProcess() {
        return this.stateProcess;
    }

    public int getXTar() {
        return this.xTar;
    }

    public int getYTar() {
        return this.yTar;
    }

    /**
     *
     * @return le tableau char[][] de la matrice après qu'elle ait été transformé pour que la victime soit positionné au bon endroit sur la grille
     */
    public char[][] getNewMatrix() {
        return this.newMatrix;
    }
}
