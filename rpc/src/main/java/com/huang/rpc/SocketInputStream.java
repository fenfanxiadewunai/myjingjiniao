package com.huang.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public class SocketInputStream extends InputStream implements ReadableByteChannel{

	private Reader reader;
	
	public SocketInputStream(ReadableByteChannel channel,long timeout)throws IOException{
		SocketIOWithTimeout.checkChannelValidity(channel);
		reader = new Reader(channel,timeout);
	}
	
	public SocketInputStream(Socket socket,long timeout)throws IOException{
		this(socket.getChannel(),timeout);
	}
	
	public SocketInputStream(Socket socket) throws IOException{
		this(socket.getChannel(),socket.getSoTimeout());
	}
	
	public boolean isOpen() {
		return reader.isOpen();
	}

	public int read(ByteBuffer dst) throws IOException {
		return reader.doIO(dst, SelectionKey.OP_READ);
	}
	
	public void waitForReadable()throws IOException{
		reader.waitForIO(SelectionKey.OP_READ);
	}

	@Override
	public int read() throws IOException {
		byte[] buf = new byte[1];
		int ret = read(buf,0,1);
		if(ret>0){
			return (byte)buf[0];
		}
		if(ret!=-1){
			throw new IOException("could not read from stream");
		}
		return ret;
	}
	
	public int read(byte[] b,int off,int len) throws IOException{
		return read(ByteBuffer.wrap(b, off, len));
	}
	
	public synchronized void close() throws IOException{
		reader.channel.close();
		reader.close();
	}
	
	public ReadableByteChannel getChannel(){
		return reader.channel;
	}
	
	private static class Reader extends SocketIOWithTimeout{
		ReadableByteChannel channel;
		public Reader(ReadableByteChannel channel,long timeout) throws IOException{
			super((SelectableChannel)channel, timeout);
			this.channel = channel;
		}
		@Override
		int performIO(ByteBuffer buf) throws IOException {
			return channel.read(buf);
		}
		
	}

}
