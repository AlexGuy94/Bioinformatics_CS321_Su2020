

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class GeneBankCreateBTree {
	private static int cacheLevel; //arg0
	private static int degree;		//arg1
	private static File gbkFile;		//arg2
	private static int sequenceLength;	//arg3
	private static int cacheSize;		//arg4
	private static int debugLevel;		//arg5
	private static boolean useDebug, useCache = false;
	private static int blockSize =4096;
	private static int metaData = 40; // filler numbers
	private static int pointerSize = 4; // filler numbers
	private static int nodeSize = 40;  // filler numbers
	private static String strSequence, subSequence; // whole dna sequence string
	private static String outputFileName, dumpFileName; 
	private static BTree bTree = null;
	
	public static void main(String[] args) throws IOException {
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
		sequenceLength = Integer.parseInt(args[3]);
		if(sequenceLength>31 || sequenceLength <1) {
			System.err.println("Wrong sequence lenght");
			printUsage();
		}
		//sets cache size 
		if (args.length == 5 || args.length ==6 ) {
			cacheSize = Integer.parseInt(args[4]);
		}
		
		// writes a text file named dump if arg[5] = 1;
		if (args.length == 6) {
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
		
		outputFileName = args[2] + ".btree.data." + sequenceLength + "." + degree;
		dumpFileName = args[2] + ".btree.data." + sequenceLength + "." + degree;
		
		//
		
		parseFile(gbkFile);
		System.out.println("done");
			
		
	}
	//function to dump tree with inorder traversal when debug = true 
	public static void dumpTree() throws FileNotFoundException {
		PrintStream o =new PrintStream(new File(dumpFileName));
		
		//breaks the dna sequence into substrings of sequence lenght k 
				subSequence = "";
				for(int i =0; i<strSequence.length(); i++) {
					for(int j= sequenceLength; j< strSequence.length(); j++) {
						if(!strSequence.substring(i,j).contains("n")){
							subSequence += strSequence.substring(i,j);
						}
					}
				}
			
	}
	
	
	//parses the given file, ignores everything but the genome
	public static void parseFile(File gbkFile) throws IOException {
 		Scanner scan = null;
		boolean startParse =false;
		try {
			scan = new Scanner(gbkFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String line = "";
		strSequence = "";
		
		//starts scanning the file for sequence start
		while(scan.hasNextLine()) {
				line = scan.nextLine();
				if(line.contains("ORIGIN")) {   //NEED TO READ THROUGH CHECKING FOR MULTIPLE NUMBER OF i "ORINGIN"'S THEN LOOP THE PARSING SEQUENCE i TIMES.
					startParse=true;
					break;
				}	
		}
		if(startParse == false) {
			System.out.println("Origin not Found");
			System.exit(1);
		}

		// starts dna sequence
		if(startParse == true) {
			while(scan.hasNextLine()){
				if(scan.hasNext("//"))
					break;
				strSequence += scan.next();
				
			}
			strSequence = strSequence.replaceAll("\\d", "");
			strSequence = strSequence.toLowerCase();
		}
		
		//breaks the dna sequence into substrings of sequence lenght k 
		subSequence = "";
		
		
		BTree bTree = new BTree(degree,sequenceLength,outputFileName);
		RandomAccessFile bTreeFile = new RandomAccessFile(outputFileName, "rw");
		bTreeFile.setLength(0);
		bTreeFile.close();
		int i=0;
		int j=sequenceLength;
		
		while(i<strSequence.length()&&j<strSequence.length()) {
			int frequency=0;
			if(!strSequence.substring(i,j).contains("n")){
				subSequence = strSequence.substring(i,j);
				//testing outputs
				
				BTreeObject object = bTree.BTreeFrequencySearch(bTree.getRoot(), convertToLong(subSequence));
					if (object==null) {
					bTree.insert(convertToLong(subSequence)); 
					frequency = 1;
				} else {
					frequency= object.getFrequency();
				}
					
					
				
			}
			System.out.println(subSequence +": " + frequency+": "+ i);
			i++;
			j++;
		}
		bTree.writeMeta();
//		for(int i =0; i<strSequence.length(); i++) {
//			for(int j= sequenceLength; j< strSequence.length(); j++) {
//				if(!strSequence.substring(i,j).contains("n")){
//					subSequence = strSequence.substring(i,j);
//					System.out.println(subSequence);
//				}
//			}
//		}
		
		
		
		
		
	}
	
	public static void insertIntoBTree(BTree tree, String sub) throws IOException {
		long key = convertToLong(sub);
		tree.insert(key);
	}
	
	
	public static long encodeLong(String sequence) {
		String s = sequence;
	      s = s.replaceAll("a","00");
	      s = s.replaceAll("t","11");
	      s = s.replaceAll("c","01");
	      s = s.replaceAll("g","10");
	      Long m = 1l;
	      m = m<<63; 
	      return (Long.parseLong(s,2) | m);
	}
	
	public static void printUsage() {
		System.out.println("java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length>\r\n" + 
				"[<cache size>] [<debug level>]");
	}
	
	
	
	
	//convert DNA substring to Long data type
		private static long convertToLong(String dna) {
			dna = dna.toLowerCase();
			long seq = Long.parseLong(dna, 32);
			
			return seq;
		}
	//convert Long substring to String data type
		private static String convertToString(long dna) {
			
			String seq = Long.toString(dna,32);
			return seq;
		}
		
	

}
