package binpack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiggestFitOri extends BiggestFit{

	public static void main(String[] args) {
		test();
	}

	public static void test() {
		load_protect_config();
		int protect_size = protect_vm_list.size();
		for(int i = 0; i < protect_size; i++) {
			loadConfig();
			List<String> input_protectList = new ArrayList<String>();
			for(int j = 0; j <= i; j++) 
				input_protectList.add(protect_vm_list.get(j));
			setProtectVMs(input_protectList);
//			for(String s : input_protectList)
//				System.out.println(s);
			biggestFit();
			getMaxFailure(i+1);
		}
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
	
	/*
	 * Must ensure host.left_size = host.left_size - sizeof(all unprotect vm) - sizeof(all protect vm)
	 */
	public static int getMaxFailure(int callCnt) {
//		System.out.println("## try die host ##");
//		if(callCnt == 17) {
//			System.out.println();
//		}
		int host_size = hostList.size();
		
		for(int i = 1; i <= host_size; i++) {
			
//			System.out.println("#### try die num: " + i);
//			if(i == 26)
//				System.out.println();
			if(!TryDieHost(i)) {
//				System.out.printf("## %d -- max fail tolerance: %d ##\n", callCnt, i-1);
				return (i-1);
			}
		}
		return -1;
	}
	
	static List<Host> mock_hostList = new ArrayList<Host>();
	static List<VM> mock_vmList = new ArrayList<VM>();
	public static boolean TryDieHost(int dieNum) {
		// Assume all VMs are as big as the biggest
		int biggestVM = getBiggestVM();
		
		mock_hostList.clear();
		for(Host h : hostList) {
			Host h_new = new Host(h.name, h.size);
			h_new.left_size = h.left_size;
			for(VM vm : h.vmList) {
				VM vm_new = new VM(vm.name, vm.size, vm.protect);
				h_new.vmList.add(vm_new);
			}
			mock_hostList.add(h_new);
		}

		for(int i = 0; i < dieNum; i++) {
			// Assume each host has the max number of protect VMs on it
			int number_vms = 0;
			for(Host h : mock_hostList) {
				int protect_num = 0;
				for(VM vm : h.vmList)
					if(vm.protect)
						protect_num++;
				number_vms = protect_num > number_vms ? protect_num : number_vms;
//				number_vms = h.vmList.size() > number_vms ? h.vmList.size() : number_vms;
			}
			
			// re config VMs in each host
			for(Host h : mock_hostList) {
				h.vmList.clear();
				for(int j = 0; j < number_vms; j++)
					h.vmList.add(new VM("", biggestVM, true));
			}

			// Assume the biggest host fails
			Collections.sort(mock_hostList, new HostLeftSizeComparator());

			Host fail_host = mock_hostList.get(0);
			List<VM> move_vm_list = fail_host.vmList;
			mock_hostList.remove(0);

			// move failure VMs to other hosts
//			System.out.printf("====> die num: %d, move_vm_list size: %d\n", dieNum, move_vm_list.size());
			if(!pack_failed_vms_onto_live_hosts(move_vm_list))
				return false;
		}

		return true;
	}
	
	public static boolean pack_failed_vms_onto_live_hosts(List<VM> move_vm_list) {
		Collections.sort(move_vm_list, new VMSizeComparator());
		Collections.sort(mock_hostList, new HostLeftSizeComparator());
		for(VM vm : move_vm_list) {
			Host host = mock_hostList.get(0);
			if(host.left_size > vm.size) {
				host.left_size -= vm.size;
				host.vmList.add(vm);
				host.vmPlaceList.add(vm);
				vm.ranHostsHistory.add(host.name);
				Collections.sort(mock_hostList, new HostLeftSizeComparator());
			} else {
//				System.out.println("Error, pack_failed_vms_onto_live_hosts failed!, move_vm_list size: " + move_vm_list.size());
				return false;
			}
		}
		return true;
	}
}
