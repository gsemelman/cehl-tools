package org.cehl.raw.decode;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

public class DecodeTools {
	public static List<byte[]> decodeFile(File file, int recordLength) {

		List<byte[]> byteList = null;

		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			byteList = splitStreamToList(bis, recordLength);

		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error Reading The File.");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Unknown file processing error for file : "
					+ file.getName());
			throw new RuntimeException(e);
		}

		return byteList;
	}

	public static List<byte[]> splitStreamToList(InputStream fis, int recordSize) {

		List<byte[]> byteList = new ArrayList<byte[]>();

		try {

			byte[] data = null;
			int count = 0;

			while (count != -1) {
				data = new byte[recordSize];
				count = fis.read(data);
				byteList.add(data);
			}
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteList;
	}

	public static ArrayList<List<byte[]>> partition(List<byte[]> byteList, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        final AtomicInteger counter2 = new AtomicInteger(0);
        
        
         return new ArrayList<List<byte[]>>(byteList.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values());
    }

	public static int readInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN)
				.getInt();
	}

	public static String readString(byte[] bytes) {

		StringBuffer stringBuffer = new StringBuffer(bytes.length);
		// Read item
		for (int i = 0; i < bytes.length; i++) {
			stringBuffer.append((char) bytes[i]);
		}

		return stringBuffer.toString().trim();
	}
	
	public static String readString2(byte[] bytes, int start, int length) {

		int end = start + length;
		byte[] array = Arrays.copyOfRange(bytes,start, end);
		
		StringBuffer stringBuffer = new StringBuffer(array.length);
		// Read item
		for (int i = 0; i < array.length; i++) {
			stringBuffer.append((char) array[i]);
		}

		return stringBuffer.toString().trim();
	}

	public static byte[] getStringBytes(String string, int length) {

		byte[] bytes = new byte[length];

		for (int i = 0; i < string.length(); i++) {
			bytes[i] = (byte) string.charAt(i);
		}

		return bytes;
	}

	public static byte[] getIntBytes(Integer x, int length) {
		return ByteBuffer.allocate(length)
				.order(java.nio.ByteOrder.LITTLE_ENDIAN).putInt(x).array();
	}

	public static byte[] getShortBytes(Integer x, int length) {
		return ByteBuffer.allocate(2).order(java.nio.ByteOrder.LITTLE_ENDIAN)
				.putInt(x).array();
	}

	public static void writeString(byte[] bytes, String string, int offset,
			int maxLength) {

		if (string.isEmpty()) {
			return;
		}

		byte[] stringBytes = getStringBytes(string, string.length());

		for (int i = 0; i < string.length(); i++) {
			bytes[i + offset] = stringBytes[i];
		}

		byte spaceByte = 32; // ascii space
		int newOffset = offset + string.length();
		;
		int size = maxLength - string.length();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				bytes[i + newOffset] = spaceByte;
			}
		}

		// for (int i = 0; i < stringBytes.length; i++) {
		// bytes[i + offset] = stringBytes[i];
		// }
	}

	public static void writeInt(byte[] bytes, Integer x, int offset, int length) {
		byte[] intBytes = getIntBytes(x, 4);

		for (int i = 0; i < intBytes.length; i++) {
			bytes[i + offset] = intBytes[i];
		}
	}

	public static int readShort(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(bytes[0]);
		bb.put(bytes[1]);
		short shortVal = bb.getShort(0);

		return shortVal;
	}

	public static float readFloat(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(bytes[0]);
		bb.put(bytes[1]);
		bb.put(bytes[2]);
		bb.put(bytes[3]);
		float val = bb.getFloat();

		return val;
	}

	public static int readByte(byte b) {
		return (b & 0xFF);
	}

}
