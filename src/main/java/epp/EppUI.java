package epp;

import epp.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.seamcat.model.Scenario;
import org.seamcat.model.factory.Factory;
import org.seamcat.model.functions.Function;
import org.seamcat.model.functions.Point2D;
import org.seamcat.model.plugin.Config;
import org.seamcat.model.plugin.antenna.ITU_R_F1336_4_rec_2_Input;
import org.seamcat.model.plugin.eventprocessing.PanelDefinition;
import org.seamcat.model.plugin.eventprocessing.Panels;
import org.seamcat.model.plugin.eventprocessing.PostProcessing;
import org.seamcat.model.plugin.eventprocessing.PostProcessingUI;
import org.seamcat.model.plugin.propagation.P1546ver1Input;
import org.seamcat.model.simulation.result.*;
import org.seamcat.model.types.AntennaGain;
import org.seamcat.model.types.InterferenceLink;
import org.seamcat.model.types.PropagationModel;
import org.seamcat.model.types.result.Results;
import org.seamcat.model.types.result.ScatterDiagramResultType;
import org.seamcat.model.types.result.VectorResult;
import java.lang.Math;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static org.seamcat.model.factory.Factory.build;
import static org.seamcat.model.factory.Factory.results;
import static org.seamcat.model.factory.Factory.when;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RefineryUtilities;



public class EppUI extends JFrame implements PostProcessingUI{
    private Panels panels;
    private JSplitPane split;
    private JPanel plotPanel=new JPanel(new BorderLayout());
    private EppUIInput inputPanel;
    private boolean iO = false;
    private double[] vectorVLR_X;
    private double[] vectorVLR_Y;
    private double[] vectorVLT_X;
    private double[] vectorVLT_Y;
    private double[] vectorILR_X;
    private double[] vectorILR_Y;
    private double[] vectorILT_X;
    private double[] vectorILT_Y;
    private double maxX,minX;
    private double maxY,minY;
    private double x_size, y_size;
    private GridBagLayout gridBagLayout = new GridBagLayout();


    public void buildUI(Scenario scenario, JPanel canvas, Panels panels){
        this.panels = panels;
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.add(panels.get("Paramètres").getPanel());//Input parameters in the left panel
        split.add(plotPanel);//custom panel in the right panel
        canvas.setLayout( new BorderLayout() );
        canvas.add( split, BorderLayout.CENTER );
    }

    public String getTitle(){
        return "Manhattan Grid";
    }

    public PanelDefinition[] panelDefinitions(){
        return new PanelDefinition[]{new PanelDefinition("Paramètres",EppUIInput.class)};

    }
    public double getMaxTab(double[] tab)
    {
        double max = tab[0];

        for(int i=0; i<tab.length; i++)
        {
            if(tab[i]>max)
                max=tab[i];
        }
        return max;

    }
    //Search the min of a table
    public double getMinTab(double[] tab)
    {
        double min = tab[0];

        for(int i=0; i<tab.length; i++)
        {
            if(tab[i]<min)
                min=tab[i];
        }
        return min;

    }

    @PostProcessing(order=2,name = "plot pattern")
    public void preparePlot(Scenario scenario,Results results, Epp.Input input) {
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
        vectorILR_X= posILR_X.asArray();
        vectorILR_Y = posILR_Y.asArray();
        vectorILT_X= posILT_X.asArray();
        vectorILT_Y= posILT_Y.asArray();



        double maxVLRX=getMaxTab(vectorVLR_X);
        double maxVLTX=getMaxTab(vectorVLT_X);
        double maxILTX=getMaxTab(vectorILT_X);
        double maxILRX=getMaxTab(vectorILR_X);
        double[] tabX={maxILRX,maxILTX,maxVLRX,maxVLTX};
        maxX= getMaxTab(tabX);

        double maxVLRY=getMaxTab(vectorVLR_Y);
        double maxVLTY=getMaxTab(vectorVLT_Y);
        double maxILTY=getMaxTab(vectorILT_Y);
        double maxILRY=getMaxTab(vectorILR_Y);
        double[] tabY={maxILRY,maxILTY,maxVLRY,maxVLTY};
        maxY= getMaxTab(tabY);

        double minVLRX=getMinTab(vectorVLR_X);
        double minVLTX=getMinTab(vectorVLT_X);
        double minILTX=getMinTab(vectorILT_X);
        double minILRX=getMinTab(vectorILR_X);
        double[] tabminX={minILRX,minILTX,minVLRX,minVLTX};
        minX= getMinTab(tabminX);

        double minVLRY=getMinTab(vectorVLR_Y);
        double minVLTY=getMinTab(vectorVLT_Y);
        double minILTY=getMinTab(vectorILT_Y);
        double minILRY=getMinTab(vectorILR_Y);
        double[] tabminY={minILRY,minILTY,minVLRY,minVLTY};
        minY= getMinTab(tabminY);

        x_size=(Math.abs(minX)+maxX)*1.1;
        y_size=(Math.abs(minY)+maxX)*1.1;


        plotPanel.removeAll();

        plotPanel.setLayout(new GridBagLayout());
        plotPanel.add(new JLabel("TOTO"));//Should print TOTO in the middle of the right panel

        plotPanel.repaint();
        plotPanel.revalidate();

    }





    private XYSeriesCollection createDataset()//data used to create a chartPanel
    {
        int length_VLRX = vectorVLR_X.length;
        System.out.println("nb of events: "+length_VLRX);

        final XYSeries VLR = new XYSeries("VLR");
        final XYSeries ILR = new XYSeries("ILR");
        final XYSeries VLT = new XYSeries("VLT");
        final XYSeries ILT = new XYSeries("ILT");

        for(int numevent=0;numevent<length_VLRX;numevent++)
        {
            VLR.add(vectorVLR_X[numevent],vectorVLR_Y[numevent]);
            VLT.add(vectorVLT_X[numevent],vectorVLT_Y[numevent]);
            ILR.add(vectorILR_X[numevent],vectorILR_Y[numevent]);
            ILT.add(vectorILT_X[numevent],vectorILT_Y[numevent]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ILT);
        dataset.addSeries(VLT);
        dataset.addSeries(ILR);
        dataset.addSeries(VLR);

        return dataset;

    }
    private JPanel getResultPanel(){//unused => found on the seamcat website & doesn't work for now
        JPanel resultPanel= new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths=new int[]{0,0,0,0,0,0};
        gridBagLayout.rowHeights=new int[]{0,0,0};
        gridBagLayout.columnWeights=new double[]{0.0,0.0,0.0,0.0,1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights=new double[]{0.0,0.0,Double.MIN_VALUE};
        resultPanel.setLayout(gridBagLayout);

        JSeparator separator = new JSeparator();
        GridBagConstraints gbc_separator = new GridBagConstraints();
        gbc_separator.insets = new Insets(0,0,5,5);
        gbc_separator.gridx = 0;
        gbc_separator.gridy = 0;

        return resultPanel;
    }
}