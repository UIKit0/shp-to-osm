/**
 * 
 */
package net.sourceforge.capcode.S57Library.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.sourceforge.capcode.S57Library.basics.E_DataStructure;
import net.sourceforge.capcode.S57Library.basics.E_VerticalDatum;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldATTF;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldATTV;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldCATD;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldDSID;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldDSPM;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldDSSI;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldDefinitionTable;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldFFPT;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldFOID;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldFRID;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldFSPT;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldNATF;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldSG2D;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldSG3D;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldVRID;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldVRPT;
import net.sourceforge.capcode.S57Library.fiedsRecords.descriptive.S57DDRDataDescriptiveField;
import net.sourceforge.capcode.S57Library.fiedsRecords.descriptive.S57DDRField;
import net.sourceforge.capcode.S57Library.objects.S57Edge;
import net.sourceforge.capcode.S57Library.objects.S57Feature;
import net.sourceforge.capcode.S57Library.objects.S57Object;
import net.sourceforge.capcode.S57Library.objects.S57ObjectsVector;
import net.sourceforge.capcode.S57Library.objects.S57Spatial;
import net.sourceforge.capcode.S57Library.records.S57DataDescriptiveRecord;
import net.sourceforge.capcode.S57Library.records.S57DataRecord;
import net.sourceforge.capcode.S57Library.records.S57LogicalRecord;

/**
 * @author cyrille
 *
 */
public class S57ModuleReader{
	protected String fileName;
	public S57FieldDefinitionTable fieldDefinitions;
	protected S57ObjectsVector vectors;
	protected S57ObjectsVector features;
	//used to store the current object being analysed in the file
	public S57Object currentObject = null;
	private String intendedUsage;
	protected String dataSetName;
	private String editionNumber;
	private String updateNumber;
	private boolean showProgressWindow;
	private E_VerticalDatum verticalDatum;
	
	private double coordinateMultiplicationFactor;
	protected int scale;
	private double soundingMultiplicationFactor;
	private E_DataStructure dataStructure;
	private int lexicalLevel;
	private int nationalLexicalLevel;
	private int numberOfMetaRecords;
	private int numberOfCartographicRecords;
	private int numberOfGeoRecords;
	private int numberOfCollectionrecords;
	private int numberOfIsolatedNodes;
	private int numberOfConnectedNodes;
	private int numberOfEdgeRecords;
	private int numberOfFaceRecords;
	private boolean canceled = false;
	private ByteBuffer buffer;
	private boolean DSIDavailable = false;
	private boolean DSPMavailable = false;
	
	public S57ModuleReader(){
		super();
		fieldDefinitions = new S57FieldDefinitionTable(50);
		vectors = new S57ObjectsVector();
		features = new S57ObjectsVector();
	}
				
	public S57ModuleReader(String filename) throws Exception {
		this();
		load(filename);
	}

	/**
	 * reads the LogicalRecord at the index position.<br>
	 * the first 5 bytes at the index must give the size of the LogicalRecord.
	 * the first record is the Data Descriptive Record.
	 * all the following ones are Data record.
	 * the fields read from data record are organised and classified in the field objects.
	 * @param index : position in the file where should start a LogicalRecord 
	 * @return a LogicalRecord class
	 * @throws Exception 
	 */
	private S57LogicalRecord readRecordAtIndex(int index) throws Exception {
		int size = extractRecordSize(index);	
		byte[] array = new byte[size];
		buffer.get(array);
//		S57ByteBuffer record = new S57ByteBuffer(array);			
		if (index == 0){
//			return new S57DataDescriptiveRecord(record, fieldDefinitions);			
			return new S57DataDescriptiveRecord(array, fieldDefinitions);			
		}
		//implicite else: index is not 0
//		return new S57DataRecord(record, this);
		return new S57DataRecord(array, this);
	}

