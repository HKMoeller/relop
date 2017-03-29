package relop;

/**
 * The projection operator extracts columns from a relation; unlike in
 * relational algebra, this operator does NOT eliminate duplicate tuples.
 */
public class Projection extends Iterator {
	/** The underlying iterator. */
	protected Iterator iter;

	/** Field numbers. */
	protected Integer[] fields;

	/** Next tuple to return. */
	protected Tuple next;

	/** Next tuple before projection. */
	protected Tuple nextBeforeProjection;

	/**
	 * Constructs a projection, given the underlying iterator and field numbers.
	 */
	public Projection(Iterator iter, Integer... fields) {
		this.iter = iter;
		this.fields = fields;
		next = null;
		schema = new Schema(fields.length);
		for (int i = 0; i < fields.length; i++) {
			schema.initField(i, iter.schema, fields[i]);
		}
	}

	/**
	 * Gives a one-line explaination of the iterator, repeats the call on any
	 * child iterators, and increases the indent depth along the way.
	 */
	public void explain(int depth) {
		indent(depth);
		System.out.print("Selection : ");
		if (fields.length > 0) {
			for (int i = 0; i < fields.length - 1; i++) {
				System.out.print(fields[i].toString() + " AND ");
			}
			System.out.println(fields[fields.length - 1]);
		} else {
			System.out.println("(project nothing)");
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
			nextBeforeProjection = iter.getNext();

			next = new Tuple(schema);

			for (int i = 0; i < fields.length; i++) {
				next.setField(i, nextBeforeProjection.getField(fields[i]));
			}
			return true;

		} // while

		// No more tuples
		next = null;
		return false;
	}

	/**
	 * Gets the next tuple in the iteration.
	 * 
	 * @throws IllegalStateException
	 *             if no more tuples
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

} // public class Projection extends Iterator
