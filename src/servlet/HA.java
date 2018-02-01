package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import binpack.BiggestFit;
import binpack.BiggestFitOri;
import binpack.BiggestFitSkip;
import binpack.BiggestFitSkipWithIterFail;
import binpack.Combination;
import binpack.Host;
//import binpack.HostNameComparator;
import binpack.HostVMChart;
import binpack.BiggestFitNew;

public class HA {

	static String work_dir = "/tmp/ha/";
	static String topo_config_subdir = "topo_cfg/";
	static String pic_subdir = "pic/";
	static String combination_subdir = "combination/";
	
	public static void main(String[] args) {
		String[] ay = listConfigs();
		for(String s : ay) {
			System.out.println(s);
		}
		
//		getMaxFailureTolerance("HA-16-hosts-128-VMs-same-memory-64-protect-VMs");
//		writeCombination("HA-32-hosts-128-VMs-same-memory-64-protect-VMs");
//		verifyCombination("HA-32-hosts-128-VMs-same-memory-64-protect-VMs");
//		System.out.println(getExhaustive_result("HA-16-hosts-128-VMs-with-same-memory"));
//		getCombinationResultPic("HA-data-01", 3);
		
		
		genProtectSeriesCompareResultPic("HA-32-hosts-128-VMs-same-memory-64-protect-VMs");//("HA-16-hosts-128-VMs-with-same-memory");
	}

	public static String[] listConfigs() {
		File f = new File(work_dir + topo_config_subdir);
		String[] file_array = f.list();
		Arrays.sort(file_array);
		return file_array;
	}
	
	public static void clean_old_pic(String contain_name) {
		String pic_path = work_dir + pic_subdir;
		String[] files = new File(pic_path).list();
		String old_file = null;
		String pattern = contain_name + "_indicator";
		for(String s: files) {
			if(s.startsWith(pattern)) {
				old_file = s;
				break;
			}
		}
		System.out.println("old_file: " + old_file);
		if(old_file != null)
			new File(pic_path + old_file).delete();
	}
	
	public static int getMaxFailureTolerance(String config_name) {
		BiggestFitNew.clean();
		BiggestFitNew.config_file = work_dir + topo_config_subdir + config_name;
		return BiggestFitNew.test_once();
	}
	
	public static void writeCombination(String config_name) {
		int fail_tolerance = getMaxFailureTolerance(config_name);
		List<String> host_name_list = new ArrayList<String>();
		BiggestFitNew.sort_host_list_by_name();
		for(Host h : BiggestFitNew.hostList) {
			host_name_list.add(h.name);
		}
		Combination.combinations_to_file(host_name_list, fail_tolerance, work_dir + combination_subdir + config_name);
	}
	
