package net.ion.radon.repository.vfs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.util.RandomAccessMode;

public class NodeFileRandomAccessContent implements RandomAccessContent {

	protected int filePointer = 0;

	private NodeFileData ndata ;
	private byte[] buf;

	private byte[] buffer8 = new byte[8];

	private byte[] buffer4 = new byte[4];

	private byte[] buffer2 = new byte[2];

	private byte[] buffer1 = new byte[1];

	private RandomAccessMode mode;

	private InputStream rafis;

	NodeFileRandomAccessContent(NodeFileData ndata, RandomAccessMode mode) throws UnsupportedEncodingException {
		super();
		this.ndata = ndata;
		this.buf = ndata.getBuffer();
		this.mode = mode;

		rafis = new InputStream() {
			public int read() throws IOException {
				try {
					return readByte();
				} catch (EOFException e) {
					return -1;
				}
			}

			public long skip(long n) throws IOException {
				seek(getFilePointer() + n);
				return n;
			}

			public void close() throws IOException {
			}

			public int read(byte[] b) throws IOException {
				return read(b, 0, b.length);
			}

			public int read(byte[] b, int off, int len) throws IOException {
				int retLen = Math.min(len, getLeftBytes());
				NodeFileRandomAccessContent.this.readFully(b, off, retLen);
				return retLen;
			}

			public int available() throws IOException {
				return getLeftBytes();
			}
		};
	}

	public long getFilePointer() throws IOException {
		return this.filePointer;
	}

	public void seek(long pos) throws IOException {
		this.filePointer = (int) pos;
	}

	public long length() throws IOException {
		return buf.length;
	}

	public void close() throws IOException {

	}

	public byte readByte() throws IOException {
		return (byte) this.readUnsignedByte();
	}

	public char readChar() throws IOException {
		int ch1 = this.readUnsignedByte();
		int ch2 = this.readUnsignedByte();
		return (char) ((ch1 << 8) + (ch2 << 0));
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(this.readLong());
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(this.readInt());
	}

	public int readInt() throws IOException {
		return (readUnsignedByte() << 24) | (readUnsignedByte() << 16) | (readUnsignedByte() << 8) | readUnsignedByte();
	}

	public int readUnsignedByte() throws IOException {
		if (filePointer < buf.length) {
			return buf[filePointer++] & 0xFF;
		} else {
			throw new EOFException();
		}
	}

	public int readUnsignedShort() throws IOException {
		this.readFully(buffer2);
		return toUnsignedShort(buffer2);
	}

	public long readLong() throws IOException {
		this.readFully(buffer8);
		return toLong(buffer8);
	}

	public short readShort() throws IOException {
		this.readFully(buffer2);
		return toShort(buffer2);
	}

	public boolean readBoolean() throws IOException {
		return (this.readUnsignedByte() != 0);
	}

	public int skipBytes(int n) throws IOException {
		if (n < 0) {
			throw new IndexOutOfBoundsException("The skip number can't be negative");
		}

		long newPos = filePointer + n;

		if (newPos > buf.length) {
			throw new IndexOutOfBoundsException("Tyring to skip too much bytes");
		}

		seek(newPos);

		return n;
	}

	public void readFully(byte[] b) throws IOException {
		this.readFully(b, 0, b.length);
	}

	public void readFully(byte[] b, int off, int len) throws IOException {
		if (len < 0) {
			throw new IndexOutOfBoundsException("Length is lower than 0");
		}

		if (len > this.getLeftBytes()) {
			throw new IndexOutOfBoundsException("Read length (" + len + ") is higher than buffer left bytes (" + this.getLeftBytes() + ") ");
		}

		System.arraycopy(buf, filePointer, b, off, len);

		filePointer += len;
	}

	private int getLeftBytes() {
		return buf.length - filePointer;
	}

	public String readUTF() throws IOException {
		return DataInputStream.readUTF(this);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if (this.getLeftBytes() < len) {
			int newSize = this.buf.length + len - this.getLeftBytes();
			this.ndata.resize(newSize);
			this.buf = this.ndata.getBuffer();
		}
		System.arraycopy(b, off, this.buf, filePointer, len);
		this.filePointer += len;
	}

	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	public void writeByte(int i) throws IOException {
		this.write(i);
	}

	public static long toLong(byte[] b) {
		return ((((long) b[7]) & 0xFF) + ((((long) b[6]) & 0xFF) << 8) + ((((long) b[5]) & 0xFF) << 16) + ((((long) b[4]) & 0xFF) << 24) + ((((long) b[3]) & 0xFF) << 32) + ((((long) b[2]) & 0xFF) << 40) + ((((long) b[1]) & 0xFF) << 48) + ((((long) b[0]) & 0xFF) << 56));
	}

	public static byte[] toBytes(long n, byte[] b) {
		b[7] = (byte) (n);
		n >>>= 8;
		b[6] = (byte) (n);
		n >>>= 8;
		b[5] = (byte) (n);
		n >>>= 8;
		b[4] = (byte) (n);
		n >>>= 8;
		b[3] = (byte) (n);
		n >>>= 8;
		b[2] = (byte) (n);
		n >>>= 8;
		b[1] = (byte) (n);
		n >>>= 8;
		b[0] = (byte) (n);
		return b;
	}

	public static short toShort(byte[] b) {
		return (short) toUnsignedShort(b);
	}

	public static int toUnsignedShort(byte[] b) {
		return ((b[1] & 0xFF) + ((b[0] & 0xFF) << 8));
	}

	public void write(int b) throws IOException {
		buffer1[0] = (byte) b;
		this.write(buffer1);
	}

	public void writeBoolean(boolean v) throws IOException {
		this.write(v ? 1 : 0);
	}

	public void writeBytes(String s) throws IOException {
		write(s.getBytes());
	}

	public void writeChar(int v) throws IOException {
		buffer2[0] = (byte) ((v >>> 8) & 0xFF);
		buffer2[1] = (byte) ((v >>> 0) & 0xFF);
		write(buffer2);
	}

	public void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			writeChar(s.charAt(i));
		}
	}

	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	public void writeInt(int v) throws IOException {
		buffer4[0] = (byte) ((v >>> 24) & 0xFF);
		buffer4[1] = (byte) ((v >>> 16) & 0xFF);
		buffer4[2] = (byte) ((v >>> 8) & 0xFF);
		buffer4[3] = (byte) (v & 0xFF);
		write(buffer4);
	}

	public void writeLong(long v) throws IOException {
		write(toBytes(v, buffer8));
	}

	public void writeShort(int v) throws IOException {
		buffer2[0] = (byte) ((v >>> 8) & 0xFF);
		buffer2[1] = (byte) (v & 0xFF);
		write(buffer2);
	}

	public void writeUTF(String str) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(str.length());
		DataOutputStream dataOut = new DataOutputStream(out);
		dataOut.writeUTF(str);
		dataOut.flush();
		dataOut.close();
		byte[] b = out.toByteArray();
		write(b);
	}

	public String readLine() throws IOException {
		throw new UnsupportedOperationException("deprecated");
	}

	public InputStream getInputStream() throws IOException {
		return rafis;
	}
}
