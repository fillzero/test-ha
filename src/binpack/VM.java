package binpack;

import java.util.ArrayList;
import java.util.List;

public class VM {
	String name;
	int size;
	boolean protect;
	String moveToHost;
	List<String> ranHostsHistory = new ArrayList<String>();

	public VM() {
		name = null;
		size = 0;
		protect = false;
		moveToHost = null;
	}
	public VM(String name, int size) {
		this.name = name;
		this.size = size;
		this.protect = false;
		moveToHost = null;
	}
	public VM(String name, int size, boolean protect) {
		this.name = name;
		this.size = size;
		this.protect = protect;
		moveToHost = null;
	}
	
	public void output() {
		System.out.printf("name: %s, size: %d, ran host: ", name, size);
		for(String s : ranHostsHistory) 
			System.out.printf("%s, ", s);
		System.out.println();
	}
}