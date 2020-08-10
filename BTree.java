import java.io.RandomAccessFile;

public class BTree {

	private int degree;
	private BTreeNode root;
	private RandomAccessFile bTreeFile;
	private String fileName;
	
	
	
	public BTree(int degree, String fileName) {
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
	
	//Search BTree
	
	
}
