package binpack;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class HostVMChart extends ApplicationFrame {
	public HostVMChart(String paramString) {
		super(paramString);
		JPanel localJPanel = createDemoPanel();
		localJPanel.setPreferredSize(new Dimension(1800, 1200));
		setContentPane(localJPanel);
	}

	static int series_normal_cnt = 0;
	static int series_protect_cnt = 0;
	static int series_move_cnt = 0;
	static int max_fail_tolerance = 0;
	
	static CategoryDataset createDataset_from_cfg() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		BiggestFit.loadConfig();
		BiggestFit.load_protect_config();
		BiggestFitNew.setProtectVMs_ForUI();
		Collections.sort(BiggestFit.hostList, new HostNameComparator());
		
		
//		TestBiggestFit.test();  // deprecate
//		max_fail_tolerance = TestBiggestFit.test_once();
//		Collections.sort(TestBiggestFit.hostList, new HostLeftSizeComparator());
		
		List<String> host_vm_size_list = new ArrayList<String>();
		series_normal_cnt = BiggestFitNew.getVMListForUI(host_vm_size_list, false);
		
		int index = 0;
		for(String s : host_vm_size_list) {
//			System.out.println(s);
			String[] ay = s.split("_");
			index = index % series_normal_cnt;
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index++;
		}
		
		index = 0;
		host_vm_size_list.clear();
		series_protect_cnt = BiggestFitNew.getVMListForUI(host_vm_size_list, true);
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			index = index % series_protect_cnt;
			index += series_normal_cnt;
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index -= series_normal_cnt;
			index++;
		}
		
		index = series_normal_cnt + series_protect_cnt;
		host_vm_size_list.clear();
		BiggestFitNew.getHostFreeMemListForUI(host_vm_size_list);
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
		}
	    
		return dataset;
	}
	
	
	static CategoryDataset createDataset_last_result() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
//		BiggestFitNew.loadConfig();
//		BiggestFitNew.load_protect_config();
//		BiggestFitNew.setProtectVMs_ForUI();
//		Collections.sort(TestBiggestFit.hostList, new HostNameComparator());

//		BiggestFitNew.test();
		max_fail_tolerance = BiggestFitNew.test_once();
		Collections.sort(BiggestFitNew.hostList, new HostLeftSizeComparator());
		
		List<String> host_vm_size_list = new ArrayList<String>();
		series_normal_cnt = BiggestFitNew.getVMListForUI(host_vm_size_list, false);

		int index = 0, host_cnt =0;
		for(String s : host_vm_size_list) {
//			System.out.println(s);
			String[] ay = s.split("_");
			index = index % series_normal_cnt;
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index++;
		}
		
		index = 0;
		host_vm_size_list.clear();
		series_protect_cnt = BiggestFitNew.getVMListForUI(host_vm_size_list, true);
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			index = index % series_protect_cnt;
			index += series_normal_cnt;
			
//			if(host_cnt++ < max_fail_tolerance)
//				dataset.addValue(0, String.valueOf(index), ay[0]);	// don't show failed host
//			else
				dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index -= series_normal_cnt;
			index++;
		}
		
		index = series_normal_cnt + series_protect_cnt;
		host_vm_size_list.clear();
		BiggestFitNew.getHostFreeMemListForUI(host_vm_size_list);
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
		}
	    
		return dataset;
	}
	
	static CategoryDataset createDataset_simulate_fail() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
//		TestBiggestFit.loadConfig();
//		TestBiggestFit.load_protect_config();
//		TestBiggestFit.setProtectVMs_ForUI();
//		Collections.sort(TestBiggestFit.hostList, new HostNameComparator());

//		TestBiggestFit.test();
//		max_fail_tolerance = TestBiggestFit.test_once();
//		TestBiggestFit.simulate_fail(2);
		Collections.sort(BiggestFitNew.hostList, new HostNameComparator());

		List<String> host_vm_size_list = new ArrayList<String>();
		series_normal_cnt = BiggestFitNew.getVMListForUI(host_vm_size_list, false);

		int index = 0;
		for(String s : host_vm_size_list) {
//			System.out.println(s);
			String[] ay = s.split("_");
			index = index % series_normal_cnt;
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index++;
		}
		
		index = 0;
		host_vm_size_list.clear();
		series_protect_cnt = BiggestFitNew.getVMListForUI(host_vm_size_list, true);
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			index = index % series_protect_cnt;
			index += series_normal_cnt;
			
