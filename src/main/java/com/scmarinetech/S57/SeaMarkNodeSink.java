package com.scmarinetech.S57;

public interface SeaMarkNodeSink {

	/**
	 * Called when new data set is started
	 */
	void onDataSetStart();
	/**
	 * Called when new node is decoded
	 * @param node
	 */
	void onNodeDecoded( SeaMarkNode node);
	/**
	 * Called when 
	 */
	void onDataSetEnd();
	
}
