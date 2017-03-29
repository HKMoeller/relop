package relop;

/**
 * The selection operator specifies which tuples to retain under a condition; in
 * Minibase, this condition is simply a set of independent predicates logically
 * connected by OR operators.
 */
public class Selection extends Iterator {
	  /** The underlying iterator. */
	  protected Iterator iter;
	  
	  /** Predicates to evaluate. */
	  protected Predicate[] preds;

	  /** Next tuple to return. */
	  protected Tuple next;
  /**
   * Constructs a selection, given the underlying iterator and predicates.
   */
  public Selection(Iterator iter, Predicate... preds) {
    schema = iter.schema;
    this.iter = iter;
    this.preds = preds;
    next = null;
  }

  /**
   * Gives a one-line explanation of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public void explain(int depth) {
	  indent(depth);
	  System.out.print("Selection : ");
	  if (preds.length > 0) {
	      for (int i = 0; i < preds.length - 1; i++) {
	        System.out.print(preds[i].toString() + " OR ");
	      }
	      System.out.println(preds[preds.length - 1]);
	    } else {
	      System.out.println("(select all)");
	    }
	  iter.explain(depth + 1);
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public void restart() {
	  iter.restart();
	  next = null;
  }

  /**
   * Returns true if the iterator is open; false otherwise.
   */
  public boolean isOpen() {
	  return (iter != null);
  }

  /**
   * Closes the iterator, releasing any resources (i.e. pinned pages).
   */
  public void close() {
	  if (iter != null) {
	      iter.close();
	      iter = null;
	    }
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext() {
	      // in general, continue looping through the right
	      while (iter.hasNext()) {

	        // try to pass the next tuple
	        next = iter.getNext();
	        if (preds.length == 0) {
	          return true;
	        }
	        for (int i = 0; i < preds.length; i++) {

	        	// if it passes, return found
	          if (preds[i].evaluate(next)) {
	            return true;
	          }

	        } // for

	      } // while
	      
	      // No more tuples
	      next = null;
	      return false;
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
	    // validate the next tuple
	    if (next == null) {
	      throw new IllegalStateException("no more tuples");
	    }

	    // return (and forget) the tuple
	    Tuple tuple = next;
	    next = null;
	    return tuple;

	  } // public Tuple getNext()

} // public class Selection extends Iterator
