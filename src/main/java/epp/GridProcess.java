
package epp;

/**
 *
 * @author ensea
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
    /*private int pasX;
    private int pasY;*/
    //private Grid oldGrid;
    private int height;
    private int width;
    private int[] delta = new int[2];
    private char[][] oldMatrix;
    private int blockSizeW, blockSizeH, streetWidthX, streetWidthY, nbBlocks;

    public GridProcess(char[][] matrix, boolean iO, int VicX, int VicY, int width, int height, int blockSizeW,
                       int blockSizeH, int streetWidthX, int streetWidthY, int nbBlocks) {

        //this.oldGrid = oldGrid;
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

    private void calculTar() {
        yTar = (nbBlocks / 2) * (streetWidthY + blockSizeH) + streetWidthY + this.blockSizeH / 2;
        if (iO == true) {
            xTar = (streetWidthX + blockSizeW) * (nbBlocks - 1) / 2 + streetWidthX;
        } else {
            xTar = (this.streetWidthX + this.blockSizeW) * ((int) (this.nbBlocks) / 2);
        }
    }

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
    public void copyMatrixPartLR() {
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

    public boolean getStateProcess() {
        return this.stateProcess;
    }

    public int getXTar() {
        return this.xTar;
    }

    public int getYTar() {
        return this.yTar;
    }

    public char[][] getNewMatrix() {
        return this.newMatrix;
    }
}
