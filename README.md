# Bioinformatics_CS321_Su2020
CS321FinalProject


Team Members
Alex Guy
Ryan Josephson
Andres Guzman

Notes

There is a cache, however it is not supported.
Dump is not working as intended.

Files

Btree.java: An implementation of a BTree.
BTreeNode: A Node class used by the BTree.
BTreeObject.java
GeneBankCreateBTree.java: Creates the BTree file
GeneBankSearch.java: Searches the BTree 

Compile and Run
java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length>
[<cache size>] [<debug level>]

java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>]
[<debug level>]


Usage Example: $ java GeneBankCreateBTree 0 6 test2.gbk 6