//			if(host_cnt++ < max_fail_tolerance)
//				dataset.addValue(0, String.valueOf(index), ay[0]);	// don't show failed host
//			else
				dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index -= series_normal_cnt;
			index++;
		}
		
		// get Moved vm
		index = 0;
		host_vm_size_list.clear();
		series_move_cnt = BiggestFitNew.getMovedVMListForUI(host_vm_size_list);
//		index = series_normal_cnt + series_protect_cnt;
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			index = index % series_move_cnt;
			index = index + series_normal_cnt + series_protect_cnt;
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
			index = index - series_normal_cnt - series_protect_cnt;
			index++;
		}
		
		// get free memory
		index = series_normal_cnt + series_protect_cnt + series_move_cnt;
		host_vm_size_list.clear();
		BiggestFitNew.getHostFreeMemListForUI(host_vm_size_list);
		for(String s : host_vm_size_list) {
			String[] ay = s.split("_");
			dataset.addValue(Integer.parseInt(ay[1]), String.valueOf(index), ay[0]);
		}
	    
		return dataset;
	}
	
	private static CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(197.0D, "N_VM_1", "Host1");
		dataset.addValue(64.0D, "N_VM_2", "Host1");
		dataset.addValue(57.0D, "Protect_VM", "Host1");
		dataset.addValue(339.0D, "N_VM_1", "Host2");
		dataset.addValue(30.0D, "N_VM_2", "Host2");
		dataset.addValue(4.0D, "Protect_VM", "Host2");
		dataset.addValue(279.0D, "N_VM_1", "Host3");
		dataset.addValue(27.0D, "N_VM_2", "Host3");
		dataset.addValue(107.0D, "Protect_VM", "Host3");
		dataset.addValue(92.0D, "N_VM_1", "Host4");
		dataset.addValue(55.0D, "N_VM_2", "Host4");
		dataset.addValue(313.0D, "Protect_VM", "Host4");
		dataset.addValue(96.0D, "N_VM_1", "Host5");
		dataset.addValue(102.0D, "N_VM_2", "Host5");
		dataset.addValue(337.0D, "Protect_VM", "Host5");
		dataset.addValue(403.0D, "N_VM_1", "Host6");
		dataset.addValue(82.0D, "N_VM_2", "Host6");
		dataset.addValue(60.0D, "Protect_VM", "Host6");

		return dataset;
	}

	static Color normalVMColor = new Color(0, 55, 122);
	static Color protectVMColor = new Color(0xe6, 0x55, 0x0d);
	static Color moveVMColor = new Color(0xc5, 0xb0, 0xd5);
	static Color freeMemColor = new Color(0x2c, 0xa0, 0x2c);
	
	private static JFreeChart createChart(CategoryDataset paramCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("", "Hosts",
				"Memory", paramCategoryDataset,
				PlotOrientation.VERTICAL,     // the plot orientation
	            false,                         // include legend
	            false,                         // tooltips
	            false                         // urls
				);
//		localJFreeChart.addSubtitle(new TextTitle("Orange: Protect VM            Dark blue: Normal VM"));
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();
		CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
		localCategoryAxis.setLowerMargin(0.01D);
//		localCategoryAxis.setUpperMargin(0.01D);
//		Color normalVMColor = new Color(0, 55, 122);
//		localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
//		AttributedString localAttributedString = new AttributedString("m3/person/year");
//		localAttributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
//		localAttributedString.addAttribute(TextAttribute.SIZE, Integer.valueOf(14));
//		localAttributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 1, 2);
//		localCategoryPlot.getRangeAxis().setAttributedLabel(localAttributedString);
		StackedBarRenderer render = (StackedBarRenderer) localCategoryPlot.getRenderer();
		render.setDrawBarOutline(true);
		render.setBarPainter(new StandardBarPainter());
		render.setBaseItemLabelsVisible(true);
		render.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		render.setBaseItemLabelPaint(Color.WHITE);
		
		for(int i = 0; i < series_normal_cnt; i++)
			render.setSeriesPaint(i, normalVMColor);
		for(int i = 0; i < series_protect_cnt; i++)
			render.setSeriesPaint(i + series_normal_cnt, protectVMColor/*Color.ORANGE*/);
		render.setSeriesPaint(series_normal_cnt + series_protect_cnt, freeMemColor/*Color.GREEN*/);
//		render.setSeriesPaint(2, Color.ORANGE);

	    	    
		return localJFreeChart;
	}

	private static JFreeChart createChartLastResult(CategoryDataset paramCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("", "Hosts",
				"Memory", paramCategoryDataset,
				PlotOrientation.VERTICAL,     // the plot orientation
	            false,                         // include legend
	            false,                         // tooltips
	            false                         // urls
				);
//		localJFreeChart.addSubtitle(new TextTitle("Orange: Protected VM            Dark blue: Normal VM"));
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();
		CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
		localCategoryAxis.setLowerMargin(0.01D);
//		localCategoryAxis.setUpperMargin(0.01D);
		Color normalVMColor = new Color(0, 55, 122);
//		localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
//		AttributedString localAttributedString = new AttributedString("m3/person/year");
//		localAttributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
//		localAttributedString.addAttribute(TextAttribute.SIZE, Integer.valueOf(14));
//		localAttributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 1, 2);
//		localCategoryPlot.getRangeAxis().setAttributedLabel(localAttributedString);
		StackedBarRenderer render = (StackedBarRenderer) localCategoryPlot.getRenderer();
		render.setDrawBarOutline(true);
		render.setBarPainter(new StandardBarPainter());
		render.setBaseItemLabelsVisible(true);
		render.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		render.setBaseItemLabelPaint(Color.WHITE);
		
		for(int i = 0; i < series_normal_cnt; i++)
			render.setSeriesPaint(i, normalVMColor);
		for(int i = 0; i < series_protect_cnt; i++)
			render.setSeriesPaint(i + series_normal_cnt, protectVMColor);
		render.setSeriesPaint(series_normal_cnt + series_protect_cnt, freeMemColor);
//		render.setSeriesPaint(2, Color.ORANGE);
		

		// add marker
		
		// marker for fail hosts
		for(int i = 0; i < max_fail_tolerance; i++) {
//		    CategoryMarker localCategoryMarker = new CategoryMarker("host03", new Color(0, 0, 255, 25), new BasicStroke(2.0F));
			CategoryMarker localCategoryMarker = new CategoryMarker(paramCategoryDataset.getColumnKey(i));//(TestBiggestFit.hostList.get(i).name);//("host03");		
		
		    localCategoryMarker.setLabel("Fail");
		    localCategoryMarker.setLabelPaint(Color.red);
		    
		    localCategoryMarker.setPaint(new Color(21, 255, 213, 128));
		    localCategoryMarker.setAlpha(0.9F);
		    localCategoryMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		    localCategoryMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		    localCategoryMarker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
		    
		    localCategoryPlot.addDomainMarker(localCategoryMarker, Layer.BACKGROUND);			
		}
		// marker for hosts which contain most VMs
		Collections.sort(BiggestFitNew.hostList, new HostPlaceVMSizeComparator());
		for(int i = 0; i < max_fail_tolerance; i++) {
			
			CategoryMarker localCategoryMarker = new CategoryMarker(BiggestFitNew.hostList.get(i).name);//(TestBiggestFit.hostList.get(i).name);//("host03");		
			
		    localCategoryMarker.setLabel("Move");
		    localCategoryMarker.setLabelPaint(Color.blue);
		    
		    localCategoryMarker.setPaint(new Color(215, 205, 253, 128));
		    localCategoryMarker.setAlpha(0.8F);
		    localCategoryMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		    localCategoryMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		    localCategoryMarker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
		    
		    localCategoryPlot.addDomainMarker(localCategoryMarker, Layer.BACKGROUND);	
		}
	    	    
		return localJFreeChart;
	}
	
	private static JFreeChart createChartSimulateFail(CategoryDataset paramCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createStackedBarChart("", "Hosts",
				"Memory", paramCategoryDataset,
				PlotOrientation.VERTICAL,     // the plot orientation
	            false,                         // include legend
	            false,                         // tooltips
	            false                         // urls
				);
//		localJFreeChart.addSubtitle(new TextTitle("Orange: Protect VM            Dark blue: Normal VM"));
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();
		CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
		localCategoryAxis.setLowerMargin(0.01D);
//		localCategoryAxis.setUpperMargin(0.01D);
		Color normalVMColor = new Color(0, 55, 122);
//		localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
//		AttributedString localAttributedString = new AttributedString("m3/person/year");
//		localAttributedString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
//		localAttributedString.addAttribute(TextAttribute.SIZE, Integer.valueOf(14));
//		localAttributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 1, 2);
//		localCategoryPlot.getRangeAxis().setAttributedLabel(localAttributedString);
		StackedBarRenderer render = (StackedBarRenderer) localCategoryPlot.getRenderer();
		render.setDrawBarOutline(true);
		render.setBarPainter(new StandardBarPainter());
		render.setBaseItemLabelsVisible(true);
		render.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		render.setBaseItemLabelPaint(Color.WHITE);
		
		for(int i = 0; i < series_normal_cnt; i++)
			render.setSeriesPaint(i, normalVMColor);
		for(int i = 0; i < series_protect_cnt; i++)
			render.setSeriesPaint(i + series_normal_cnt, protectVMColor);
		for(int i = 0; i < series_move_cnt; i++)
			render.setSeriesPaint(i + series_normal_cnt + series_protect_cnt, moveVMColor);

		render.setSeriesPaint(series_normal_cnt + series_protect_cnt + series_move_cnt, freeMemColor);
//		render.setSeriesPaint(2, Color.ORANGE);
		

		// add marker
		// marker for fail hosts
		for(int i = 0; i < max_fail_tolerance; i++) {
//		    CategoryMarker localCategoryMarker = new CategoryMarker("host03", new Color(0, 0, 255, 25), new BasicStroke(2.0F));
			CategoryMarker localCategoryMarker = new CategoryMarker(BiggestFitNew.simulate_fail_hostList.get(i));//(TestBiggestFit.hostList.get(i).name);//("host03");		
		
		    localCategoryMarker.setLabel("Fail");
		    localCategoryMarker.setLabelPaint(Color.red);
		    
		    localCategoryMarker.setPaint(new Color(21, 255, 213, 128));
		    localCategoryMarker.setAlpha(0.9F);
		    localCategoryMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
		    localCategoryMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		    localCategoryMarker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
		    
		    localCategoryPlot.addDomainMarker(localCategoryMarker, Layer.BACKGROUND);			
		}		

	    	    
		return localJFreeChart;
	}
	
	public static JPanel createDemoPanel() {
//		JFreeChart localJFreeChart = createChart(createDataset());
//		JFreeChart localJFreeChart = createChart(createDataset_from_cfg());
//		JFreeChart localJFreeChart = createChartLastResult(createDataset_last_result());
		JFreeChart localJFreeChart = createChartSimulateFail(createDataset_simulate_fail());
		 // savePictureToDisk
        OutputStream out;
		try {
			out = new FileOutputStream("/tmp/9/a.png");
	        ChartUtilities.writeChartAsPNG(out,
	        		localJFreeChart,
	        		1400, 800);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ChartPanel(localJFreeChart);
	}

	public static void main(String[] paramArrayOfString) {
		HostVMChart localStackedBarChartDemo1 = new HostVMChart("");
		localStackedBarChartDemo1.pack();
		RefineryUtilities.centerFrameOnScreen(localStackedBarChartDemo1);
		localStackedBarChartDemo1.setVisible(true);
	}
	
	/***
	 *  For Web UI
	 */
	public static void genLoadTopoConfigPic(String config_name, String output_pic_path) {
		BiggestFit.config_file = config_name;
		JFreeChart chart = createChart(createDataset_from_cfg());
		// savePictureToDisk
        OutputStream out;
		try {
			int host_num = BiggestFitNew.hostList.size();
			int vm_num = BiggestFitNew.vmList.size();
			out = new FileOutputStream(output_pic_path);
	        ChartUtilities.writeChartAsPNG(out,
	        		chart,
	        		host_num*70, (int)(vm_num/2048.0 + 1)*(host_num * 30));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// createDataset_last_result
	public static void genTopoResultPic(String config_name, String output_pic_path) {
		BiggestFitNew.config_file = config_name;
		JFreeChart chart = createChartLastResult(createDataset_last_result());
		// savePictureToDisk
        OutputStream out;
		try {
			int host_num = BiggestFitNew.hostList.size();
			int vm_num = BiggestFitNew.vmList.size();
			out = new FileOutputStream(output_pic_path);
	        ChartUtilities.writeChartAsPNG(out,
	        		chart,
	        		host_num*70, (int)(vm_num/2048.0 + 1)*(host_num * 30));
	        		//1400, 800);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void genCombinationResultPic(String config_name, String output_pic_path) {
		BiggestFitNew.config_file = config_name;
		JFreeChart chart = createChartSimulateFail(createDataset_simulate_fail());
		// savePictureToDisk
        OutputStream out;
		try {
			int host_num = BiggestFitNew.hostList.size();
			int vm_num = BiggestFitNew.vmList.size();
			out = new FileOutputStream(output_pic_path);
	        ChartUtilities.writeChartAsPNG(out,
	        		chart,
	        		host_num*70, (int)(vm_num/2048.0 + 1)*(host_num * 30));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
