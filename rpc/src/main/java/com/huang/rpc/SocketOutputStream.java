package com.huang.rpc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;

public class SocketOutputStream extends OutputStream implements WritableByteChannel{
	
	private Writer writer;
	
	public SocketOutputStream(WritableByteChannel channel,long timeout)throws IOException{
		SocketIOWithTimeout.checkChannelValidity(channel);
		writer = new Writer(channel, timeout);
	}
	
	public SocketOutputStream(Socket socket,long timeout)throws IOException{
		this(socket.getChannel(),timeout);
	}
	
	public boolean isOpen() {
		return writer.isOpen();
	}

	public int write(ByteBuffer src) throws IOException {
		return writer.doIO(src, SelectionKey.OP_WRITE);
	}

	public void write(int b) throws IOException {
		byte[] buf = new byte[1];
		buf[0] = (byte) b;
		write(buf, 0, 1);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		ByteBuffer buf = ByteBuffer.wrap(b, off, len);
		while (buf.hasRemaining()) {
			try {
				if (write(buf) < 0) {
					throw new IOException("The stream is closed");
				}
			} catch (IOException e) {
				if (buf.capacity() > buf.remaining()) {
					writer.close();
				}
				throw e;
			}
		}
	}
	
	public void waitForWritable() throws IOException {
		writer.waitForIO(SelectionKey.OP_WRITE);
	}
	
	
	private static class Writer extends SocketIOWithTimeout{

		WritableByteChannel channel;
		
		Writer(WritableByteChannel channel, long timeout) throws IOException {
			super((SelectableChannel)channel, timeout);
			this.channel = channel;
		}

		@Override
		int performIO(ByteBuffer buf) throws IOException {
			return channel.write(buf);
		}
		
	}
	

}
