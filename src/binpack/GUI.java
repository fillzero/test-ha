package binpack;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class GUI extends ApplicationFrame {

	public static void main(String[] args) {
		final GUI demo = new GUI("Stacked Bar Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
	}

	public GUI(final String title) {

        super(title);
        final CategoryDataset dataset = createCategoryDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }
	
//	private CategoryDataset createDataset() {
//        return DemoDatasetFactory.createCategoryDataset();
//    }
	
	public static CategoryDataset createCategoryDataset() {

        double[][] data = new double[][]
            {{10.0, 10.0, 10.0, 10.0},
//             {-5.0, -7.0, 14.0, -3.0},
//             {6.0, 17.0, -12.0, 7.0},
//             {7.0, 15.0, 11.0, 0.0},
//             {-8.0, -6.0, 10.0, -9.0},
//             {9.0, 8.0, 0.0, 6.0},
//             {-10.0, 9.0, 7.0, 7.0},
             {11.0, 11.0, 0.0, 11.0},
             {5.0, 5.0, 11.0, 5.0}};

        return DatasetUtilities.createCategoryDataset("Series ", "Category ", data);

    }
	
	private JFreeChart createChart(final CategoryDataset dataset) {

		final JFreeChart chart = ChartFactory.createStackedBarChart3D("Stacked Bar Chart Demo 1", // chart
																								// title
				"Category", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				true, // legend
				true, // tooltips
				false // urls
		);
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        final CategoryItemRenderer renderer = plot.getRenderer();
//        renderer.setLabelGenerator(new StandardCategoryLabelGenerator());
        renderer.setItemLabelsVisible(true);
        renderer.setPositiveItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        renderer.setNegativeItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
		return chart;

	}

}
