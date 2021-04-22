package utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.Color.BLACK;


public class VisualiseImprovement extends JFrame {

    XYSeriesCollection dataset = new XYSeriesCollection();

    public VisualiseImprovement(String instance, List<Double> costs) {
        addSeries(costs);
        initUI(instance);
    }

    private void initUI(String title) {
        JFreeChart chart = createChart(title, dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addSeries(List<Double> costs) {

        XYSeries series = new XYSeries("Solution cost");

        for (int iter = 0; iter < costs.size(); iter++) {
            series.add(iter, costs.get(iter));
        }

        dataset.addSeries(series);
    }

    private JFreeChart createChart(String title, XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Improvement for " + title,
                "Iteration",
                "Percent improvement",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(chart.getTitle().getText(), new Font("Serif", Font.BOLD, 18)));

        return chart;
    }
}
