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
	
	public void writeNode(BTreeNode node) throws IOException {
		bTreeFile.seek(node.getLoc());
		node.writeNode(bTreeFile);
		
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
	
	//Insert Non-Full
}
