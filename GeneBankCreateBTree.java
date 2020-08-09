

import java.io.File;

public class GeneBankCreateBTree {
	private static int cacheLevel; //arg0
	private static int degree;		//arg1
	private static File gbkFile;		//arg2
	private static int sequenceLenght;	//arg3
	private static int cacheSize;		//arg4
	private static int debugLevel;		//arg5
	private static boolean useDebug, useCache = false;
	private static int blockSize =4096;
	private static int metaData = 40; // filler numbers
	private static int pointerSize = 4; // filler numbers
	private static int nodeSize = 40;  // filler numbers
	
	
	public static void main(String[] args) {
		//checks for correct args lenght
		try {
			
				if(args.length < 3 || args.length > 5) {
					System.err.println("Incorrect argument length");
					printUsage();
				}
		} catch(Exception e) {
			printUsage();
		}
		
		// checks cacheLevel and uses cache if arg[0]=1
		if (Integer.parseInt(args[0]) == 1) {
			useCache =true;
		}
		
		//sets user arguments
		degree = Integer.parseInt(args[1]);
		gbkFile= new File(args[2]);
		
		
		//checks to make sure sequence lenght is correct
		sequenceLenght = Integer.parseInt(args[3]);
		if(sequenceLenght>31 || sequenceLenght <1) {
			System.err.println("Wrong sequence lenght");
			printUsage();
		}
		//sets cache size 
		if (args.length == 4 || args.length ==5 ) {
			cacheSize = Integer.parseInt(args[4]);
		}
		
		// writes a text file named dump if arg[5] = 1;
		if (args.length == 5) {
			if (Integer.parseInt(args[5]) == 1 )
				useDebug=true;
		}
		
		//find optimal degree when user degree =0
		
		if (degree == 0) {
			int result = blockSize - pointerSize + nodeSize - metaData;
			result = result / (nodeSize*2 + pointerSize*2) ; 
			
			degree = result;  // check math to see if its correct
		}
		
		//Create the Btree
		
		String outputFileName = args[2] + ".btree.data." + sequenceLenght + "." + degree;
		
		
		
		
		
		
			
		
	}
	//function to dump tree with inorder traversal when debug = true 
	public static void dumpTree() {
		
	}
	
	
	//parses the given file, ignores everything but the genome
	public static void parseFile() {
		
	}
	
	public static void printUsage() {
		
	}
	
	
	

}
