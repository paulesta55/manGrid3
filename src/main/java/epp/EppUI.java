package epp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.seamcat.model.*;
import org.seamcat.model.plugin.eventprocessing.PanelDefinition;
import org.seamcat.model.plugin.eventprocessing.Panels;
import org.seamcat.model.plugin.eventprocessing.PostProcessing;
import org.seamcat.model.plugin.eventprocessing.PostProcessingUI;
import org.seamcat.model.types.result.Results;
import org.seamcat.model.types.result.VectorResult;

import java.lang.Math;
import javax.swing.*;
import java.awt.*;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.awt.image.*;

/**
 * Cette Interface permet d'ajouter un environnement graphique au plugin. C'est ici qu'on affiche la grille
 * 3 méthodes sont à implémenter obligatoirement :
 * -BuildUI
 * -getTitle
 * -panelDefinition
 * l'annotation @PostProcessing permet de définir un bouton qui déclenche la méthode qu'elle précède
 * @author Paul Estano
 * @see org.seamcat.model.plugin.eventprocessing.PostProcessingUI
 */
public class EppUI extends JFrame implements PostProcessingUI {
    private Panels panels;
    private JSplitPane split;
    private PlotPanel plotPanel = new PlotPanel(new BorderLayout());
    private XYPlot gridXYplot = new XYPlot();
    private XYDataset results;
    private JFreeChart gridChart;
    private ChartPanel gridPanel;
    private EppUIInput inputPanel;
    private JLabel gridLabel = new JLabel("Manhattan Grid");
    private JLabel gridLegend = new JLabel("Number of floors");
    private double[] vectorVLR_X;
    private double[] vectorVLR_Y;
    private double[] vectorVLT_X;
    private double[] vectorVLT_Y;
    private double[] vectorILR_X;
    private double[] vectorILR_Y;
    private double[] vectorILT_X;
    private double[] vectorILT_Y;
    private double maxX, minX;
    private double maxY, minY;
    private double x_size, y_size;
    private double vicX,vicY;
    private GridBagLayout gridBagLayout = new GridBagLayout();
    private int maxFloor = 10;
    //IL FAUT TROUVER UN MOYEN D'ADAPTER LA FENETRE AUTOMATIQUEMENT.
    private int wChartPanel=500;
    private int hChartPanel=500;

    /**
     * Cette méthode construit l'IHM en assemblant à la fois des panels statiques et les panels dynamiques créé après l'appuie sur
     * un bouton postProcessing(Cf plus loin)
     * Ici plotPanel est un contenu "dynamique" et Paramètres correspond à l'interface EppUIInput implémenté tel que le
     * demande Seamcat (Cf EppUIInput)
     * @param scenario
     * @param canvas
     * @param panels
     */
    public void buildUI(Scenario scenario, JPanel canvas, Panels panels) {

        this.panels = panels;
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.add(panels.get("Paramètres").getPanel());
        split.add(plotPanel);
        canvas.setLayout(new BorderLayout());
        canvas.add(split, BorderLayout.CENTER);
    }

    /**
     * Renvoie le titre à afficher sur l'onglet de la fenêtre
     * @return le titre de la fenêtre
     */
    public String getTitle() {
        return "Manhattan Grid";
    }

    /**
     * Cette méthode sert à ajouter à la fenêtre une interface ou l'utilisateurs fourni les données nécessaire au process.
     * On pourra ensuite les récupérer pour effectuer nos opérations.
     * Cette interface doit être impérativement défini en tant que telle (cf EppUIInput)
     * On les récupère grace à la méthode get("Nom du panel").getModel qu'on applique sur panels (argument de la méthode
     * buildUI
     * @return le panneau de paramètres d'entrée pour le post processing
     */
    public PanelDefinition[] panelDefinitions() {
        return new PanelDefinition[]{
                new PanelDefinition<EppUIInput>("Paramètres", EppUIInput.class)
        };

    }

    public double getMaxTab(double[] tab) {
        double max = tab[0];

        for (int i = 0; i < tab.length; i++) {
            if (tab[i] > max)
                max = tab[i];
        }
        return max;

    }

    //Search the min of a table
    public double getMinTab(double[] tab) {
        double min = tab[0];

        for (int i = 0; i < tab.length; i++) {
            if (tab[i] < min)
                min = tab[i];
        }
        return min;

    }

