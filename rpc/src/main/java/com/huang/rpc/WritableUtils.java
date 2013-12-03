package com.huang.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WritableUtils {
	private static final Log LOG = LogFactory.getLog(WritableUtils.class);
	
	public static void writeString(DataOutput out,String s)throws IOException{
		if(s!=null){
			byte[] buffer = s.getBytes("UTF-8");
			int len = buffer.length;
			out.writeInt(len);
			out.write(buffer,0,len);
		}else{
			out.writeInt(-1);
		}
	}
	
	public static String readString(DataInput in)throws IOException{
		int length = in.readInt();
		if(length == -1){
			return null;
		}
		byte[] buffer = new byte[length];
		in.readFully(buffer);
		return new String(buffer,"UTF-8");
	}
	
	public static void closeStream(java.io.Closeable...closeables){
		for(java.io.Closeable c:closeables){
			if(c!=null){
				try{
					c.close();
				}catch(IOException e){
					if(LOG.isDebugEnabled()){
						LOG.debug("Exception in closing " + c, e);
					}
				}
			}
		}
	}
}
