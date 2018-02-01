package binpack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TopoConfig {

	// Host
	static int host_num;
	static int host_mem_min;
	static int host_mem_max;
	static boolean host_mem_range; // false, every host has the same memory (host_mem_max)
	static int host_mem_usage_percent_range_min; // eg: 70% or 80%
	static int host_mem_usage_percent_range_max;
//	static int vm_num_per_host_range_min;
//	static int vm_num_per_host_range_max;
	
	// VM
	static int total_vm_num;
	static int vm_mem_min;
	static int vm_mem_max;
	static List<Integer> vm_mem_list;
	static int protect_vm_num;
	
	public static void main(String[] args) {
//		TopoConfig topoConfig = new TopoConfig();
//		System.out.println(topoConfig.host_mem_usage_percent);
//		test();
		
		mock_input_vm_info();
		mock_input_host_info();
		generate_config();
	}
	
	public static void test() {
		total_vm_num = 4096;
		vm_mem_min = 256;
		vm_mem_max = 256;
		
		host_num = 32;
		
		// how much memory each host needs (average) ?
		int[] vm_mem_array = new int[total_vm_num];
		int total_vm_mem = 0;
		Random rand = new Random();
		for(int i = 0; i < total_vm_num; i++) {
			int n = rand.nextInt(vm_mem_max - vm_mem_min + 1) + vm_mem_min;
			vm_mem_array[i] = n;
			total_vm_mem += n;
			System.out.println(n);
		}
		
		int host_min_mem_need_average = (int) ((total_vm_mem * 1.2) / host_num); 
	}
	
	public static void mock_input_vm_info() {
		total_vm_num = 4096;
		protect_vm_num = 512;
		vm_mem_min = 512;
		vm_mem_max = 4096;
		host_num = 32;
	}

	public static void mock_input_host_info() {
		host_mem_min = getHostMinMemNeed();
//		host_mem_max = host_mem_min + 550;//(int)(host_mem_min * 1.3);
		host_mem_max = (int)(host_mem_min * 1.3);
		host_mem_range = true;
	}
	
	// how much memory each host needs (average) ?
	public static int getHostMinMemNeed() {
//		int[] vm_mem_array = new int[total_vm_num];
		vm_mem_list = new ArrayList<Integer>();
		int total_vm_mem = 0;
		Random rand = new Random();
		for(int i = 0; i < total_vm_num; i++) {
			int n = rand.nextInt(vm_mem_max - vm_mem_min + 1) + vm_mem_min;
//			vm_mem_array[i] = n;
			vm_mem_list.add(n);
			total_vm_mem += n;
//			System.out.println(n);
		}
		
		// init host list with enough memory
		int host_mem_need_enough = (int) ((total_vm_mem * 1.5) / host_num);
		List<Host> host_list = new ArrayList<Host>();
		List<VM> vm_list = new ArrayList<VM>();
		
		for(int i = 0; i < host_num; i++) {
			String host_name;
			if(i < 9)
				host_name = "host0" + (i+1);
			else
				host_name = "host" + (i+1);
			Host h = new Host(host_name, host_mem_need_enough);
			host_list.add(h);
		}
		
		// put each vm into the biggest host (most free mem)
		for(int i = 0; i < total_vm_num; i++) {
			Collections.sort(host_list, new HostLeftSizeComparator());
			Host h = host_list.get(0);
			
			String vm_name;
			if(h.vmList.size() < 9)
				vm_name = h.name + "_VM_0" + (h.vmList.size() + 1);
			else
				vm_name = h.name + "_VM_" + (h.vmList.size() + 1);
			int vm_size = vm_mem_list.get(i);
			
			VM vm  = new VM(vm_name, vm_size);
			h.vmList.add(vm);
			vm_list.add(vm);
			h.left_size -= vm_size;
		}
		
		Collections.sort(host_list, new HostLeftSizeComparator());
		return host_mem_need_enough - host_list.get(host_list.size()-1).left_size;
//		int host_min_mem_need_average = (int) ((total_vm_mem * 1.2) / host_num); 
//		System.out.printf("min mem need per host: %d\n", host_min_mem_need_average); 
//		return host_min_mem_need_average;
	}

	public static void generate_config() {
		List<Host> host_list = new ArrayList<Host>();
		List<VM> vm_list = new ArrayList<VM>();
		Random rand = new Random();
		for(int i = 0; i < host_num; i++) {
			String host_name;
			if(i < 9)
				host_name = "host0" + (i+1);
			else
				host_name = "host" + (i+1);
			
			int host_mem;
			if(host_mem_range)
				host_mem = rand.nextInt(host_mem_max - host_mem_min + 1) + host_mem_min;
			else
				host_mem = host_mem_max;
			Host h = new Host(host_name, host_mem);
			host_list.add(h);
		}
		
		// put each vm into the biggest host (most free mem)
		
		for(int i = 0; i < total_vm_num; i++) {
			Collections.sort(host_list, new HostLeftSizeComparator());
			Host h = host_list.get(0);
			
			String vm_name;
			if(h.vmList.size() < 9)
				vm_name = h.name + "_VM_0" + (h.vmList.size() + 1);
			else
				vm_name = h.name + "_VM_" + (h.vmList.size() + 1);
			int vm_size = vm_mem_list.get(i);
			
			VM vm  = new VM(vm_name, vm_size);
			h.vmList.add(vm);
			vm_list.add(vm);
			h.left_size -= vm_size;
		}
		
		
		// put each vm into random host
		/*
		for(int i = 0; i < total_vm_num; i++) {
			int vm_size = vm_mem_list.get(i);
//			Collections.sort(host_list, new HostLeftSizeComparator());
			Host h;
			while(true) {
				h = host_list.get(rand.nextInt(host_num));
				if(h.left_size > vm_size)
					break;
			}

			String vm_name;
			if(h.vmList.size() < 9)
				vm_name = h.name + "_VM_0" + (h.vmList.size() + 1);
			else
				vm_name = h.name + "_VM_" + (h.vmList.size() + 1);
			
			
			VM vm  = new VM(vm_name, vm_size);
			h.vmList.add(vm);
			vm_list.add(vm);
			h.left_size -= vm_size;
		}*/
		
		
		Collections.sort(host_list, new HostLeftSizeComparator());
		// output 
		/// output host
		Collections.sort(host_list, new HostNameComparator());
		System.out.println("## Host");
		for(Host h : host_list) {
			System.out.printf("%s\t%d\n", h.name, h.size);	
		}
		System.out.println("## Host end\n");

		/// output vm
		System.out.println("## VM");
		for(Host h : host_list) {
			for(VM vm : h.vmList) {
				System.out.printf("%s\t%s\t%s\n", vm.name, vm.size, h.name);
			}
		}
		System.out.println("## VM end");
		
		/// output protect vm
		//// random select 
		System.out.println("## protect order");
		int cnt = protect_vm_num;
		while(cnt-- > 0) {
			int vm_index = rand.nextInt(vm_list.size());
			System.out.println(vm_list.get(vm_index).name);
			vm_list.remove(vm_index);
		}

		
		System.out.println("## protect end");		
	}

	// calculate average memory usage percent (min)
	public static int host_mem_usage_percent_min() {
		
		return 0;
	}

	
	public static void output()  {

	}
}
