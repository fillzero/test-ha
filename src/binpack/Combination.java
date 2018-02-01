package binpack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Combination {
	public static void main(String[] args){
        String[] arr = {"A","B","C","D","E","F", "G", "H", "I", "J", "K"};
        combinations2(arr, 3, 0, new String[3]);        
    }

    static void combinations2(String[] arr, int len, int startPosition, String[] result){
        if (len == 0){
            System.out.println(Arrays.toString(result));
            return;
        }       
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            combinations2(arr, len-1, i+1, result);
        }
    }
    
    public static void combinations_to_file(List<String> strList, int n, String file_path){
    	String[] ay = strList.toArray(new String[0]);
    	try {
			FileWriter fw = new FileWriter(file_path);
			combinations_with_fw(ay, n, 0, new String[n], fw);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        
    }
    
    static void combinations_with_fw(String[] arr, int len, int startPosition, String[] result, FileWriter fw) throws IOException{
        if (len == 0){
        	String s = Arrays.toString(result);
        	s = s.replaceAll(" ", "").replace("[", "").replace("]", "");
        	fw.write(s);
        	fw.write("\n");
            return;
        }       
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            combinations_with_fw(arr, len-1, i+1, result, fw);
        }
    }
}
