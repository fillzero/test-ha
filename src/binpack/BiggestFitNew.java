package binpack;

import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiggestFitNew extends BiggestFit {

	public static void main(String[] args) {
//		test();
//		test_once();
//		choose(5, 3);
		List<Integer> new_algorithm_result_list = new ArrayList<Integer>();
		genMaxToleranceBasedOnOrder(new_algorithm_result_list);
	}


	public static void test() {
//		loadConfig();
//		protectAndShowMaxTolerance();
		load_protect_config();
		int protect_size = protect_vm_list.size();
		for(int i = 0; i < protect_size; i++) {
			loadConfig();
			List<String> input_protectList = new ArrayList<String>();
			for(int j = 0; j <= i; j++) 
				input_protectList.add(protect_vm_list.get(j));
			setProtectVMs(input_protectList);
			biggestFit();
			getMaxFailure(i+1);
		}
	}
	
	public static int test_once() {
		loadConfig();
		load_protect_config();
		setProtectVMs(protect_vm_list);
		biggestFit();
		return getMaxFailure(1);
	}
	
	public static void genMaxToleranceBasedOnOrder(List<Integer> retList) {
		load_protect_config();
		int protect_size = protect_vm_list.size();
		for(int i = 0; i < protect_size; i++) {
			loadConfig();
			List<String> input_protectList = new ArrayList<String>();
			for(int j = 0; j <= i; j++) 
				input_protectList.add(protect_vm_list.get(j));
			setProtectVMs(input_protectList);
			biggestFit();
			retList.add(getMaxFailure(i+1));
		}
	}
	
	public static void setProtectVMs_ForUI() {
		int protect_size = protect_vm_list.size();

		
		for(String vm_name : protect_vm_list) {
			VM vm = vmMap.get(vm_name);
			vm.protect = true;
		}

		// reduce memory of none-procted VM and protect VM in host for UI
		for(Host h : hostList) {
			for(VM vm : h.vmList) {
//				if(!vm.protect) {
					h.left_size -= vm.size;
//				}
			}
		}
//			input_protectList.add(protect_vm_list.get(j));
//		setProtectVMs(input_protectList);
	}
		
//	public static void write_combination(n, S) {
//        combinations_to_file(strList, n, "/tmp/9/a");
//	}
	
	public static void getHostFreeMemListForUI(List<String> retList) {
		Collections.sort(hostList, new HostNameComparator());
		for(Host h : hostList) {
			retList.add(h.name + "_" + h.left_size);
		}
	}
	
	public static int getVMListForUI(List<String> retList, boolean onlyProtect) {
		
		int host_size = hostList.size();
//		Integer[] VM_number_per_host = new Integer[host_size];
		Map<String, Integer> host_vm_count_map = new HashMap<String, Integer>();
		Map<String, ArrayList<Integer>> host_vm_list_map = new HashMap<String, ArrayList<Integer>>();
		for(VM vm : vmList) {
			if(onlyProtect) {
				if(vm.protect) {
					String run_on_host_name = vm.ranHostsHistory.get(vm.ranHostsHistory.size()-1);
					Integer vm_num = host_vm_count_map.get(run_on_host_name);
					if( vm_num == null) {
						host_vm_count_map.put(run_on_host_name, 1);
						ArrayList<Integer> vm_size_list = new ArrayList<Integer>();
						vm_size_list.add(vm.size);
						host_vm_list_map.put(run_on_host_name, vm_size_list);
					} else {
						host_vm_count_map.put(run_on_host_name, vm_num+1);
						ArrayList<Integer> vm_size_list = host_vm_list_map.get(run_on_host_name);
						vm_size_list.add(vm.size);
					}
				}
			} else {
				
				if(!vm.protect) {
					String run_on_host_name = vm.ranHostsHistory.get(vm.ranHostsHistory.size()-1);
					Integer vm_num = host_vm_count_map.get(run_on_host_name);
					
					if( vm_num == null) {
						host_vm_count_map.put(run_on_host_name, 1);
						ArrayList<Integer> vm_size_list = new ArrayList<Integer>();
						vm_size_list.add(vm.size);
						host_vm_list_map.put(run_on_host_name, vm_size_list);
					} else {
						host_vm_count_map.put(run_on_host_name, vm_num+1);
						ArrayList<Integer> vm_size_list = host_vm_list_map.get(run_on_host_name);
						vm_size_list.add(vm.size);
					}
				}

			}
		}
		// find max count
		int max_cont = 0;

		for(Host h : hostList) {
			if(host_vm_count_map.containsKey(h.name)) {
				int vm_cnt = host_vm_count_map.get(h.name);
				max_cont = max_cont > vm_cnt ? max_cont : vm_cnt;				
			}
		}
		
		for(int i = 0; i < host_size; i++) {
			String host_name = hostList.get(i).name;
			if(host_vm_list_map.containsKey(host_name)) {
				List<Integer> vm_list = host_vm_list_map.get(host_name);
				int size = vm_list.size();
				for(int j = 0; j < size; j++) {
					retList.add(host_name + "_" + vm_list.get(j));
				}
				for(int j = size; j < max_cont; j++) {
					retList.add(host_name + "_0");
				}				
			} else {
				for(int j = 0; j < max_cont; j++) {
					retList.add(host_name + "_0");
				}
			}

		}
		return max_cont;
	}
	

	/*
	public static void protectAndShowMaxTolerance() {
//		System.out.println("## protectAndShowMaxTolerance ##");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(config_file));
			String str;
			while((str = br.readLine()) != null) {
				if(str.startsWith("## protect order")) {
					while((str = br.readLine()) != null) {
						if(str.contains("end"))
							break;
//						System.out.println("protect vm: " + str);
						VM vm = vmMap.get(str);
						vm.protect = true;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// first reduce memory of none-procted VM in host
		for(Host h : hostList) {
			for(VM vm : h.vmList) {
				if(!vm.protect) {
					h.left_size -= vm.size;
				}
			}
		}

		biggestFit();
//		Collections.sort(hostList, new HostLeftSizeComparator());
//		for(Host h: hostList) {
//			h.output();
//		}
		
		getMaxFailure(0);
	}
	*/
	
	public static int getMaxFailure(int callCnt) {
//		System.out.println("## try die host ##");
//		if(callCnt == 31) {
//			System.out.println();
//		}
		int host_size = hostList.size();
		for(int i = 1; i <= host_size; i++) {
//			System.out.println("#### try die num: " + i);
//			if(i == 26)
//				System.out.println();
			if(!TryDieHost(i)) {
				System.out.printf("## %d -- max fail tolerance: %d ##\n", callCnt, i-1);
				return (i-1);
			}
		}
		return -1;
	}
	


	public static boolean TryDieHost(int dieNum) {
		int biggestVM = getBiggestVM();
		List<Integer> capacityList = new ArrayList<Integer>();
		Collections.sort(hostList, new HostLeftSizeComparator());
		for(Host h : hostList)
			capacityList.add(h.left_size / biggestVM);

		int leftCapacity = 0;
		for (int i = dieNum; i < hostList.size(); i++)
			leftCapacity += capacityList.get(i);

		System.out.printf("## leftCapacity: %d\n", leftCapacity);

		int failure_vm_length = 0;
		Collections.sort(hostList, new HostPlaceVMSizeComparator());
		for(int i = 0; i < dieNum; i++) {
			failure_vm_length += hostList.get(i).vmPlaceList.size();
		}
		System.out.printf("## failure_vm_length: %d\n", failure_vm_length);
		if(failure_vm_length > leftCapacity)
			return false;
		return true;
	}
	
	/********************************************************************************
	 * Simulate fail
	 ********************************************************************************/
	public static List<String> simulate_fail_hostList = new ArrayList<String>();
	
	public static void simulate_fail(int num_hosts_fail) {
		clean();
		loadConfig();
		load_protect_config();
		simulate_fail_hostList.clear();
		setProtectVMs(protect_vm_list);
		List<String> fail_hosts = new ArrayList<String>();
		Collections.sort(hostList, new HostNameComparator());
		for(int i = 0; i < num_hosts_fail; i++)
			fail_hosts.add(hostList.get(i).name);
		
		simulate_hosts_fail(fail_hosts);
	}
	
	public static void simulate_hosts_fail_prepare() {
		clean();
		loadConfig();
		load_protect_config();
		simulate_fail_hostList.clear();
//		setProtectVMs(protect_vm_list);
		setProtectVMs_ForUI();
	}
	
	
	public static boolean simulate_hosts_fail(List<String> die_hosts_name_list) {
		simulate_fail_hostList = die_hosts_name_list;
		for(String host_name : die_hosts_name_list) {
			Host h = hostMap.get(host_name);
			if(h == null) {
				System.err.println("#err#");
			}
			for(VM vm : h.vmList) {
				if(vm.protect) {
					Collections.sort(hostList, new HostLeftSizeComparator());
					for(Host h_ : hostList) {
						if(die_hosts_name_list.contains(h_.name))
							continue;
						if(h_.left_size < vm.size) {
							return false;
						}
						vm.moveToHost = h_.name;
						h_.left_size -= vm.size;
						break;
					}					
				}

			}
		}
		return true;
	}
	
	public static int getMovedVMListForUI(List<String> retList) {
		int host_size = hostList.size();
//		Integer[] VM_number_per_host = new Integer[host_size];
		Map<String, Integer> host_vm_count_map = new HashMap<String, Integer>();
		Map<String, ArrayList<Integer>> host_vm_list_map = new HashMap<String, ArrayList<Integer>>();
		for(VM vm : vmList) {
			if(vm.moveToHost != null) {
//				String run_on_host_name = vm.ranHostsHistory.get(vm.ranHostsHistory.size()-1);
				Integer vm_num = host_vm_count_map.get(vm.moveToHost);
				if( vm_num == null) {
					host_vm_count_map.put(vm.moveToHost, 1);
					ArrayList<Integer> vm_size_list = new ArrayList<Integer>();
					vm_size_list.add(vm.size);
					host_vm_list_map.put(vm.moveToHost, vm_size_list);
				} else {
					host_vm_count_map.put(vm.moveToHost, vm_num+1);
					ArrayList<Integer> vm_size_list = host_vm_list_map.get(vm.moveToHost);
					vm_size_list.add(vm.size);
				}
			}
		}
		// find max count
		int max_cont = 0;

		for(Host h : hostList) {
			if(host_vm_count_map.containsKey(h.name)) {
				int vm_cnt = host_vm_count_map.get(h.name);
				max_cont = max_cont > vm_cnt ? max_cont : vm_cnt;				
			}
		}
		
		for(int i = 0; i < host_size; i++) {
			String host_name = hostList.get(i).name;
			if(host_vm_list_map.containsKey(host_name)) {
				List<Integer> vm_list = host_vm_list_map.get(host_name);
				int size = vm_list.size();
				for(int j = 0; j < size; j++) {
					retList.add(host_name + "_" + vm_list.get(j));
				}
				for(int j = size; j < max_cont; j++) {
					retList.add(host_name + "_0");
				}				
			} else {
				for(int j = 0; j < max_cont; j++) {
					retList.add(host_name + "_0");
				}
			}

		}
		return max_cont;
	}
	
	public static void test_old() {
		/*
		Arrays.sort(VMs, Collections.reverseOrder());
		Arrays.sort(Hosts, Collections.reverseOrder());
		System.out.printf("VMs: %s\n", Arrays.toString(VMs));
		System.out.printf("Hosts: %s\n", Arrays.toString(Hosts));

		for (Integer h : Hosts) {
			Host host = new Host(h);
			hostList.add(host);
		}
		biggestFit();
		
		TryDieHost(7);
		*/
	}
	
	public static long choose(long total, long choose){
	    if(total < choose)
	        return 0;
	    if(choose == 0 || choose == total)
	        return 1;
	    return choose(total-1,choose-1)+choose(total-1,choose);
	}
	
	public static void sort_host_list_by_name() {
		Collections.sort(hostList, new HostNameComparator());
	}
}

