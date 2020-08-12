import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class BTree {

	private int degree,length;
	private BTreeNode root;
	private RandomAccessFile bTreeFile;
	
	
	
	/**
	 * Constructor for BTree
	 * 
	 * @param degree
	 * @param bTreeFile
	 * @throws IOException 
	 */
	public BTree(int degree,int length, RandomAccessFile bTreeFile) throws IOException {
		this.degree = degree;
		root = new BTreeNode(degree, 12);
		root.setnumKeys(0);
		root.setLeaf(true);
		this.bTreeFile = bTreeFile;
		this.length = length;
		this.writeMeta();
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
		bTreeFile.seek(location);
		BTreeNode node = new BTreeNode(degree, location);
		node.readNode(bTreeFile);
		return node;
	}
	
	/**Writes a node to file at it's listed location after updates to the Node.
	 * 
	 * @param node
	 * @throws IOException
	 */
	public void writeNode(BTreeNode node) throws IOException {
		bTreeFile.seek(node.getLoc());
		node.writeNode(bTreeFile);
	}
	
	
	/**
	 * BTreeSearch searches the BTree for a specific key and returns the frequency of the key.
	 * 
	 * @param node
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public int BTreeSearch(BTreeNode node, long key) throws IOException {
		int i=0;
		while(i<node.getnumKeys() && key>node.getBTreeObject(i).getKey()) {
			i++;
		}
		if (i<node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			return node.getBTreeObject(i).getFrequency();
		}
		if(node.isLeaf()) {
			return 0;
		} else {
			node = readNode(node.getChild(i));
			return BTreeSearch(node, key);
		}
	}
	
	
	public int BTreeFrequencySearch(BTreeNode node, long key) throws IOException {
		int i=0;
		while(i<node.getnumKeys() && key>node.getBTreeObject(i).getKey()) {
			i++;
		}
		if (i<node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			node.getBTreeObject(i).setFrequency(node.getBTreeObject(i).getFrequency()+1); //update frequency
			return node.getBTreeObject(i).getFrequency();
		}
		if(node.isLeaf()) {
			return 0;
		} else {
			node = readNode(node.getChild(i));
			return BTreeSearch(node, key);
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
	public int BTreeSearch(BTreeNode node, long key, Cache c) throws IOException {
		int i=1;
		
		while(i<node.getnumKeys() && key>node.getBTreeObject(i).getKey()) {
			i++;
		}
		if (i<=node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			return node.getBTreeObject(i).getFrequency();
		}
		if(node.isLeaf()) {
			return 0;
		} else {
			int cacheIndex = c.findNode(node.getChild(i));
			if(cacheIndex>-1){
				node = (BTreeNode) c.getObject(cacheIndex);
			}else {
			node = readNode(node.getChild(i));
			c.addObject(node);
			}
			return BTreeSearch(node, key);
		}
	}
	
	
	
	//Split-Child
	private void childSplit(BTreeNode parentNode, int index, BTreeNode childNode) throws IOException {
		int newNodeLocation = (int) bTreeFile.length();
		BTreeNode newChildNode = new BTreeNode(degree, newNodeLocation);
		newChildNode.setLeaf(childNode.isLeaf());
		newChildNode.setParent(parentNode.getLoc());
		newChildNode.setnumKeys(degree-1);
		for (int j=0;j<degree-1;j++) {
			newChildNode.setTreeObject(j, childNode.getBTreeObject(j+degree));
		}
		if (!childNode.isLeaf()) {
			for(int j=0;j<degree-1;j++) {
				newChildNode.setChildPointer(j, childNode.getChild(j+degree-1));
			}
		}
		childNode.setnumKeys(degree-1);
		for(int j=parentNode.getnumKeys()+1;j>=index+1;j--) {
			parentNode.setChildPointer(j, parentNode.getChild(j-1));	
		}
		parentNode.setChildPointer(index, newChildNode.getLoc());
		for(int j=parentNode.getnumKeys();j>=index+1;j--) {
			parentNode.setTreeKey(j, parentNode.getBTreeObject(j-1).getKey());
		}
		parentNode.setTreeObject(index, childNode.getBTreeObject(degree-1));
		parentNode.setnumKeys(parentNode.getnumKeys()+1);
		writeNode(parentNode);
		writeNode(childNode);
		writeNode(newChildNode);
		
		
	}
	
	
	//Insert
	public void insert(long key) throws IOException {
	
		BTreeNode r = this.getRoot();
		if(r.getnumKeys()==2*degree-1) {
			int newNodeLocation = (int) bTreeFile.length();
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
			while(i>=1 && key<node.getBTreeObject(i).getKey()) {
				node.setTreeKey(i, node.getBTreeObject(i).getKey());
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
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.putInt(degree);
		buffer.putInt(length);
		buffer.putInt(root.getLoc());
		buffer.flip();
		bTreeFile.seek(0);
		bTreeFile.write(buffer.array());
		buffer.clear();
	}
}