	public static boolean verifyCombination(String config_name) {
		File f = new File(work_dir + combination_subdir + config_name);
		if(!f.exists()) {
			System.err.printf("Combination file : %s not exist!", f.toPath());
			return false;
		}
		BiggestFitNew.config_file = work_dir + topo_config_subdir + config_name;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String str;
			while((str = br.readLine()) != null) {
//				System.out.println(str);
				String[] ay = str.split(",");
				List<String> host_list = new ArrayList<String>();
				for(String s : ay) {
					host_list.add(s);
				}
				
				BiggestFitNew.simulate_hosts_fail_prepare();

				if(!BiggestFitNew.simulate_hosts_fail(host_list)) {
					System.err.printf("Simulate fail: %s\n", str);
					FileWriter fw = new FileWriter(f + "_result_fail");
					fw.write(str);
					fw.close();
					return false;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FileWriter fw;
		try {
			fw = new FileWriter(f + "_result_success");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String getExhaustive_result(String config_name) {
		File f = new File(work_dir + combination_subdir);
		String[] files = f.list();
		for(String file_name: files) {
			System.out.println(file_name);
			if(file_name.contains(config_name) && !file_name.equals(config_name)) {
				if(file_name.contains("success"))
					return "All combination cases verified and passed";
				else if(file_name.contains("fail"))
					return "Verify failed!";
				else
					return "Verification in progress ......";
			}
		}
		return "Verification in progress ......";
	}
	
	public static int getExhaustive_cnt(String config_name) {
		int cnt = 0;
		File f = new File(work_dir + combination_subdir + config_name);
		if(f.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				while((br.readLine()) != null) {
					cnt++;
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.printf("File : %s not exists!\n", f.getPath());
			return -1;
		}
		return cnt;
	}
	
	public static String getCombinationResultPic(String config_name, int case_index) {
		File f = new File(work_dir + combination_subdir + config_name);
		if(!f.exists()) {
			System.err.printf("Combination file : %s not exist!", f.toPath());
			return null;
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String str;
			while((str = br.readLine()) != null) {
				if(--case_index == 0) {
					br.close();
					System.out.println(str);					
					String[] ay = str.split(",");
					List<String> host_list = new ArrayList<String>();
					for(String s : ay) {
						host_list.add(s);
					}

					BiggestFitNew.simulate_hosts_fail_prepare();
					BiggestFitNew.simulate_hosts_fail(host_list);
					
					String pic_name = config_name + "_combination_" + case_index;
					clean_old_pic(pic_name);
					String pic_path = work_dir + pic_subdir;
					
					String post_fix = "_indicator.png_" + new Date().getTime();
					String output_pic_path = pic_path + pic_name + post_fix;
					
					String ret_path = "data/" + pic_subdir + pic_name +  post_fix;
//					SavePicture.SavePicture_AllDaysWithNDays(sk, Util.getSkName(sk) + " - " + date, nDays, Data.sk_yahoo_path + sk.substring(2) + ".cvs", nDays*10/*1200*/,800, pic_path);

					HostVMChart.genCombinationResultPic(work_dir + topo_config_subdir + config_name, output_pic_path);
					System.out.println(ret_path);
					return ret_path;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String genLoadTopoConfigPic(String config_name) {
		BiggestFitNew.clean();
		String pic_name = config_name + "_cfg";
		clean_old_pic(pic_name);
		String pic_path = work_dir + pic_subdir;
		
		String post_fix = "_indicator.png_" + new Date().getTime();
		String output_pic_path = pic_path + pic_name + post_fix;
		
		String ret_path = "data/" + pic_subdir + pic_name +  post_fix;
//		SavePicture.SavePicture_AllDaysWithNDays(sk, Util.getSkName(sk) + " - " + date, nDays, Data.sk_yahoo_path + sk.substring(2) + ".cvs", nDays*10/*1200*/,800, pic_path);

		HostVMChart.genLoadTopoConfigPic(work_dir + topo_config_subdir + config_name, output_pic_path);
		return ret_path;
	}

	public static String genTopoResultPic(String config_name) {
		BiggestFitNew.clean();
		String pic_name = config_name + "_result";
		clean_old_pic(pic_name);
		String pic_path = work_dir + pic_subdir;

		String post_fix = "_indicator.png_" + new Date().getTime();
		String output_pic_path = pic_path + pic_name + post_fix;

		String ret_path = "data/" + pic_subdir + pic_name +  post_fix;
//		SavePicture.SavePicture_AllDaysWithNDays(sk, Util.getSkName(sk) + " - " + date, nDays, Data.sk_yahoo_path + sk.substring(2) + ".cvs", nDays*10/*1200*/,800, pic_path);
		
		HostVMChart.genTopoResultPic(work_dir + topo_config_subdir + config_name, output_pic_path);
		return ret_path;
	}
	

	public static String genProtectSeriesCompareResultPic(String config_name) {
		BiggestFitOri.clean();
		BiggestFit.config_file = work_dir + topo_config_subdir + config_name;
		String pic_name = config_name + "_compare";
		clean_old_pic(pic_name);
		String pic_path = work_dir + pic_subdir;

		String post_fix = "_indicator.png_" + new Date().getTime();
		String output_pic_path = pic_path + pic_name + post_fix;

		String ret_path = "data/" + pic_subdir + pic_name +  post_fix;
		
		// gen existing algorithm result
		List<Integer> existing_algorithm_result_list = new ArrayList<Integer>();
		BiggestFitOri.genMaxToleranceBasedOnOrder(existing_algorithm_result_list);
		
		// gen BiggestFitSkip algorithm result
		BiggestFitSkip.clean();
		List<Integer> new_algorithm_skip_result_list = new ArrayList<Integer>();
		BiggestFitSkip.genMaxToleranceBasedOnOrder(new_algorithm_skip_result_list);
		
		
		BiggestFitNew.clean();
		// gen new algorithm result
		List<Integer> new_algorithm_result_list = new ArrayList<Integer>();
		BiggestFitNew.genMaxToleranceBasedOnOrder(new_algorithm_result_list);
		
		try {
			String xrange = String.format("set xrange [0:%d]\n", BiggestFitNew.protect_vm_list.size());
			String yrange = String.format("set yrange [0:%d]\n", BiggestFitNew.hostList.size());
			String output = String.format("set output '%s.png'\n", config_name);
			String key_point = String.format("set key at %d, %d\n", (int)(BiggestFitNew.protect_vm_list.size() * 0.8),
					(int)(BiggestFitNew.hostList.size() * 0.8));
			String dat_file_name = String.format("%s.dat", config_name);
			String plot_cmd = String.format("plot \"%s\" using 1:2 title \"Reported tolerance with code change\" with linespoints lw 1, "
					+ "\"%s\" using 1:3 title \"Reported tolerance\" with linespoints lw 1\n", dat_file_name, dat_file_name);
			
//			File tempFile = new File("/tmp/9/my.p");//File.createTempFile("compare_result", "p");
			File gnu_plot_file = new File(work_dir + pic_subdir + config_name + ".p");;
			FileWriter fw = new FileWriter(gnu_plot_file);
			fw.write("set title \"Reported max host failure tolerance of HA on pool\"\n");
			fw.write("set xlabel \"VMs protected\"\n");
			fw.write(xrange);
			fw.write("set xtics\n");
			fw.write("set ylabel \"Hosts\"\n");
			fw.write(yrange);
			fw.write("set terminal png truecolor size 1240,900\n");
			fw.write(output);
//			fw.write("set key at 120, 30\n");
			fw.write(key_point);
			fw.write(plot_cmd);
			
			fw.close();
			
			// gen data file
			int len = existing_algorithm_result_list.size();
			FileWriter fw_dat = new FileWriter(work_dir + pic_subdir + config_name + ".dat");
			String s = String.format("0\t%d\t%d\n", BiggestFitNew.hostList.size(), BiggestFitNew.hostList.size());
			fw_dat.write(s);
			for(int i = 1; i <= len; i++) {
				s = String.format("%d\t%d\t%d\n", i, new_algorithm_result_list.get(i-1), existing_algorithm_result_list.get(i-1));
				fw_dat.write(s);
			}
			fw_dat.close();
			
			// run gnuplot
//			 Process process = new ProcessBuilder(
//                     "cd " + work_dir + pic_subdir  + " && " +  "gnuplot", config_name + ".p").start();
//			 process.waitFor();
//			String[] cmd = { "/bin/sh", "-c", "cd /var; ls -l" };
			String[] cmd = { "/bin/sh", "-c", "cd " +  work_dir + pic_subdir + "; " + "gnuplot " +  config_name + ".p"};
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
//			 process = new ProcessBuilder(
//                     "mv","*.png", work_dir + pic_subdir).start();
//			 process.waitFor();
			return "data/" + pic_subdir + config_name + ".png";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
