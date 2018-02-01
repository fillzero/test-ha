package binpack;

import java.util.ArrayList;
import java.util.List;

public class Host {
	public String name;
	int size;
	int left_size;
	List<VM> vmList;
	List<VM> vmPlaceList;
	public Host(String name, int size) {
		this.name = name;
		this.size = size;
		left_size = size;
		vmList = new ArrayList<VM>();
		vmPlaceList = new ArrayList<VM>();
	}
	
	public void output() {
		System.out.printf("name: %s, size: %d, left_size: %d => ", name, size, left_size);
		for(VM vm : vmList) {
			System.out.printf("%s # %d, ", vm.name, vm.size);
		}
		System.out.println();
	}
}