import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class GeneBankSearch {


	private static int cache = 0;
	private static String bTreeFileName = null;
	private static String queryFileName = null;
	private static int cacheSize = 0;
	private static int debug = 0;
	private static Cache c;

	/**
	 * @param args
	 */

	public static void main(String args[]) {
	
	if(args.length < 3 || args.length > 6){
			
		System.out.println("Usage Error: java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>][<debug level>]");
	}
	else{

	//parse arguments
			if(Integer.parseInt(args[0])==0 ||  Integer.parseInt(args[0])==1) {
				cache = Integer.parseInt(args[0]);
				if (cache==0) {

				}

			}else {
				System.out.println("Please use a 0 or 1 for the first argument");
				System.out.println("<0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
				System.exit(-1);
			}

			if(args[1]!=null) {
				bTreeFileName = args[1];
			}
			else{
				System.out.println("BTree file invalid or does not exist.");
				System.exit(-1);
			}

			if(args[2]!=null) {
				queryFileName = args[2];
			}
			else{
				System.out.println("Query file invalid or does not exist.");
				System.exit(-1);
			}
		
			if (cache == 1 && args.length==4){
				if (Integer.parseInt(args[3])>0) 
					cacheSize = Integer.parseInt(args[3]);
					c = new Cache(cacheSize);


			} else if (cache == 1 && args.length==5) {
				if (Integer.parseInt(args[3])>1) {
					cacheSize = Integer.parseInt(args[3]);
					c = new Cache(cacheSize);
				}
				if (Integer.parseInt(args[4])==1) {
					debug = 1;
				} else {
					System.out.println("Please use a 0 or 1 for the debug level");
					System.exit(-1);
				}
			} else if (cache == 0 &&  args.length==4){
				if (Integer.parseInt(args[3])==1) {
					debug = 1;
				} else {
					System.out.println("Please use a 0 or 1 for the debug level");
					System.exit(-1);
				}
			}

			//testing output
			System.out.println(cache + ", " + bTreeFileName + ", " + queryFileName + ", " + cacheSize + ", " + debug);
			System.out.println(convertToLong("abc"));
			System.out.println(convertToString(convertToLong(("abc"))));
			// Add Files to be read and extract metadata

			try {
				RandomAccessFile bTreeFile = new RandomAccessFile(bTreeFileName, "rw");
				FileInputStream queryFile = new FileInputStream(queryFileName);

				ByteBuffer byteBuffer = ByteBuffer.allocate(12);
				bTreeFile.read(byteBuffer.array());

				//adjust order of metadata inputs if necessary 
				int degree = byteBuffer.getInt();
				int sequenceLength = byteBuffer.getInt();
				int rootLocation = byteBuffer.getInt();
				System.out.println(degree+" "+sequenceLength+" "+rootLocation);
				//build a root node from bTreeFile, will need degree
				BTreeNode rootNode = new BTreeNode(degree, rootLocation);
				//find the first byte of the Root Node
				bTreeFile.seek(rootLocation);

				//Need to read the node with a Node class read method
				rootNode.readNode(bTreeFile);

				//Make a BTree
				BTree bTree = new BTree(degree,sequenceLength, bTreeFileName);
				bTree.setRoot(rootNode);

				//Search for DNA subsequences in BTree and write to file
				Scanner scanner = new Scanner(queryFile);
				FileWriter writer;
				writer = new FileWriter("dump");

				//scan for subsequence and write to dump file and or print to console
				while(scanner.hasNext()) {
					String dnaSequence = scanner.next().toLowerCase();
					long longSequence = convertToLong(dnaSequence);
					int frequency = 0;
					if(cache==1) {
						BTreeObject object = bTree.BTreeSearch(bTree.getRoot(),longSequence,c);
						frequency = object.getFrequency();
					} else {
						BTreeObject object = bTree.BTreeSearch(bTree.getRoot(),longSequence);
						if (object == null) {
							frequency = 0;
						}else {
						frequency = object.getFrequency();
						}
					}
					System.out.println(dnaSequence + ": " + frequency);
					if(debug==1) {
						 writer.write(dnaSequence + ": " + frequency);
					}
				}
				writer.close();
				scanner.close();
				queryFile.close();
				//Add Cache Option...Search BTree...Output to Console/file



			} catch (IOException e) {

				e.printStackTrace();
				System.exit(-1);

			}
		}
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

	
