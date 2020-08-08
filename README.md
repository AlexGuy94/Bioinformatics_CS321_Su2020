# Bioinformatics_CS321_Su2020

Laying out some thoughts on the classes.  (Feel free to update names)

Tree Object
Var: Long DNA sequence, int frequency

Methods: GetKey, SetKey, GetFrequency, SetFrequency, CompareTo(Tree), increaseFrequency


BTreeNode
Var: byte isLeaf, int NumberOfObjectsInNode, int byteLocationOfNode, degree, int parent pointer, Array<TreeObjects>, Array< int (child pointers)>
  
Methods: isleaf, setleaf, getNumObjects, setNumObjects, writeMetaData, isNodeFull, getParent, setParent, getChildren, setLocation, getLocation, getMaxBytes(for allocating space for a full node), readNode


BTree
Var: int degree, int sequenceLength, int RootLocation, RandomAccessFile bTreeFile,

Methods: Search(key), SplitNode, InsertTreeObject, readNodeFromFile, writeNodeToFile, writeMetaData, traverseTreeAndPrintToDump, TraverseTreeAndPrintToConsole,


GeneBankSearch
Var: 

Methods: ConvertDNAtoBytes, ConvertBytestoDNA,
