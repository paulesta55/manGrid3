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


public class EppUI extends JFrame implements PostProcessingUI {
    private Panels panels;
    private JSplitPane split;
    private PlotPanel plotPanel = new PlotPanel();
    private XYPlot gridXYplot = new XYPlot();
    private XYDataset results;
    private JFreeChart gridChart;
    private ChartPanel gridPanel;
    private EppUIInput inputPanel;
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

    //IL FAUT TROUVER UN MOYEN D'ADAPTER LA FENETRE AUTOMATIQUEMENT.
    private int wChartPanel=500;
    private int hChartPanel=500;


    public void buildUI(Scenario scenario, JPanel canvas, Panels panels) {
        /*
        Cette méthode construit l'IHM en assemblant à la fois des panels statiques et les panels dynamiques créé après l'appuie sur
        un bouton postProcessing(Cf plus loin)
        Ici plotPanel est un contenu "dynamique" et Paramètres correspond à l'interface EppUIInput implémenté tel que le
        demande Seamcat (Cf EppUIInput)
         */
        this.panels = panels;
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.add(panels.get("Paramètres").getPanel());
        split.add(plotPanel);
        canvas.setLayout(new BorderLayout());
        canvas.add(split, BorderLayout.CENTER);
    }

    public String getTitle() {
        return "Manhattan Grid";
    }

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

    @PostProcessing(order = 2, name = "Plot")
    /*Cette annotation crée le bouton "Plot" qui va déclencher le "post processing".
    Ici le post processing déclenche la méthode preparePlot.
    */
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


    private void plot(Scenario scenario) {
        /*
        Ici est créé le contenu dynamique ajouté au split dans la méthode buildUI.
        Ce contenu est placé dans un JPanel customisé (Cf class PlotPanel) qui est lui-même ajouté au plit de la méthode buildUI
        On utilise le gridBaglayout par facilité d'utilisation
         */

        plotPanel.removeAll();

        results = createDataset();

        gridXYplot.setDataset(results);//crée les données à afficher sur la grille
        gridChart = ChartFactory.createScatterPlot("Manhattan Grid","axis x","axis y",results);

        gridPanel = new ChartPanel(gridChart);
        gridPanel.setOpaque(false);
        gridChart.setBackgroundPaint(null);
        gridChart.getPlot().setBackgroundPaint(null);
        gridChart.getLegend().setBackgroundPaint(new Color(200, 200, 200, 100));
        gridChart.getTitle().setPaint(Color.white);

        JLabel label = new JLabel("Hello World!");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setForeground(Color.yellow);
        label.setOpaque(false);

        gridChart.setBorderVisible(true);

        NumberAxis xAxis=new NumberAxis("X Distance (km)");
        NumberAxis yAxis=new NumberAxis("Y Distance (km)");
        XYItemRenderer itemrenderer = new XYLineAndShapeRenderer(true,false);

        /*
        On peut ensuite utiliser un XYplot sur lequel on applique backgroundImage en fond à l'aide de
        de setBackgroundImage (hérité de plot et utilisé dans customChartPanel)
        JFreeChart correspond au graphe en lui-même qui ne peut être créé que si myplot a été initialisé (sans backgroundimage)
        customChartPanel correspond au conteneur du graphe
        */
        final XYPlotWithZoomableBackgroundImage myplot = new XYPlotWithZoomableBackgroundImage((XYDataset)results,inputPanel,wChartPanel,hChartPanel,vicX,vicY,
                xAxis, yAxis, itemrenderer, true);// final
        JFreeChart chart = new JFreeChart("Manhattan Grid for SEAMCAT", JFreeChart.DEFAULT_TITLE_FONT, myplot, true);
        customChartPanel chartPanel = new customChartPanel(chart,wChartPanel,hChartPanel);

        plotPanel.add(chartPanel);
        plotPanel.revalidate();
        plotPanel.repaint();

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        plotPanel.setLayout(gridBagLayout);
        gbc.fill = GridBagConstraints.CENTER;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 2;
        plotPanel.revalidate();
        plotPanel.repaint();

    }

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