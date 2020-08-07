import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.Arrays;

public class GeneBankSearch {


	static int cache = 0;
	static String bTreeFileName = null;
	static String queryFileName = null;
	static int cacheSize = 0;
	static int debug = 0;

	public static void main(String args[]) {



		if(Integer.parseInt(args[0])==0 ||  Integer.parseInt(args[0])==1) {
			cache = Integer.parseInt(args[0]);
			if (cache==0) {

			}

		}else {
			System.out.println("Please use a 0 or 1 for the first argument");
			System.out.println("<0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		}

		if(args[1]!=null) {
			bTreeFileName = args[1];
		}

		if(args[2]!=null) {
			queryFileName = args[2];
		}
		if (cache == 1 && args.length==4){
			if (Integer.parseInt(args[3])>0) 
				cacheSize = Integer.parseInt(args[3]);

		} else if (cache == 1 && args.length==5) {
			if (Integer.parseInt(args[3])>1) 
				cacheSize = Integer.parseInt(args[3]);
			if (Integer.parseInt(args[4])==1) {
				debug = 1;
			} else {
				System.out.println("Please use a 0 or 1 for the debug level");
			}
		} else if (cache == 0 &&  args.length==4){
			if (Integer.parseInt(args[3])==1) {
				debug = 1;
			} else {
				System.out.println("Please use a 0 or 1 for the debug level");
			}
		}
		System.out.println(cache + ", " + bTreeFileName + ", " + queryFileName + ", " + cacheSize + ", " + debug);

	}

	private Byte[] ConvertFile() {
		Byte[] nodeArray = new Byte[200];
		try {
			RandomAccessFile bTreeFile = new RandomAccessFile(bTreeFileName, "r");
			for(int i=0;i<bTreeFile.length();i++) {
				if(nodeArray.length == i) {
					nodeArray = Arrays.copyOf(nodeArray, nodeArray.length*2);
				}
				nodeArray[i] = bTreeFile.readByte();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return nodeArray;
	}
}

