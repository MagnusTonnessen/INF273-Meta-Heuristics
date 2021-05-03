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
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class VisualiseOperatorWeights extends JFrame {

    XYSeriesCollection dataset = new XYSeriesCollection();

    public VisualiseOperatorWeights(String instance, Map<String, List<Double>> probabilities) {
        probabilities.forEach(this::addSeries);
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

    private void addSeries(String name, List<Double> probabilities) {

        XYSeries series = new XYSeries(name);

        for (int iter = 0; iter < probabilities.size(); iter++) {
            series.add(iter * 100, probabilities.get(iter));
        }

        dataset.addSeries(series);
    }

    private JFreeChart createChart(String title, XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Probability of operator for " + title,
                "Iteration",
                "Probability",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        List<Color> colors = Arrays.asList(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.BLACK, Color.MAGENTA);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, colors.get(i));
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(i, false);
        }

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(chart.getTitle().getText(), new Font("Serif", Font.BOLD, 18)));

        return chart;
    }
}
