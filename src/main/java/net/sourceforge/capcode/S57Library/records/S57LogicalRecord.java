/**
 * 
 */
package net.sourceforge.capcode.S57Library.records;

import java.io.IOException;

import net.sourceforge.capcode.S57Library.fiedsRecords.descriptive.S57DDRField;
import net.sourceforge.capcode.S57Library.files.S57ByteBuffer;

/**
 * @author cyrille
 *
 */
public abstract class S57LogicalRecord {
	public static final int HEADER_RECORD_SIZE = 24;
	protected byte[] header = new byte[HEADER_RECORD_SIZE];


	protected boolean valid = true;
	protected int recordLength;
//	protected int interchangeLevel;
//	protected char identifier;
//	protected char codeExtensionIndicator;
//	protected int versionNumber;
//	protected byte applicationIndicator;
	protected int fieldControlLength = 9;
	protected int fieldAreaBaseAddress;
	protected String extendedCharSetIndicator;
	protected S57DataRecordEntryMap entryMap;
//	protected S57ByteBuffer buffer;
	protected byte[] array;
	
	
	public S57LogicalRecord(){
		super();
	}
	
//	public S57LogicalRecord(S57ByteBuffer buffer) throws IOException {
//		this();
//		this.buffer = buffer;
//		buffer.getByteArray(header, 0);
//		recordLength  = buffer.decodeInteger(0, 5);
//		interchangeLevel = buffer.decodeInteger(1);
//		identifier = (char) buffer.getByte();
//		codeExtensionIndicator = (char) buffer.getByte();
//		versionNumber = buffer.decodeInteger(1);
//		applicationIndicator = buffer.getByte();
//		fieldControlLength = buffer.decodeInteger(2);
//		fieldAreaBaseAddress = buffer.decodeInteger(5);
//		extendedCharSetIndicator = buffer.getString (3);
//		entryMap = new S57DataRecordEntryMap(buffer, 20);
//		valid = checkValidity();
//	}

	public S57LogicalRecord(byte[] array) {
		this();
		this.array = array;
		recordLength  = S57ByteBuffer.getInteger(array, 0, 5);
		fieldAreaBaseAddress = S57ByteBuffer.getInteger(array, 12, 5);
		extendedCharSetIndicator = S57ByteBuffer.getString(array, 17, 3);
		entryMap = new S57DataRecordEntryMap(array, 20);
	}

	protected abstract boolean checkValidity();

	public boolean isValid() {
		return valid;
	}

	/**
	 * @return the recordLength
	 */
	public int getRecordLength() {
		return recordLength;
	}

//	public int getDirectoryEntriesCount() {
//		int res = 0;
//		int width = this.getFieldEntryWidth();
//		for(int i = HEADER_RECORD_SIZE; i < recordLength; i += width ){
//			if( buffer.getByte(i) == S57DDRField.FIELD_TERMINATOR)
//				break;
//			res++;
//		}
//		return res;
//	}


	/**
	 * @return the fieldControlLength
	 */
	public int getFieldControlLength() {
		return fieldControlLength;
	}


	/**
	 * @return the fieldAreaBaseAddress
	 */
	public int getFieldAreaStart() {
		return fieldAreaBaseAddress;
	}


	/**
	 * @return the extendedCharSetIndicator
	 */
	public String getExtendedCharSet() {
		return extendedCharSetIndicator;
	}


	/**
	 * @return the size of the field size.
	 * The field size is coded in the directory of every LogicalRecord
	 * this is used to decode the directory fields (tag, length, offset) 
	 */
	public int getFieldLengthSize() {
		return entryMap != null ? entryMap.fieldLengthSize : 0;
	}


	/**
	 * @return the size of the field offset.
	 * The field size is coded in the directory of every LogicalRecord
	 * this is used to decode the directory fields (tag, length, offset) 
	 */
	public int getFieldOffsetSize() {
		return entryMap != null ? entryMap.fieldPosSize : 0;
	}


	/**
	 * @return the size of the field tag (name).
	 * The field size is coded in the directory of every LogicalRecord
	 * this is used to decode the directory fields (tag, length, offset) 
	 */
	public int getFieldTagSize() {
		return entryMap != null ? entryMap.fieldTagSize : 0;
	}


	public int getFieldEntryWidth() {
		return entryMap != null ? entryMap.getWidth() : 0;
	}


	public String toString(){
		return String.format("length %d, map: %s", recordLength, entryMap.toString());
	}

	public int getHeaderSize() {
		return HEADER_RECORD_SIZE;
	}
}
