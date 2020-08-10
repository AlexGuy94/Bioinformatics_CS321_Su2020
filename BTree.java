import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree {

	private int degree;
	private BTreeNode root;
	private RandomAccessFile bTreeFile;
	private String fileName;
	
	
	
	public BTree(int degree, RandomAccessFile bTreeFile) {
		this.degree = degree;
		root = new BTreeNode(degree);
		this.fileName = fileName;
		
		
	}

	
	public BTreeNode getRoot() {
		return root;
	}
	
	public void setRoot(BTreeNode root) {
		
		this.root = root;
	}
	
	public BTreeNode readNode(int location) throws IOException {
		
		bTreeFile.seek(location);
		BTreeNode node = new BTreeNode(degree);
		node.readNode(bTreeFile);
		return node;
		
	}
	
	//Search BTree
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
	
	//Insert
	
	//Insert Non-Full
}
