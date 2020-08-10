import java.util.LinkedList;
public class Cache {

	LinkedList<Object> list;
	int len;
	public int hitcount=0;
	public int misscount=0;
	public int total = hitcount+misscount;

	
	/**
	 * Constructor
	 * 
	 * @param length
	 */
	public Cache(int length) {
		len = length;
		list = new LinkedList<Object>();
	}

	/**
	 * Finds the index of a BTreeNode
	 * 
	 * @param searchterm
	 * @return
	 */
	public int findIndex(Object searchterm) {
		int index = this.list.indexOf(searchterm);
		System.out.println(searchterm);
		if(index!=-1) {
				this.hitcount++;
				total = hitcount+misscount;
				return index;
			
		}else {
			this.misscount++;
			total = hitcount+misscount;
			return index;
		}

	}

	/**
	 * Gets the BTreeNode at a specific index.
	 * 
	 * @param searchterm
	 * @return returns object being searched.
	 */
	public Object getObject(int index){
		return list.get(index);

	};

	/**
	 * Adds and obejct to the top of the Cache
	 * 
	 * @param obj
	 */
	public void addObject(Object obj) {
		list.addFirst(obj);
		if(list.size() >= len) {
			this.removeObject(len-1);
		}


	}

	/**
	 * Moves a BTreeNode to the top of the cache.  Use when found after a 
	 * cache search.
	 * @param index
	 */
	public void moveToTop(int index) {
		this.addObject(this.removeObject(index));
	}

	/**
	 * remove and object from the cache.  Used when the cache is full.
	 * 
	 * @param index
	 */
	public Object removeObject(int index) {
		Object obj = this.list.remove(index);
		return obj;
	}


	/**
	 * Clear the cache if needed.
	 */
	public void clearCache() {
		list.clear();
	}
}

