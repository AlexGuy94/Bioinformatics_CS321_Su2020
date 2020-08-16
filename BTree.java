import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class BTree {

	private int degree,length;
	private BTreeNode root;
	private RandomAccessFile bTreeFile;
	private String fileName;
	
	
	/**
	 * Constructor for BTree
	 * 
	 * @param degree
	 * @param bTreeFile
	 * @throws IOException 
	 */
	public BTree(int degree,int length, String fileName) throws IOException {
		this.fileName = fileName;
		this.degree = degree;
		root = new BTreeNode(degree, 12);
		root.setnumKeys(0);
		root.setLeaf(true);
		this.length = length;
		//this.writeMeta();
		this.writeNode(root);
		
	}
	
	/**
	 * Returns the root node of the BTree
	 * 
	 * @return 
	 */
	public BTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sets the root of the Btree
	 * 
	 * @param root
	 */
	public void setRoot(BTreeNode root) {
		
		this.root = root;
	}
	
	/**
	 * Reads bytes for the RandomAccessFile at a given locationa and parses into a BTreeNode.
	 * 
	 * @param location
	 * @return node at a given location
	 * @throws IOException
	 */
	public BTreeNode readNode(int location) throws IOException {
		bTreeFile = new RandomAccessFile(fileName, "r");
		bTreeFile.seek(location);
		BTreeNode node = new BTreeNode(degree, location);
		node.readNode(bTreeFile);
		bTreeFile.close();
		return node;
	}
	
	/**Writes a node to file at it's listed location after updates to the Node.
	 * 
	 * @param node
	 * @throws IOException
	 */
	public void writeNode(BTreeNode node) throws IOException {
		bTreeFile = new RandomAccessFile(fileName, "rw");
		node.writeNode(bTreeFile);
		bTreeFile.close();
	}
	
	
	/**
	 * BTreeSearch searches the BTree for a specific key and returns the frequency of the key.
	 * 
	 * @param node
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public BTreeObject BTreeSearch(BTreeNode node, long key) throws IOException {
		int i=0;
		while(i<node.getnumKeys() && key>node.getBTreeObject(i).getKey()) {
			i++;
		}
		if (i<node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			return node.getBTreeObject(i);
		}
		if(node.isLeaf()) {
			return null;
		} else {
			BTreeNode newNode = readNode(node.getChild(i));
			return BTreeSearch(newNode, key);
		}
	}
	
	
	public BTreeObject BTreeFrequencySearch(BTreeNode node, long key) throws IOException {
		int i=0;
		while(i<node.getnumKeys() && key>node.getBTreeObject(i).getKey()) {
			i++;
		}
		if (i<node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			node.getBTreeObject(i).setFrequency(node.getBTreeObject(i).getFrequency()+1); //update frequency
			RandomAccessFile bTreeFile = new RandomAccessFile(fileName,"rw");
			node.writeNode(bTreeFile);
			bTreeFile.close();
			return node.getBTreeObject(i);
		}
		if(node.isLeaf()) {
			return null;
		} else {
			BTreeNode newNode = readNode(node.getChild(i));
			if(newNode.getLoc()==0) {
				return null;
			}

			return BTreeFrequencySearch(newNode, key);
		}
	}
	
	/**
	 * Overload Method for including a cache.
	 * 
	 * @param node
	 * @param key
	 * @param c
	 * @return
	 * @throws IOException
	 */
	public BTreeObject BTreeSearch(BTreeNode node, long key, Cache c) throws IOException {
		int i=1;
		
		while(i<node.getnumKeys() && key>node.getBTreeObject(i).getKey()) {
			i++;
		}
		if (i<=node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			return node.getBTreeObject(i);
		}
		if(node.isLeaf()) {
			return null;
		} else {
			int cacheIndex = c.findNode(node.getChild(i));
			if(cacheIndex>-1){
				node = (BTreeNode) c.getObject(cacheIndex);
			}else {
			node = readNode(node.getChild(i));
			c.addObject(node);
			}
			return BTreeSearch(node, key, c);
		}
	}
	
	
	
	//Split-Child
	private void childSplit(BTreeNode parentNode, int index, BTreeNode childNode) throws IOException {
		bTreeFile = new RandomAccessFile(fileName, "r");
		int newNodeLocation = (int) bTreeFile.length();
		bTreeFile.close();
		BTreeNode newChildNode = new BTreeNode(degree, newNodeLocation);
		newChildNode.setLeaf(childNode.isLeaf());
		newChildNode.setParent(parentNode.getLoc());
		newChildNode.setnumKeys(degree-1);
		for (int j=0;j<degree-1;j++) {
			newChildNode.setTreeObject(j, childNode.getBTreeObject(j+degree));
			//childNode.setTreeKey(j+degree, 0);
			//childNode.setTreefrequency(j+degree, 1);
		}
		if (!childNode.isLeaf()) {
			for(int j=0;j<degree;j++) {
				newChildNode.setChildPointer(j, childNode.getChild(j+degree));
				//childNode.setChildPointer(j+degree, 0);
			}
		}
		childNode.setnumKeys(degree-1);
		for(int j=parentNode.getnumKeys()+1;j>=index+1;j--) {
			parentNode.setChildPointer(j, parentNode.getChild(j-1));	
		}
		parentNode.setChildPointer(index+1, newChildNode.getLoc());
		for(int j=parentNode.getnumKeys();j>=index+1;j--) {
			parentNode.setTreeObject(j, parentNode.getBTreeObject(j-1));
		}
		parentNode.setTreeObject(index, childNode.getBTreeObject(degree-1));
		parentNode.setnumKeys(parentNode.getnumKeys()+1);
		writeNode(parentNode);
		writeNode(childNode);
		writeNode(newChildNode);
		
		
	}
	
	
	//Insert
	public void insert(long key) throws IOException {
		bTreeFile = new RandomAccessFile(fileName, "r");
		BTreeNode r = this.getRoot();
		if(r.getnumKeys()==2*degree-1) {
			int newNodeLocation = (int) bTreeFile.length();
			bTreeFile.close();
			BTreeNode newNode = new BTreeNode(degree, newNodeLocation);
			writeNode(newNode);
			this.setRoot(newNode);
			r.setParent(newNode.getLoc());
			newNode.setLeaf(false);
			newNode.setnumKeys(0);
			newNode.setChildPointer(0, r.getLoc());
			childSplit(newNode, 0, r);
			insertNonFull(newNode, key);
			
		} else {
			insertNonFull(r, key);
		}
	}
	
	
	//Insert Non-Full
	private void insertNonFull(BTreeNode node, long key) throws IOException {
		int i = node.getnumKeys();
		if (node.isLeaf()) {
			while(i>=1 && key<node.getBTreeObject(i-1).getKey()) {
				node.setTreeKey(i, node.getBTreeObject(i-1).getKey());
				i--;
			}
			node.setTreeKey(i, key);
			node.setnumKeys(node.getnumKeys()+1);
			writeNode(node);
		} else {
			while(i>=1 && key<node.getBTreeObject(i-1).getKey()) {
				i--;
			}
			//i++;
			BTreeNode childNode = readNode(node.getChild(i));
			if(childNode.getparent()==0) {
			childNode.setParent(node.getLoc());
			}
			if(childNode.getnumKeys()==2*degree-1) {
				childSplit(node, i, childNode);
				if(key>node.getBTreeObject(i).getKey()) {
					i++;
				}
			}
			insertNonFull(childNode, key);
		}
		
	}
	public void writeMeta() throws IOException {
		bTreeFile = new RandomAccessFile(this.fileName, "rw");
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.putInt(degree);
		buffer.putInt(length);
		buffer.putInt(root.getLoc());
		buffer.flip();
		bTreeFile.seek(0);
		bTreeFile.write(buffer.array());
		buffer.clear();
		bTreeFile.close();
	}
	
//<<<<<<< HEAD
	public void inorderTraversalPrint() throws IOException {
		
		if(root!=null) {
			NodePrint(root);
		}
			//=======
		}
	//method to be called by main to print dump file
	public  void inorderTraversalPrint(String outPutName, int seqLength) throws IOException {
		File dumpFile = new File(outPutName);
		FileWriter fileWriter = new FileWriter(dumpFile);
		
		printNodes(root, fileWriter, seqLength);
		fileWriter.close();
	
	}
	
	// method to traverse the tree 
	private void printNodes(BTreeNode node, FileWriter fileWriter, int seqLength) {
		
		int objects = node.getNumObjects();
		int value = seqLength;

		for (int i = 0; i < node.getnumKeys(); i++) {
			try {
				printNodes(readNode(i), fileWriter, value);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("error on recursive call");
				e1.printStackTrace();
			}

			if (objects >= i) {
				String string = convertToString(node.getBTreeObject(i).getKey())+": "+node.getBTreeObject(i).getFrequency();
				try {
					fileWriter.write(string + "\n");
				} catch (IOException e) {
					System.out.println("error on 2nd block");
					e.printStackTrace();
				}
			}	
//>>>>>>> 5964a6fda3c64ebd9b56dc87c344f395c1a1947c
		}
		if (node.isLeaf()) {

			for (int j = 1; j <= objects; j++) {
				String string = convertToString(node.getBTreeObject(j).getKey())+": "+node.getBTreeObject(j).getFrequency();
				try {
					fileWriter.write(string + "\n");
				} catch (IOException e) {
					System.out.println("error on 3rd block");
					e.printStackTrace();
				}
			}
		}	
	}
	
	public void NodePrint(BTreeNode node ) throws IOException {
		int i=0;
		for (i=0; i<node.getnumKeys();i++) {
			if(!node.isLeaf()) {
				NodePrint(readNode(node.getChild(i)));
			}
			System.out.println(convertToString(node.getBTreeObject(i).getKey())+": "+node.getBTreeObject(i).getFrequency());
		}
		if(!node.isLeaf()) {
			NodePrint(readNode(node.getChild(i)));
		}
	}
	//convert Long substring to String data type
		private static String convertToString(long dna) {
			
			String seq = Long.toString(dna,32);
			return seq;
		
	}
}





