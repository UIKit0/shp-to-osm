package net.sourceforge.capcode.S57Library.files;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import net.sourceforge.capcode.S57Library.fiedsRecords.S57DataField;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57SubFieldFormat;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57SubFieldFormat.FormatType;

import org.junit.Test;

public class S57DataFieldTest {

	@Test
	public void testGetValue() {
		class Test extends S57DataField{
				public Test(){
					super("test", null, null);
				}
				@Override
				public void decode() {
					byte[] b = new byte[]{49,50,51};
					assertEquals("123", getSubFieldValue(b, new S57SubFieldFormat(FormatType.ascii, 4)));
					assertEquals(123, getSubFieldValue(b, new S57SubFieldFormat(FormatType.integer, 4)));
					assertEquals(123.0, getSubFieldValue(b, new S57SubFieldFormat(FormatType.real, 4)));
					
					b = reverseArray(BigInteger.valueOf(125).toByteArray());
					assertEquals(125, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryUnsignedInteger, 4)));

					b = reverseArray(BigInteger.valueOf(-1024).toByteArray());
					assertEquals(-1024, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryUnsignedInteger, 4)));

					b = reverseArray(BigInteger.valueOf(-123456).toByteArray());
					assertEquals(-123456, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryUnsignedInteger, 4)));

					b = reverseArray(BigInteger.valueOf(Integer.MAX_VALUE).toByteArray());
					assertEquals(Integer.MAX_VALUE, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryUnsignedInteger, 4)));	

					b = reverseArray(BigInteger.valueOf(Double.doubleToLongBits(3.5)).toByteArray());
					assertEquals(3.5, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryFloatingPoint, 8)));
					
					b = reverseArray(BigInteger.valueOf(Double.doubleToLongBits(3.5)).toByteArray());
					assertEquals(3.5, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryFloatingPoint,  4)));

					b = reverseArray(BigInteger.valueOf(Double.doubleToLongBits(49.75)).toByteArray());
					assertEquals(49.75, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryFloatingPoint, 8)));
					
					b = reverseArray(BigInteger.valueOf(Double.doubleToLongBits(-175.55)).toByteArray());
					assertEquals(-175.55, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryFloatingPoint, 8)));
					
					b = reverseArray(BigInteger.valueOf(Double.doubleToLongBits(-10000.0)).toByteArray());
					assertEquals(-10000.0, getSubFieldValue(b, new S57SubFieldFormat(FormatType.binaryFloatingPoint, 8)));				
				}
			}
			Test test = new Test();
	}

}
