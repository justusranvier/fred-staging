/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */

package freenet.support;

import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.LongHolder;

import junit.framework.TestCase;

/**
 * Test case for {@link freenet.support.Fields} class.
 * 
 *  @author stuart martin &lt;wavey@freenetproject.org&gt;
 */
public class FieldsTest extends TestCase {

	public void testHexToLong(){
		
		long l1 = Fields.hexToLong("0");
		assertEquals(l1, 0);
		
		l1 = Fields.hexToLong("000000");
		assertEquals(l1, 0);
		
		l1 = Fields.hexToLong("1");
		assertEquals(l1, 1);
		
		l1 = Fields.hexToLong("a");
		assertEquals(l1, 10);
		
		l1 = Fields.hexToLong("ff");
		assertEquals(l1, 255);
		
		l1 = Fields.hexToLong("ffffffff");
		assertEquals(l1, 4294967295L);
		
		l1 = Fields.hexToLong("7fffffffffffffff");
		assertEquals(l1, Long.MAX_VALUE); 
		
		l1 = Fields.hexToLong("8000000000000000");
		assertEquals(l1, Long.MIN_VALUE); 
		
		l1 = Fields.hexToLong("FFfffFfF"); // mix case
		assertEquals(l1, 4294967295L);

		try {
			l1 = Fields.hexToLong("abcdef123456789aa"); // 17 chars
			fail();
		}
		catch(NumberFormatException e){
			// expect this
		}
		
		try {
			l1 = Fields.hexToLong("DeADC0dER"); // invalid char
			fail();
		}
		catch(NumberFormatException e){
			// expect this
		}
		
		// see javadoc
		l1 = Fields.hexToLong(Long.toHexString(20));
		assertEquals(20, l1);

		l1 = Fields.hexToLong(Long.toHexString(Long.MIN_VALUE));
		assertEquals(Long.MIN_VALUE, l1);

		// see javadoc
		try {
			String longAsString = Long.toString(-1, 16);
			l1 = Fields.hexToLong(longAsString);
			fail();
		}
		catch(NumberFormatException e) {
			// expect this
		}
	}
	
	public void testHexToInt() {
		
		int i1 = Fields.hexToInt("0");
		assertEquals(i1, 0);
		
		i1 = Fields.hexToInt("000000");
		assertEquals(i1, 0);
		
		i1 = Fields.hexToInt("1");
		assertEquals(i1, 1);
		
		i1 = Fields.hexToInt("a");
		assertEquals(i1, 10);
		
		i1 = Fields.hexToInt("ff");
		assertEquals(i1, 255);
		
		i1 = Fields.hexToInt("80000000");
		assertEquals(i1, Integer.MIN_VALUE);
		
		i1 = Fields.hexToInt("0000000080000000"); // 16 chars
		assertEquals(i1, Integer.MIN_VALUE);
		
		i1 = Fields.hexToInt("7fffffff");
		assertEquals(i1, Integer.MAX_VALUE);
		
		try {
			i1 = Fields.hexToInt("0123456789abcdef0"); // 17 chars
			fail();
		}
		catch(NumberFormatException e){
			// expect this
		}
		
		try {
			i1 = Fields.hexToInt("C0dER"); // invalid char
			fail();
		}
		catch(NumberFormatException e){
			// expect this
		}
		
		// see javadoc
		i1 = Fields.hexToInt(Integer.toHexString(20));
		assertEquals(20, i1);

		i1 = Fields.hexToInt(Long.toHexString(Integer.MIN_VALUE));
		assertEquals(Integer.MIN_VALUE, i1);

		// see javadoc
		try {
			String integerAsString = Integer.toString(-1, 16);
			i1 = Fields.hexToInt(integerAsString);
			fail();
		}
		catch(NumberFormatException e) {
			// expect this
		}
	}
	
	public void testStringToBool() {
		assertTrue(Fields.stringToBool("true"));
		assertTrue(Fields.stringToBool("TRUE"));
		assertFalse(Fields.stringToBool("false"));
		assertFalse(Fields.stringToBool("FALSE"));
		
		try {
			Fields.stringToBool("Free Tibet");
			fail();
		}
		catch(NumberFormatException e) {
			// expect this
		}
		
		try {
			Fields.stringToBool(null);
			fail();
		}
		catch(NumberFormatException e) {
			// expect this
		}
	}
	
	public void testStringToBoolWithDefault() {
		assertTrue(Fields.stringToBool("true", false));
		assertFalse(Fields.stringToBool("false", true));
		assertTrue(Fields.stringToBool("TruE", false));
		assertFalse(Fields.stringToBool("faLSE", true));
		assertTrue(Fields.stringToBool("trueXXX", true));
		assertFalse(Fields.stringToBool("XXXFalse", false));
		assertTrue(Fields.stringToBool(null, true));
	}
	
	public void testBoolToString() {
		assertEquals(Fields.boolToString(true), "true");
		assertEquals(Fields.boolToString(false), "false");
	}
	
