package relop;

import global.*;
import heap.HeapFile;
import bufmgr.*;
import java.util.*;
import java.lang.*;

public class Sort extends Iterator implements GlobalConst {
	/** The underlying iterator. */
	protected Iterator iter;

	/** The field of the table we are sorting on. */
	protected int sortfield;

	/** The memory used for internal sorting. */
	protected int sortMemSize;

	/** The total buffer size for the merging phase. */
	protected int bufSize;

	/** The buffer for the merging phase. */
	protected BufMgr buffer;

	/** The number of tuples per page. */
	protected int tuple_per_page;

	/** Temporary tuple used for a number of things. */
	protected Tuple temp_tuple;
	
	/** The comparator used for comparing two tuples. */
	protected Comparator<Tuple> tuple_Comparator;
	/**
	 * Constructs a sort operator. 
	 * @param sortMemSize the size the memory used for internal sorting. For simplicity, you can assume it is in the unit of tuples.
	 * @param bufSize the total buffer size for the merging phase in the unit of page.   
	 */
	public Sort(Iterator iter, int sortfield, int sortMemSize, int bufSize) {
		this.schema = iter.schema;
		this.iter = iter;
		this.sortfield = sortfield;
		this.sortMemSize = sortMemSize;
		this.bufSize = bufSize;
		buffer = new BufMgr(bufSize);
		tuple_per_page = PAGE_SIZE / iter.schema.getLength();

		Comparator<Tuple> tuple_Comparator = 
				new Comparator<Tuple>() {
			public int compare(Tuple t1, Tuple t2) {
				switch (iter.schema.fieldType(sortfield)) {

			      case AttrType.INTEGER:
			    	  return new Integer(t2.getIntFld(sortfield)).compareTo( new Integer(t1.getIntFld(sortfield)));
			        
			      case AttrType.FLOAT:
			    	  return new Float(t2.getFloatFld(sortfield)).compareTo( new Float(t1.getFloatFld(sortfield)));
			        
			      case AttrType.STRING:
			    	  return t2.getStringFld(sortfield).compareTo( t1.getStringFld(sortfield));
			        
			      default:
			        throw new IllegalStateException("invalid attribute type");
			    }
			}
		};
	}


	@Override
	public void explain(int depth) {
		throw new UnsupportedOperationException("Not implemented");

	}

	@Override
	public void restart() {
		throw new UnsupportedOperationException("Not implemented");

	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException("Not implemented");

	}

	@Override
	public void close() {
		throw new UnsupportedOperationException("Not implemented");

	}

	@Override
	public boolean hasNext() {
		// read from Iter, write to disk.
		// File size = bufsize-1 pages
		// Create pages with tuple_per_page tuples.
		
		while(true){
			/*
			Byte[] concat_data = new Byte[PAGE_SIZE];
			iter.hasNext();
			temp_tuple = iter.getNext();
			 */
			int i;
			Tuple[] tuples = new Tuple[tuple_per_page*bufSize];
			for(i=0; iter.hasNext() && i < tuples.length; i++){
				tuples[i] = iter.getNext();
			} // a run 0 worth of tuples are now in the array. i = the amount of tuples in the array.

			List<Tuple> tupleList = Arrays.asList(tuples);
			java.util.Collections.sort(tupleList, tuple_Comparator); // tuples are sorted, ready to write to file.
			
			HeapFile newHeap = new HeapFile(null);
			for(int j = 0; j < i; j++){
				newHeap.insertRecord(tupleList.get(j));
			}
			
			//temp_tuple.

		}
	}

	@Override
	public Tuple getNext() {
		throw new UnsupportedOperationException("Not implemented");

	}

}
