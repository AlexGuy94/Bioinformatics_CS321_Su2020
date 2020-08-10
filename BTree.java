import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree {

	private int degree;
	private BTreeNode root;
	private RandomAccessFile bTreeFile;
	
	
	/**
	 * Constructor for BTree
	 * 
	 * @param degree
	 * @param bTreeFile
	 */
	public BTree(int degree, RandomAccessFile bTreeFile) {
		this.degree = degree;
		root = new BTreeNode(degree);
		this.bTreeFile = bTreeFile;
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
		BTreeNode node = new BTreeNode(degree);
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
		if (i<=node.getnumKeys() && key==node.getBTreeObject(i).getKey()) {
			return node.getBTreeObject(i).getFrequency();
		}
		if(node.isLeaf()) {
			return 0;
		} else {
			node = readNode(node.getChild(i));
			return BTreeSearch(node, key);
		}
	}
	
	
	
	//Split-Child
	private void childSplit(BTreeNode parentNode, int index, BTreeNode childNode) throws IOException {
		int newNodeLocation = (int) bTreeFile.length();
		BTreeNode newChildNode = new BTreeNode(newNodeLocation);
		newChildNode.setLeaf(childNode.isLeaf());
		newChildNode.setnumKeys(degree-1);
		for (int j=0;j<degree-1;j++) {
			newChildNode.setTreeObject(j, childNode.getBTreeObject(j+degree));
		}
		if (!childNode.isLeaf()) {
			for(int j=0;j<degree;j++) {
				newChildNode.setChildPointer(j, childNode.getChild(j+degree));
			}
		}
		childNode.setnumKeys(degree-1);
		for(int j=parentNode.getnumKeys();j>index;j--) {
			parentNode.setChildPointer(j, parentNode.getChild(j-1));
		}
		parentNode.setChildPointer(index+1, newChildNode.getLoc());
		for(int j=parentNode.getnumKeys();j>=index;j--) {
			parentNode.setTreeKey(j+1, parentNode.getBTreeObject(index).getKey());
		}
		parentNode.setTreeObject(index, childNode.getBTreeObject(degree));
		parentNode.setnumKeys(parentNode.getnumKeys()+1);
		writeNode(parentNode);
		writeNode(childNode);
		writeNode(newChildNode);
		
		
	}
	
	
	//Insert
	public void insert(BTree tree, long key) throws IOException {
	
		BTreeNode r = tree.getRoot();
		if(r.getnumKeys()==2*degree-1) {
			int newNodeLocation = (int) bTreeFile.length();
			BTreeNode newNode = new BTreeNode(newNodeLocation);
			tree.setRoot(newNode);
			newNode.setLeaf(false);
			newNode.setnumKeys(0);
			newNode.setChildPointer(0, r.getLoc());
			childSplit(newNode, 1, r);
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
				node.setTreeKey(i+1, node.getBTreeObject(i).getKey());
				i--;
			}
			node.setTreeKey(i+1, key);
			node.setnumKeys(node.getnumKeys()+1);
			writeNode(node);
		} else {
			while(i>=1 && key<node.getBTreeObject(i).getKey()) {
				i--;
			}
			i++;
			BTreeNode childNode = readNode(node.getChild(i));
			
			if(childNode.getnumKeys()==2*degree-1) {
				childSplit(node, i, childNode);
				if(key>node.getBTreeObject(i).getKey()) {
					i++;
				}
			}
			insertNonFull(childNode, key);
		}
		
	}
}





