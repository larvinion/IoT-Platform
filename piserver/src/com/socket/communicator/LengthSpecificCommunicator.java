package com.socket.communicator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.Logger;

import com.socket.ServerLog;

public class LengthSpecificCommunicator extends StopableSocketCommunicator implements Communicator {

	static Logger logger = Logger.getLogger(LengthSpecificCommunicator.class);

	private static String ENCODING = "ms949";

	public LengthSpecificCommunicator( Socket socket ){
		super(socket);
	}

	public String receiveRequest() throws IOException{

		DataInputStream dis = new DataInputStream(socket_.getInputStream());
		int readCount = dis.readInt();

		byte[] buf = new byte[readCount];

		dis.read(buf);
		String msg = new String(buf, ENCODING);

		ServerLog.getInstance().info(this.getClass().getName(), " msg[" + msg + "]");

		return msg;
	}

	public void sendResponse(String resp) throws Exception{

		DataOutputStream dos = new DataOutputStream(socket_.getOutputStream());
		byte[] respByte = resp.getBytes(ENCODING);
		int retLen = respByte.length;

		dos.writeInt(retLen);
		dos.flush();

		dos.write(respByte);
		dos.flush();

		ServerLog.getInstance().info(this.getClass().getName(), "");
	}
}