    /**
     * Cette annotation crée le bouton "Plot" qui va déclencher le "post processing".
     * Ici le post processing déclenche la méthode preparePlot.
     * @param scenario
     * @param results
     * @param input
     */
    @PostProcessing(order = 2, name = "Plot")
    public void preparePlot(Scenario scenario, Results results, Epp.Input input) {
        //plotPanel.setBack(true);
        //plotPanel.repaint();

        //Use the ManGridEPP results

        VectorResult posVLR_X = results.findVector(input.VLRX).getValue();
        VectorResult posVLR_Y = results.findVector(input.VLRY).getValue();
        VectorResult posVLT_X = results.findVector(input.VLTX).getValue();
        VectorResult posVLT_Y = results.findVector(input.VLTY).getValue();
        VectorResult posILR_X = results.findVector(input.ILRX).getValue();
        VectorResult posILR_Y = results.findVector(input.ILRY).getValue();
        VectorResult posILT_X = results.findVector(input.ILTX).getValue();
        VectorResult posILT_Y = results.findVector(input.ILTY).getValue();

        vectorVLR_X = posVLR_X.asArray();
        vectorVLR_Y = posVLR_Y.asArray();
        vectorVLT_X = posVLT_X.asArray();
        vectorVLT_Y = posVLT_Y.asArray();
        vectorILR_X = posILR_X.asArray();
        vectorILR_Y = posILR_Y.asArray();
        vectorILT_X = posILT_X.asArray();
        vectorILT_Y = posILT_Y.asArray();


        double maxVLRX = getMaxTab(vectorVLR_X);
        double maxVLTX = getMaxTab(vectorVLT_X);
        double maxILTX = getMaxTab(vectorILT_X);
        double maxILRX = getMaxTab(vectorILR_X);
        double[] tabX = {maxILRX, maxILTX, maxVLRX, maxVLTX};
        maxX = getMaxTab(tabX);

        double maxVLRY = getMaxTab(vectorVLR_Y);
        double maxVLTY = getMaxTab(vectorVLT_Y);
        double maxILTY = getMaxTab(vectorILT_Y);
        double maxILRY = getMaxTab(vectorILR_Y);
        double[] tabY = {maxILRY, maxILTY, maxVLRY, maxVLTY};
        maxY = getMaxTab(tabY);


        double minVLRX = getMinTab(vectorVLR_X);
        double minVLTX = getMinTab(vectorVLT_X);
        double minILTX = getMinTab(vectorILT_X);
        double minILRX = getMinTab(vectorILR_X);
        double[] tabminX = {minILRX, minILTX, minVLRX, minVLTX};
        minX = getMinTab(tabminX);

        double minVLRY = getMinTab(vectorVLR_Y);
        double minVLTY = getMinTab(vectorVLT_Y);
        double minILTY = getMinTab(vectorILT_Y);
        double minILRY = getMinTab(vectorILR_Y);
        double[] tabminY = {minILRY, minILTY, minVLRY, minVLTY};
        minY = getMinTab(tabminY);

        x_size = (Math.abs(minX) + Math.abs(maxX));
        y_size = (Math.abs(minY) + Math.abs(maxY));

        vicX = (int)(maxVLTX*wChartPanel/(maxX-minX));
        vicY = (int)(maxVLTY*hChartPanel/(maxY-minY));

        inputPanel = (EppUIInput) panels.get("Paramètres").getModel();

        plot(scenario);
    }

