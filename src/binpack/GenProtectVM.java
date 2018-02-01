package binpack;

public class GenProtectVM {

	public static void main(String[] args) {
		test();
	}

	public static void test() {
		int host_num = 16;//32;//16;
		int vm_num_per_host = 8;//4;//8;
		int order = 0;
		// order = 0 : H1_VM_01, H1_VM_02, ..., H1_VM_08; H2_VM_01, ...., H2_VM_08; ..., H16_VM_08
		// order = 1 : H1_VM_01, H2_VM_01, ..., H16_VM_01; H1_VM_02, ..., H16_VM_08; ..., H16_VM_08
		
//		String[] host_ay = new String[host_num];
//		String[] vm_ay = new String[host_num * vm_num_per_host];
		
		if(order == 0) {
			for(int i = 1; i <= host_num; i++) {
				for(int j = 1; j <= vm_num_per_host; j++) {
					if(vm_num_per_host < 10)
						System.out.printf("H%d_VM_0%d\n", i, j);
					else
						System.out.printf("H%d_VM_%d\n", i, j);
				}
			}
		} else if(order == 1) {
			for(int j = 1; j <= vm_num_per_host; j++) {
				for(int i = 1; i <= host_num; i++) {
					if(vm_num_per_host < 10)
						System.out.printf("H%d_VM_0%d\n", i, j);
					else
						System.out.printf("H%d_VM_%d\n", i, j);
				}
			}
		}
	}
}