	public void testCommaListFromString() {
		String[] expected = new String[] {"one", "two", "three", "four"};
		String[] actual = Fields.commaList("one,two,     three    ,  four");
		
		for(int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
		
		// null
		assertNull(Fields.commaList((String)null));
		
		// no items
		expected = new String[] {};
		actual = Fields.commaList("");
		
		assertTrue(expected.length == actual.length);
	}
	
	public void testStringArrayToCommaList() {
		
		String[] input = new String[] { "one", "two", "three", "four" };
		
		String expected = "one,two,three,four";
		String actual = Fields.commaList(input);
		
		assertEquals(expected, actual);
		
		// empty
		input = new String[] {};
		
		expected = "";
		actual = Fields.commaList(input);
		
		assertEquals(expected, actual);
	}
	
	public void testHashcodeForByteArray() {
		byte[] input = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		
		assertEquals(67372036, Fields.hashCode(input));
		
		// empty
		input = new byte[] {};
		
		assertEquals(0, Fields.hashCode(input));
	}
	
	public void testLongHashcode() {
		
		byte[] b1 = new byte[] { 1, 1, 2, 2, 3, 3 };
		byte[] b2 = new byte[] { 2, 2, 3, 3, 4, 4 };
		byte[] b3 = new byte[] { 1, 1, 2, 2, 3, 3 };
		
		Long l1 = new Long(Fields.longHashCode(b1));
		Long l2 = new Long(Fields.longHashCode(b2));
		Long l3 = new Long(Fields.longHashCode(b3));

		assertFalse(l1.equals(l2));
		assertFalse(l2.equals(l3));
		assertTrue(l3.equals(l1)); // should be same due to Fields.longHashcode
	}	
	
	public void testIntsToBytes() {
		int[] longs = new int[] {};
		doRoundTripIntsArrayToBytesArray(longs);
		
		longs = new int[] {Integer.MIN_VALUE};
		doRoundTripIntsArrayToBytesArray(longs);
		
		longs = new int[] {0, Integer.MAX_VALUE, Integer.MIN_VALUE};
		doRoundTripIntsArrayToBytesArray(longs);
		
		longs = new int[] {33685760, 51511577};
		doRoundTripIntsArrayToBytesArray(longs);		
	}

	private void doRoundTripIntsArrayToBytesArray(int[] ints) {
		byte[] bytes = Fields.intsToBytes(ints);
		assert(bytes.length == ints.length * 8);
		
		int[] outLongs = Fields.bytesToInts(bytes);
		for(int i = 0; i < ints.length; i++) {
			assertTrue(outLongs[i] == ints[i]);
		}
		assertEquals(outLongs.length, ints.length);
	}
	
	public void testBytesToIntException() {
		byte[] bytes = new byte[3];
		try {
			Fields.bytesToLongs(bytes, 0, bytes.length);
			fail();
		}
		catch(IllegalArgumentException e){
			// expect this
		}
	}
	
	public void testBytesToInt() {
		
		byte[] bytes = new byte[] { 0, 1, 2, 2 };
		
		int outLong = Fields.bytesToInt(bytes, 0);
		assertEquals(outLong, 33685760);
		
		doTestRoundTripBytesArrayToInt(bytes);
		
		bytes = new byte[] {};
		try{
			doTestRoundTripBytesArrayToInt(bytes);
			fail();
		}
		catch(IllegalArgumentException e) {
			//expect this
		}
		
		bytes = new byte[] {1, 1, 1, 1};
		doTestRoundTripBytesArrayToInt(bytes);
		
	}
	
	private void doTestRoundTripBytesArrayToInt(byte[] inBytes) {

		int outLong = Fields.bytesToInt(inBytes, 0);
		byte[] outBytes = Fields.intToBytes(outLong);
		for(int i = 0; i < inBytes.length; i++) {
			assertEquals(outBytes[i], inBytes[i]);
		}
		assertEquals(outBytes.length, inBytes.length);
	}
	
	public void testLongsToBytes() {
		long[] longs = new long[] {};
		doRoundTripLongsArrayToBytesArray(longs);
		
		longs = new long[] {Long.MIN_VALUE};
		doRoundTripLongsArrayToBytesArray(longs);
		
		longs = new long[] {0L, Long.MAX_VALUE, Long.MIN_VALUE};
		doRoundTripLongsArrayToBytesArray(longs);
		
		longs = new long[] {3733393793879837L};
		doRoundTripLongsArrayToBytesArray(longs);		
	}

	private void doRoundTripLongsArrayToBytesArray(long[] longs) {
		byte[] bytes = Fields.longsToBytes(longs);
		assert(bytes.length == longs.length * 8);
		
		long[] outLongs = Fields.bytesToLongs(bytes);
		for(int i = 0; i < longs.length; i++) {
			assertTrue(outLongs[i] == longs[i]);
		}
		assertEquals(outLongs.length, longs.length);
	}
	
	public void testBytesToLongException() {
		byte[] bytes = new byte[3];
		try {
			Fields.bytesToLongs(bytes, 0, bytes.length);
			fail();
		}
		catch(IllegalArgumentException e){
			// expect this
		}
	}
	
	public void testBytesToLong() {
		
		byte[] bytes = new byte[] { 0, 1, 2, 2, 1, 3, 6, 7 };
		
		long outLong = Fields.bytesToLong(bytes);
		assertEquals(outLong, 506095310989295872L);
				
		doTestRoundTripBytesArrayToLong(bytes);
		
		bytes = new byte[] {};
		try{
			doTestRoundTripBytesArrayToLong(bytes);
			fail();
		}
		catch(IllegalArgumentException e) {
			//expect this
		}
		
		bytes = new byte[] {1, 1, 1, 1, 1, 1, 1, 1};
		doTestRoundTripBytesArrayToLong(bytes);
		
	}
	
	private void doTestRoundTripBytesArrayToLong(byte[] inBytes) {

		long outLong = Fields.bytesToLong(inBytes);
		byte[] outBytes = Fields.longToBytes(outLong);
		for(int i = 0; i < inBytes.length; i++) {
			assertEquals(outBytes[i], inBytes[i]);
		}
		assertEquals(outBytes.length, inBytes.length);
	}

}
