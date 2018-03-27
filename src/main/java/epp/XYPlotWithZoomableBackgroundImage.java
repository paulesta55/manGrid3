package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Align;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public class XYPlotWithZoomableBackgroundImage extends XYPlot implements Zoomable
{

    private boolean zoomableBackgroundImage;
    private Grid grid;
    private int wChartPanel;
    private int hChartPanel;
    private double init_lowXaxis, init_upXaxis, init_lowYaxis, init_upYaxis;

    public XYPlotWithZoomableBackgroundImage(XYDataset dataset,
                                             EppUIInput inputPanel,
                                             int wChartPanel,
                                             int hChartPanel,
                                             double vicX,
                                             double vicY,
                                             ValueAxis domainAxis,
                                             ValueAxis rangeAxis,
                                             XYItemRenderer renderer,
                                             boolean zoomableBackgroundImage
                                            )
    {
        super(dataset, domainAxis, rangeAxis, renderer);
        this.zoomableBackgroundImage = zoomableBackgroundImage;
        ValueAxis xAxis = getDomainAxis();
        ValueAxis yAxis = getRangeAxis();
        this.init_lowXaxis=xAxis.getLowerBound();
        this.init_lowYaxis=yAxis.getLowerBound();
        this.init_upXaxis=xAxis.getUpperBound();
        this.init_upYaxis=yAxis.getUpperBound();
        this.wChartPanel=wChartPanel;
        this.hChartPanel=hChartPanel;

        //Computing grid size with the Axis and the ChartPanel size

        //range (km => meter)
        double xrange = 1000.0*(xAxis.getUpperBound()-xAxis.getLowerBound());
        double yrange = 1000.0*(yAxis.getUpperBound()-yAxis.getLowerBound());
        //wChartPanel, hChartPanel (pixel)
        double xpixelpermeter = wChartPanel/xrange;
        double ypixelpermeter = hChartPanel/yrange;

        int testvicX = (int)Math.round(wChartPanel*Math.abs(xAxis.getLowerBound())/(xAxis.getUpperBound()-xAxis.getLowerBound()));
        int testvicY = (int)Math.round(hChartPanel*Math.abs(yAxis.getLowerBound())/(yAxis.getUpperBound()-yAxis.getLowerBound()));
        //System.out.println(testvicX+" "+testvicY);
        this.grid=new Grid(inputPanel,xpixelpermeter,ypixelpermeter,(int) testvicX, (int)testvicY);
        grid.repaint();
        BufferedImage bi = grid.takeSnapShot();
        setBackgroundImage(bi);

    }

    /**
     * Draws the background image (if there is one) aligned within the
     * specified area.
     *
     * @param g2 the graphics device.
     * @param area the area.
     *
     * @see #getBackgroundImage()
     * @see #getBackgroundImageAlignment()
     * @see #getBackgroundImageAlpha()
     */

    public void drawBackgroundImage(Graphics2D g2, Rectangle2D area)
    {

        // if background image is not zoomable call Plot.drawBackgroundImage(g2,area);
        if(!zoomableBackgroundImage)
        {
            super.drawBackgroundImage(g2,area);
            System.out.println("not zoomable");
        }

        else {

            //Set the ManhattanGrid as Background image
            Image backgroundImage = super.getBackgroundImage();
            float backgroundAlpha = super.getBackgroundAlpha();
            int backgroundImageAlignment = super.getBackgroundImageAlignment();

            if (backgroundImage != null)   // make sure there is a background image
            {

                Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, backgroundAlpha));

                //Creat a new Rectangle to draw the grid(initially same size as the Manhattan Grid, cause no zoom)
                Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, backgroundImage.getWidth(null), backgroundImage.getHeight(null));
                //System.out.println("dx="+dest.getX()+" dy="+dest.getY()+" dw="+dest.getWidth()+" dh="+dest.getHeight());
                Align.align(dest, area, backgroundImageAlignment);
                //System.out.println("dx="+dest.getX()+" dy="+dest.getY()+" dw="+dest.getWidth()+" dh="+dest.getHeight()+" areax="+area.getX()+" areay="+area.getY()+" areaw="+area.getWidth()+" areah="+area.getHeight());
                //Calculate the initial chart size
                double chart_w = Math.abs(init_lowXaxis) + Math.abs(init_upXaxis);      //chart abs. width
                double chart_h = Math.abs(init_lowYaxis) + Math.abs(init_upYaxis);      //chart abs. heigth
                //Manhattan Grid(Background) Size
                float background_w = backgroundImage.getWidth(null);          //width
                float background_h = backgroundImage.getHeight(null);         //heigth
                //get X Axis & Y Axis
                ValueAxis xAxis = getDomainAxis();
                ValueAxis yAxis = getRangeAxis();

                float x_min_rat = Math.abs((float) ((init_lowXaxis - xAxis.getLowerBound()) / (chart_w / background_w)));
                float x_max_rat = Math.abs((float) ((init_upXaxis - xAxis.getUpperBound()) / (chart_w / background_w)));
                float y_min_rat = Math.abs((float) ((init_lowYaxis - yAxis.getLowerBound()) / (chart_h / background_h)));
                float y_max_rat = Math.abs((float) ((init_upYaxis - yAxis.getUpperBound()) / (chart_h / background_h)));


                ((Graphics) g2).drawImage(backgroundImage,
                        (int) dest.getX(),                          //dest x1 corner1
                        (int) dest.getY(),                          //dest y1
                        (int) (dest.getX() + dest.getWidth()),   //dest x2 corner2
                        (int) (dest.getY() + dest.getHeight()), //dest y2 corner
                        (int) Math.round(x_min_rat),                //src  x1 corner1
                        (int) Math.round(y_max_rat),                //src  y1
                        (int) Math.round(background_w - (x_max_rat)),         //src  x2 corner2
                        (int) Math.round(background_h - (y_min_rat)),         //src  y2
                        null);

                g2.setComposite(originalComposite);

            }
        }
    }

}


