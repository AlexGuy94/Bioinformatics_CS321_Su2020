/**
 * CS321: Bioinformatics Group Project
 * 
 * BTreeNode class defining nodes for holding
 * TreeObjects in a BTree
 * 
 * @author Alex Guy, Ryan Josephson, Andres Guzman
 *
 */

public class TreeNode {
	// ============================================================
	// Instance Variables
	// ============================================================
    
	private int parent, numKeys, loc, degree; 
    private int[] children; 
    private TreeNode[] nodes;
    private TreeObject[] treeObjects; 
    private boolean leaf;

	/**
	 * Default constructor
	 * @param t - the minimum degree of the BTree
	 */
    public TreeNode(int t) {
	
		this.setDegree(t);
		this.parent = 0;
		this.children = new int[2*t];
		this.nodes = new TreeNode[2*t];
		this.treeObjects = new TreeObject[2*t-1];
		for (int i=0; i<treeObjects.length; i++) {
			treeObjects[i] = new TreeObject((long) (0));
		}
		this.numKeys = 0;
		this.leaf = true;
		this.loc = 0;
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
    public TreeObject getTreeObject(int i) {
		return this.treeObjects[i];
    }
	
	/**
	 * Sets treeObject at given index.
	 *
	 */	
	public void setTreeObject(int i, TreeObject treeObject) {
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

}