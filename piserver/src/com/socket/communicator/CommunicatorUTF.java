package com.socket.communicator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.Logger;

import com.socket.ServerLog;

public class CommunicatorUTF extends StopableSocketCommunicator implements Communicator {

	static Logger logger = Logger.getLogger(CommunicatorUTF.class);

	public CommunicatorUTF( Socket socket ){
		super(socket);
	}

	//Client�� ��û�� ����!
	public String receiveRequest() throws IOException{
		String msg = null;

		msg = new DataInputStream(socket_.getInputStream()).readUTF();
		ServerLog.getInstance().info(this.getClass().getName(), " msg[" + msg + "]");

		return msg;
	}

	//Client�� �������� �߼�!
	public void sendResponse(String resp) throws Exception{
		new DataOutputStream(socket_.getOutputStream()).writeUTF(resp);
		ServerLog.getInstance().info(this.getClass().getName(), "");
	}
}