    /**
     * Ici est créé le contenu dynamique ajouté au split dans la méthode buildUI.
     * Ce contenu est placé dans un JPanel customisé (Cf class PlotPanel) qui est lui-même ajouté au plit de la méthode buildUI
     * On utilise le gridBaglayout par facilité d'utilisation
     * @param scenario
     */
    private void plot(Scenario scenario) {

        int red, green, blue;
        plotPanel.removeAll();
        results = createDataset();
        gridXYplot.setDataset(results);//crée les données à afficher sur la grille
        /*
        On peut ensuite utiliser un XYplot sur lequel on applique backgroundImage en fond à l'aide de
        de setBackgroundImage (hérité de plot et utilisé dans customChartPanel)
        JFreeChart correspond au graphe en lui-même qui ne peut être créé que si myplot a été initialisé (sans backgroundimage)
        customChartPanel correspond au conteneur du graphe
        */
        plotPanel.setLayout(gridBagLayout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 500;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        plotPanel.add(gridLabel,gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        plotPanel.add(gridLegend,gbc);

        gbc.gridwidth = 1;
        gbc.gridheight = 100;
        gbc.ipady = 0;
        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;

        plotPanel.setLayout(gridBagLayout);
        NumberAxis xAxis=new NumberAxis("X Distance (km)");
        NumberAxis yAxis=new NumberAxis("Y Distance (km)");
        XYItemRenderer itemrenderer = new XYLineAndShapeRenderer(true,false);

        final XYPlotWithZoomableBackgroundImage myplot = new XYPlotWithZoomableBackgroundImage((XYDataset)results,inputPanel,wChartPanel,hChartPanel,vicX,vicY,
                xAxis, yAxis, itemrenderer, true);// final
        JFreeChart chart = new JFreeChart("Manhattan Grid for SEAMCAT", JFreeChart.DEFAULT_TITLE_FONT, myplot, true);

        customChartPanel chartPanel = new customChartPanel(chart,wChartPanel,hChartPanel);

        System.out.println("ILRx[1]="+this.vectorILR_X[1]+" ILRy[1]="+this.vectorILR_Y[1]);
        System.out.println("ILR[1] indoor ? "+myplot.getEnvironment(myplot.getPixelPosition(this.vectorILR_X[1],
                this.vectorILR_Y[1])[0],myplot.getPixelPosition(this.vectorILR_X[1],this.vectorILR_Y[1])[1]));
        plotPanel.add(chartPanel,gbc);
        plotPanel.revalidate();
        plotPanel.repaint();


        gbc.gridheight=1;


        /*
        création du dégradé pour la couleur
         */

        for(int i=0; i<100; i++){
            gbc.gridy = i+1;
            gbc.gridx = 1;
            gbc.insets = new Insets(0, 40, 0, 0);
            if(i<=100/2){
                red=(int)(255-i*255*2.0/100);
                green=(int)(i*255*2.0/100);
                blue=0;
            }
            else{
                green=(int)(255*2-i*255*2.0/100);
                blue=(int)(i*255.0/100);
                red=0;
            }
            System.out.println("red="+red+" green="+green+" blue="+blue);
            JPanel legendPanel = new JPanel();
            legendPanel.setMaximumSize(new Dimension(50,50/(maxFloor+1)));
            legendPanel.setMinimumSize(new Dimension(50,50/(maxFloor+1)));
            legendPanel.setPreferredSize(new Dimension(50,50/(maxFloor+1)));
            legendPanel.setBackground(new Color(red,green,blue));
            System.out.println(legendPanel.getBackground());
            plotPanel.add(legendPanel, gbc);
        }

        gbc.insets = new Insets(0, 10, 0, 0);
        //gbc.gridheight = 2;
        /*
        On met le numéro des étages en face de la couleur correspondante
         */
        gbc.gridheight = 1;
        for(int i=0; i<=maxFloor; i++){
            gbc.gridx = 2;
            gbc.gridy = i*100/maxFloor+1;
            plotPanel.add(new JLabel(String.valueOf(maxFloor-i)), gbc);
        }


    }

    /*
    on met les données sous une forme plus exploitable
     */
    private XYDataset createDataset() {
        int length_VLRX = vectorVLR_X.length;
        System.out.println("nb of events: " + length_VLRX);

        final XYSeries VLR = new XYSeries("VLR");
        final XYSeries ILR = new XYSeries("ILR");
        final XYSeries VLT = new XYSeries("VLT");
        final XYSeries ILT = new XYSeries("ILT");

        for (int numevent = 0; numevent < length_VLRX; numevent++) {
            VLR.add(vectorVLR_X[numevent], vectorVLR_Y[numevent]);
            VLT.add(vectorVLT_X[numevent], vectorVLT_Y[numevent]);
            ILR.add(vectorILR_X[numevent], vectorILR_Y[numevent]);
            ILT.add(vectorILT_X[numevent], vectorILT_Y[numevent]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ILT);
        dataset.addSeries(VLT);
        dataset.addSeries(ILR);
        dataset.addSeries(VLR);

        return dataset;

    }

}