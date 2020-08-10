/**
 * CS321: Bioinformatics Group Project
 * 
 * BTreeObject class to store gene sequences
 * 
 * @author Alex Guy, Ryan Josephson, Andres Guzman
 *
 */
 
 public class BTreeObject{
 
 
 
	private int frequency;
	private long key;
	
	public BTreeObject(long key){
		
		this.key = key;
		frequency = 1;
		
	}
	
	public BTreeObject(long key, int freq){
		
		this.key = key;
		frequency = freq;
		
	}
	
	
	public int getFrequency(){
		
		return frequency;
	}
	
	public void setFrequency(int i){
		
		frequency = i;
	}
	
	public void incrementFrequency(){
		
		frequency++;
	}
 
 
	public long getKey(){
		
		return key;
	}
	public void setKey(long key){
		
		this.key = key;
	}
 
 }