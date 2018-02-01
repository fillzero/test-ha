package binpack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiggestFit {

//	static Integer[] VMs = {4, 3, 1, 3, 2, 1, 3, 2, 1, 3, 1, 3, 2, 4, 3, 1, 2, 3, 1};
//	static Integer[] Hosts = {10, 8, 15, 14, 18, 9, 5, 15, 9, 14, 11};

//	static Integer[] VMs = {2, 2, 2, 2, 2, 2, 2, 2};
//	static Integer[] Hosts = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

	// callum
//	static Integer[] VMs =   {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
//	static Integer[] Hosts = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
	
	// callum
//	static Integer[] VMs =   {2,2,2,2,   2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
//	static Integer[] Hosts = {6, 6,      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
	
//	public static String config_file = "/home/test/soft/xenserver/issue/REQ-503/simulate/HA-data-01";
//	public static String config_file = "/home/test/soft/xenserver/issue/REQ-503/simulate/HA-data-02";
//	public static String config_file = "/home/test/soft/xenserver/issue/REQ-503/simulate/HA-test";
//	public static String config_file = "/home/test/soft/xenserver/issue/REQ-503/simulate/HA-data-32-host-01";
//	public static String config_file = "/home/test/soft/xenserver/issue/REQ-503/simulate/HA-data-32-host-02";
//	public static String config_file = "/home/test/soft/xenserver/issue/REQ-503/simulate/HA-16-hosts-128-VMs-with-same-memory";
//	public static String config_file = "/tmp/ha/topo_cfg/HA-32-hosts-4096-VMs-random-memory-1024-protect-VMs";
	public static String config_file = "/tmp/ha/topo_cfg/HA-16-hosts-128-VMs-same-memory-64-protect-VMs";
	
	public static List<VM> vmList = new ArrayList<VM>();
	public static List<Host> hostList = new ArrayList<Host>();
	static Map<String, VM> vmMap = new HashMap<String, VM>();
	static Map<String, Host> hostMap = new HashMap<String, Host>();

	public static List<String> protect_vm_list = new ArrayList<String>();
	
	public static void clean() {
		hostList.clear();
		hostMap.clear();
		vmList.clear();
		vmMap.clear();
		protect_vm_list.clear();
	}
	
	public static void loadConfig() {
		hostList.clear();
		hostMap.clear();
		vmList.clear();
		vmMap.clear();
		try {
			BufferedReader br = new BufferedReader(new FileReader(config_file));
			String str;
			while((str = br.readLine()) != null) {
//				System.out.println(str);
				if(str.startsWith("## Host")) {
					while((str = br.readLine()) != null) {
						if(str.contains("end"))
							break;
//						System.out.println(str);
						String[] ay = str.split("\t");
						Host h = new Host(ay[0], Integer.parseInt(ay[1]));
						hostList.add(h);
						hostMap.put(ay[0], h);
					}
				} else if(str.startsWith("## VM")) {
					while((str = br.readLine()) != null) {
						if(str.contains("end"))
							break;
						if(str.length() < 4)
							continue;
//						System.out.println(str);
						String[] ay = str.split("\t");
						VM vm = new VM(ay[0], Integer.parseInt(ay[1]));
						vm.ranHostsHistory.add(ay[2]);
						vmList.add(vm);
						vmMap.put(ay[0], vm);
						
						// find associate host
						Host h = hostMap.get(ay[2]);
						h.vmList.add(vm);
//						for(Host h : hostList) {
//							if(h.name.equals(ay[2])) {
//								h.vmList.add(vm);
//								break;
//							}
//						}
					}
				}
//				System.out.println(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

//		for(Host h : hostList) {
//			h.output();
//		}
	}

	public static void load_protect_config() {
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
						protect_vm_list.add(str);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setProtectVMs(List<String> input_protectList) {
//		System.out.println("## protectAndShowMaxTolerance ##");
		for(String vm_name : input_protectList) {
			VM vm = vmMap.get(vm_name);
			vm.protect = true;
		}

		// first reduce memory of none-procted VM in host
		for(Host h : hostList) {
			for(VM vm : h.vmList) {
				if(!vm.protect) {
					h.left_size -= vm.size;
				}
			}
		}

//		Collections.sort(hostList, new HostLeftSizeComparator());
//		for(Host h: hostList) {
//			h.output();
//		}
	}
	
	public static void biggestFit() {
//		Integer[] tmpHosts = new Integer[Hosts.length];
//		System.arraycopy(src, srcPos, dest, destPos, length);
		Collections.sort(hostList, new HostLeftSizeComparator());
		
		List<VM> protectVMList = new ArrayList<VM>();
		for(VM vm : vmList) {
			if(vm.protect)
				protectVMList.add(vm);
		}

		Collections.sort(protectVMList, new VMSizeComparator());
		for(VM vm : protectVMList) {
			Host host = hostList.get(0);
			if(host.left_size > vm.size) {
				host.left_size -= vm.size;
				host.vmPlaceList.add(vm);
				vm.ranHostsHistory.add(host.name);
//				Collections.sort(hostList, new Comparator<Host> () {
//					@Override
//					public int compare(Host o1, Host o2) {
////						return o1.left_size - o2.left_size;
//						return o2.left_size - o1.left_size;	// reverse sort
//					}
//					
//				});
				Collections.sort(hostList, new HostLeftSizeComparator());
			} else {
				System.out.println("Error!");
				System.exit(1);
			}
		}


//		Collections.sort(hostList, new HostSizeComparator());
//		for(Host h: hostList) {
//			h.output();
//		}
		
		
//		System.out.println("## protect vm list ##");
//		for(VM vm : protectVMList) {
//			vm.output();
//		}
	}
	
	public static int getBiggestVM() {
		int biggest = 0;
		for (VM vm : vmList) {
			if ( vm.protect && vm.size > biggest )
				biggest = vm.size;
		}
		return biggest;
	}
	
	public static void main(String[] args) {

	}
}

class HostNameComparator implements Comparator<Host> {

	@Override
	public int compare(Host o1, Host o2) {
		return o1.name.compareTo(o2.name);
	}
	
}
class HostLeftSizeComparator implements Comparator<Host> {
	@Override
	public int compare(Host o1, Host o2) {
		return o2.left_size - o1.left_size;	// reverse sort
	}
}

class HostSizeComparator implements Comparator<Host> {
	@Override
	public int compare(Host o1, Host o2) {
		return o2.size - o1.size;	// reverse sort
	}
}

class HostPlaceVMSizeComparator implements Comparator<Host> {
	@Override
	public int compare(Host o1, Host o2) {
		return o2.vmPlaceList.size() - o1.vmPlaceList.size();	// reverse sort
	}
}

class HostProtectVMSizeComparator implements Comparator<Host> {
	@Override
	public int compare(Host o1, Host o2) {
		int o1_protect_size = 0;
		int o2_protect_size = 0;
		for(VM vm : o1.vmList) {
			if(vm.protect)
				o1_protect_size++;
		}
		for(VM vm : o2.vmList) {
			if(vm.protect)
				o2_protect_size++;
		}
		return o2_protect_size - o1_protect_size; // reverse sort
//		return o2.vmList.size() - o1.vmList.size();	// reverse sort
	}
}

class VMSizeComparator implements Comparator<VM> {
	@Override
	public int compare(VM o1, VM o2) {
		return o2.size - o1.size;	// reverse sort
	}
}