	/**
	 * extract the record size from the file.
	 * then return to the position. This is used locally to allocate the exact size needed
	 * for a record.
	 * @param index
	 * @param size
	 * @return an integer representing the size of the record to read.
	 * @throws IOException
	 */
	private int extractRecordSize(int index) throws IOException {
		byte[] b = new byte[5];
		int oldPos = buffer.position();
		buffer.position(index);
		buffer.get(b);
		buffer.position(oldPos);
		String s = S57ByteBuffer.getString(b, 5);
		return Integer.parseInt(s);
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	public S57ObjectsVector getFeatures() {
		return features;
	}
	
	public S57ObjectsVector getSpatials() {
		return vectors;
	}

	/**
	 * Update the record with informations found in the DSID field.<br>
	 *see IHO S-57 section 6.3.2.1 for more details on DSID
	 * @param dsid a DSID field
	 */
	public void setDSID(S57FieldDSID dsid) {
		setIntendedUsage(dsid.intendedUsage.name());
		setDataSetName(dsid.datasetName);
		editionNumber = dsid.editionNumber;
		updateNumber = dsid.updateNumber;
		DSIDavailable = true;
	}
	
	/**
	 * Update the record with informations found in the DSPM field.<br>
	 *see IHO S-57 section 6.3.2.3 for more details on DSPM
	 * @param dspm a DSPM field
	 */
	public void setDSPM(S57FieldDSPM dspm) {
		verticalDatum = E_VerticalDatum.byCode(dspm.verticalDatum);
		coordinateMultiplicationFactor = (double)(1 / (double)dspm.coordinateMultiplicationFactor);
		setSoundingMultiplicationFactor((double) (1 / (double)dspm.soundingMultiplicationFactor));
		setScale(dspm.compilationScaleOfData);
		DSPMavailable = true;
	}
	
	/**
	 * Update the record with informations found in the DSSI field.<br>
	 *see IHO S-57 section 7.3.1.2 for more details on DSSI
	 * @param dssi a DSSI field
	 */
	public void setDSSI(S57FieldDSSI dssi) {
		setDataStructure(E_DataStructure.byCode(dssi.dataStructure));
		setLexicalLevel(dssi.attfLexicalLevel);
		setNationalLexicalLevel(dssi.nationalLexicalLevel);
		numberOfMetaRecords = dssi.numberOfMetaRecords;
		numberOfCartographicRecords = dssi.numberOfCartographicRecords;
		numberOfGeoRecords = dssi.numberOfGeoRecords;
		numberOfCollectionrecords = dssi.numberOfCollectionrecords;
		numberOfIsolatedNodes = dssi.numberOfIsolatedNodes;
		numberOfConnectedNodes = dssi.numberOfConnectedNodes;
		numberOfEdgeRecords = dssi.numberOfEdgeRecords;
		numberOfFaceRecords = dssi.numberOfFaceRecords;
		
	}	


	/**
	 * @param intendedUsage the intendedUsage to set
	 */
	public void setIntendedUsage(String intendedUsage) {
		this.intendedUsage = intendedUsage;
	}

	/**
	 * @return the intendedUsage
	 */
	public String getIntendedUsage() {
		return intendedUsage;
	}

	/**
	 * @param dataSetName the dataSetName to set
	 */
	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	/**
	 * @return the dataSetName
	 */
	public String getDataSetName() {
		return dataSetName;
	}

	/**
	 * @return the editionNumber
	 */
	public String getEditionNumber() {
		return editionNumber;
	}

	/**
	 * @param editionNumber the editionNumber to set
	 */
	public void setEditionNumber(String editionNumber) {
		this.editionNumber = editionNumber;
	}

	/**
	 * @return the updateNumber
	 */
	public String getUpdateNumber() {
		return updateNumber;
	}

	/**
	 * @param updateNumber the updateNumber to set
	 */
	public void setUpdateNumber(String updateNumber) {
		this.updateNumber = updateNumber;
	}
	
	/**
	 * @return the showProgressWindow
	 */
	public boolean isShowProgressWindow() {
		return showProgressWindow;
	}

	/**
	 * @param showProgressWindow the showProgressWindow to set
	 */
	public void setShowProgressWindow(boolean showProgressWindow) {
		this.showProgressWindow = showProgressWindow;
	}
	
/**
	 * @return the verticalDatum
	 */
	public E_VerticalDatum getVerticalDatum() {
		return verticalDatum;
	}

	/**
	 * @param verticalDatum the verticalDatum to set
	 */
	public void setVerticalDatum(E_VerticalDatum verticalDatum) {
		this.verticalDatum = verticalDatum;
	}

	/**
	 * @return the coordinateMultiplicationFactor
	 */
	public double getCoordinateMultiplicationFactor() {
		return coordinateMultiplicationFactor;
	}

	/**
	 * @param coordinateMultiplicationFactor the coordinateMultiplicationFactor to set
	 */
	public void setCoordinateMultiplicationFactor(
			double coordinateMultiplicationFactor) {
		this.coordinateMultiplicationFactor = coordinateMultiplicationFactor;
	}

	/**
	 * @param soundingMultiplicationFactor the soundingMultiplicationFactor to set
	 */
	public void setSoundingMultiplicationFactor(double soundingMultiplicationFactor) {
		this.soundingMultiplicationFactor = soundingMultiplicationFactor;
	}

	/**
	 * @return the soundingMultiplicationFactor
	 */
	public double getSoundingMultiplicationFactor() {
		return soundingMultiplicationFactor;
	}

	/**
	 * @param dataStructure the dataStructure to set
	 */
	public void setDataStructure(E_DataStructure dataStructure) {
		this.dataStructure = dataStructure;
	}

	/**
	 * @return the dataStructure
	 */
	public E_DataStructure getDataStructure() {
		return dataStructure;
	}

	/**
	 * @param lexicalLevel the lexicalLevel to set
	 */
	public void setLexicalLevel(int lexicalLevel) {
		this.lexicalLevel = lexicalLevel;
	}

	/**
	 * @return the lexicalLevel
	 */
	public int getLexicalLevel() {
		return lexicalLevel;
	}

	/**
	 * @param nationalLexicalLevel the nationalLexicalLevel to set
	 */
	public void setNationalLexicalLevel(int nationalLexicalLevel) {
		this.nationalLexicalLevel = nationalLexicalLevel;
	}

	/**
	 * @return the nationalLexicalLevel
	 */
	public int getNationalLexicalLevel() {
		return nationalLexicalLevel;
	}

	/**
	 * @return the numberOfMetaRecords
	 */
	public int getNumberOfMetaRecords() {
		return numberOfMetaRecords;
	}

	/**
	 * @param numberOfMetaRecords the numberOfMetaRecords to set
	 */
	public void setNumberOfMetaRecords(int numberOfMetaRecords) {
		this.numberOfMetaRecords = numberOfMetaRecords;
	}

	/**
	 * @return the numberOfCartographicRecords
	 */
	public int getNumberOfCartographicRecords() {
		return numberOfCartographicRecords;
	}

	/**
	 * @param numberOfCartographicRecords the numberOfCartographicRecords to set
	 */
	public void setNumberOfCartographicRecords(int numberOfCartographicRecords) {
		this.numberOfCartographicRecords = numberOfCartographicRecords;
	}

	/**
	 * @return the numberOfGeoRecords
	 */
	public int getNumberOfGeoRecords() {
		return numberOfGeoRecords;
	}

	/**
	 * @param numberOfGeoRecords the numberOfGeoRecords to set
	 */
	public void setNumberOfGeoRecords(int numberOfGeoRecords) {
		this.numberOfGeoRecords = numberOfGeoRecords;
	}

	/**
	 * @return the numberOfCollectionrecords
	 */
	public int getNumberOfCollectionrecords() {
		return numberOfCollectionrecords;
	}

	/**
	 * @param numberOfCollectionrecords the numberOfCollectionrecords to set
	 */
	public void setNumberOfCollectionrecords(int numberOfCollectionrecords) {
		this.numberOfCollectionrecords = numberOfCollectionrecords;
	}

	/**
	 * @return the numberOfIsolatedNodes
	 */
	public int getNumberOfIsolatedNodes() {
		return numberOfIsolatedNodes;
	}

	/**
	 * @param numberOfIsolatedNodes the numberOfIsolatedNodes to set
	 */
	public void setNumberOfIsolatedNodes(int numberOfIsolatedNodes) {
		this.numberOfIsolatedNodes = numberOfIsolatedNodes;
	}

	/**
	 * @return the numberOfConnectedNodes
	 */
	public int getNumberOfConnectedNodes() {
		return numberOfConnectedNodes;
	}

	/**
	 * @param numberOfConnectedNodes the numberOfConnectedNodes to set
	 */
	public void setNumberOfConnectedNodes(int numberOfConnectedNodes) {
		this.numberOfConnectedNodes = numberOfConnectedNodes;
	}

	/**
	 * @return the numberOfEdgeRecords
	 */
	public int getNumberOfEdgeRecords() {
		return numberOfEdgeRecords;
	}

	/**
	 * @param numberOfEdgeRecords the numberOfEdgeRecords to set
	 */
	public void setNumberOfEdgeRecords(int numberOfEdgeRecords) {
		this.numberOfEdgeRecords = numberOfEdgeRecords;
	}

	/**
	 * @return the numberOfFaceRecords
	 */
	public int getNumberOfFaceRecords() {
		return numberOfFaceRecords;
	}

	/**
	 * @param numberOfFaceRecords the numberOfFaceRecords to set
	 */
	public void setNumberOfFaceRecords(int numberOfFaceRecords) {
		this.numberOfFaceRecords = numberOfFaceRecords;
	}

	public void load(String aFileName) throws Exception {
		int index = 0;
		this.fileName = aFileName;
		int recNum = 0;
		int size = (int) new File(aFileName).length();
		FileInputStream fis = new FileInputStream(aFileName);
		FileChannel fc = fis.getChannel();
		buffer = ByteBuffer.allocate(size);
		callBackInit(size);
		//read the whole file into the byteBuffer
		fc.read(buffer);
		fc.close();
		buffer.rewind();
		while (buffer.position() < size && !canceled){
			S57LogicalRecord record = readRecordAtIndex(index);
			if(!record.isValid()){
				throw new S57ReadException(String.format("invalid record at rec N: %d (@%d)", recNum, index));
			}
			recNum++;
			index += record.getRecordLength();
			callBackWhileLoading(record, index);
		}
		callBackEnd();
	}

	public void extractGeneralInformation(String aFileName) throws Exception {
		int index = 0;
		int recNum = 0;
		int size = (int) new File(aFileName).length();
		FileInputStream fis = new FileInputStream(aFileName);
		FileChannel fc = fis.getChannel();
		buffer = ByteBuffer.allocate(size);
		fc.read(buffer);
		fc.close();
		buffer.rewind();
		while (buffer.position() < size && !canceled){
			S57LogicalRecord record = readRecordAtIndex(index);
			if (DSIDavailable && DSPMavailable){
				break;
			}
			index += record.getRecordLength();
		}
	}
	
	/**
	 * Override this method to do something particular 
	 * before the load really starts (progress window for example)		
	 * @param size : the size of the file to read
	 */
	protected void callBackInit(int size) {
		//do nothing here
	}

	/**
	 * Override this method to do something particular 
	 * during the load (progress window update, for example)
	 * @param record 
	 * @param index : the actual position in the file		
	 */
	protected void callBackWhileLoading(S57LogicalRecord record, int index) {
		//do nothing here
	}
	
	/**
	 * Override this method to do something particular 
	 * at the end of the load process (terminate progress info)		
	 */
	protected void callBackEnd() {
		//do nothing here
	}	
	
	public void cancel(){
		canceled = true; // this should stop the load process
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	public void newCatEntryCallBack(S57FieldCATD catd) {
		// do nothing, called by S57DataRecord
		
	}

}