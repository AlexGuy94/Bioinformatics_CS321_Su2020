import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * CS321: Bioinformatics Group Project
 * 
 * BTreeNode class defining nodes for holding
 * TreeObjects in a BTree
 * 
 * @author Alex Guy, Ryan Josephson, Andres Guzman
 *
 */

public class BTreeNode {
	// ============================================================
	// Instance Variables
	// ============================================================
    
	private int parent, numKeys, loc, degree; 
    private int[] children; 
    private BTreeNode[] nodes;//is this needed?  I think we should have nodes stored in the tree. The "children" pointers are pointing to the nodes.
    private BTreeObject[] treeObjects; 
    private boolean leaf;

	/**
	 * Default constructor
	 * @param t - the minimum degree of the BTree
	 */
    public BTreeNode(int t) {
	
		this.setDegree(t);
		this.parent = 0;
		this.children = new int[2*t];
		this.nodes = new BTreeNode[2*t]; //suggest removing
		this.treeObjects = new BTreeObject[2*t-1];
		for (int i=0; i<treeObjects.length; i++) {
			treeObjects[i] = new BTreeObject((long) (0));
		}
		this.numKeys = 0;
		this.leaf = true;
		this.loc = 0; //suggest changing to 12, there are 12 bytes of metadata in the tree so the first node should start at position 12.
    }



	// ============================================================
	// Class Methods & Getters/Setters
	// ============================================================
	
	/**
	 * Gets pointer for parent.
	 *
	 */
    public int getparent() {
		return parent;
    }
	
	/**
	 * Sets pointer for parent.
	 *
	 */
	public void setParent(int parent) {
		this.parent = parent;
    }
	
	/**
	 * Gets treeObject at given index.
	 *
	 */
    public BTreeObject getBTreeObject(int i) {
		return this.treeObjects[i];
    }
	
	/**
	 * Sets treeObject at given index.
	 *
	 */	
	public void setTreeObject(int i, BTreeObject treeObject) {
		this.treeObjects[i] = treeObject;
    }

	/**
	 * Gets pointer for child node.
	 *
	 */
    public int getChild(int i) {
		return this.children[i];
    }

	/**
	 * Sets pointer for child node.
	 *
	 */
    public void setChildPointer(int i, int childloc) {
		this.children[i] = childloc;
    }

	/**
	 * Gets number of keys.
	 *
	 */
    public int getnumKeys() {
		return numKeys;
    }

	/**
	 * Sets number of keys.
	 *
	 */
    public void setnumKeys(int numKeys) {
		this.numKeys = numKeys;
    }

	/**
	 * Increments the number of keys.
	 *
	 */
    public void incrementnumKeys() {
		numKeys++;
    }

	/**
	 * Sets node's key at given index.
	 *
	 */
    public void setTreeKey(int i, long key) {
		this.treeObjects[i].setBinaryKey(key);
    }

	/**
	 * Gets condition of key from treeObject at given index.
	 *
	 */
    public int getNodeKeyCondition(int i) {
		return this.treeObjects[i].getKeyCondition();
    }

	/**
	 * Sets conditon of key from treeObject at given index.
	 *
	 */
    public void setNodeKeyCondition(int i, int condition) {
		this.treeObjects[i].setKeyCondition(condition);
    }

	/**
	 * Gets frequency of treeObject at given index.
	 *
	 */
    public int getTreefrequency(int i) {
		return this.treeObjects[i].getFrequency();
    }

	/**
	 * Sets frequency of treeObject at given index.
	 *
	 */
    public void setTreefrequency(int i, int frequency) {
		this.treeObjects[i].setFrequency(frequency);
    }

	/**
	 * Returns boolean signifying whether node is a leaf.
	 *
	 */
    public boolean isLeaf() {
		return leaf;
    }

	/**
	 * Sets whether tree node is a leaf.
	 *
	 */
    public void setLeaf(boolean leaf) {
		this.leaf = leaf;
    }

	/**
	 * Gets location of node.
	 *
	 */
    public int getLoc() {
		return loc;
    }

	/**
	 * Sets location of node.
	 *
	 */
    public void setLoc(int loc) {
		this.loc = loc;
    }

	/**
	 * Sets child node.
	 *
	 */
    public void setNode(int i, TreeNode child) {
		this.nodes[i] = child;
    }

	/**
	 * gets child node at given index.
	 *
	 */
    public TreeNode getNode(int i) {
		return this.nodes[i];
    }

	/**
	 * Sets degree of node.
	 *
	 */
    public void setDegree(int degree) {
		this.degree = degree;
    }

	/**
	 * Gets degree of node.
	 *
	 */
    public int getDegree() {
		return degree;
    }
	
	/**
	 * Gets number of bytes in node.
	 * objects: (2*degree-1)*(long+freq)
	 * children: (2*degree)*(location)
	 * parent, numkeys, location: 4 each
	 * leaf?: 1
	 */
    public int bytesInNode(){
    		int num = (2*degree-1)*(8+4)+(2*degree)*4+4+4+4+1;
		return num;
    }
	
	/**
	 * reads a Node from file
	 * Order: numKeys, location, parent pointer, leaf status, treeObjects(long key, int freq), childpointers
	 * @throws IOException 
	 *
	 */
    public void readNode(RandomAccessFile bTreeFile) throws IOException{
	    ByteBuffer buffer = ByteBuffer.allocate(this.bytesInNode());
	    bTreeFile.read(buffer.array());
	   
	    //check ordering on these.
	    
	    numKeys = buffer.getInt();
	    loc = buffer.getInt();
	    parent = buffer.getInt();
	    leaf = (buffer.get()!=0);
	    
	    //for loops are somewhat inefficient having to iterate over unfilled nodes
	    for(int i=0;i<treeObjects.length;i++){
		    long key = buffer.getLong();
		    int frequency = buffer.getInt();
		    BTreeObject treeObject = new BTreeObject(key);
		    treeObjects[i] = treeObject;
	    }
	    for(int i=0;i<children.length;i++){
		    children[i] = buffer.getInt();
		    
		    
	    
	    }
		
    }
    
    public void writeNode(RandomAccessFile bTreeFile) throws IOException {
    	ByteBuffer buffer = ByteBuffer.allocate(bytesInNode());
    	buffer.putInt(numKeys);
	    buffer.putInt(loc);
	    buffer.putInt(parent);
	    buffer.put((byte)(leaf?1:0));
	    for(int i=0;i<treeObjects.length;i++) {
	    	buffer.putLong(treeObjects[i].getKey());
	    	buffer.putInt(treeObjects[i].getFrequency());
    }
	    for(int i=0;i<children.length;i++){
	    	 buffer.putInt(children[i]);
	    }
	    buffer.flip();
	    bTreeFile.seek(loc);
	    bTreeFile.write(buffer.array()); 
    }
}





