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

	//Client�� ��û�� ����!
	public String receiveRequest() throws IOException{

		//Ŭ���̾�Ʈ����  Byte[]��Ʈ���� ���۹���
		DataInputStream dis = new DataInputStream(socket_.getInputStream());
		//(1) ���۵� �ڷ��� ���̸� ����
		int readCount = dis.readInt();
		//(2) ���۵�  �ڷ��� ���̿� �´� byte[] ����
		byte[] buf = new byte[readCount];
		//(3) ���۵� �ڷḦ byte[]������ ������
		dis.read(buf); //�Ǵ� dis.read(buf, 0, readCount);
		//(4) ���۵� �ڷḦ String������ ��ȯ
		String msg = new String(buf, ENCODING);

		ServerLog.getInstance().info(this.getClass().getName(), " msg[" + msg + "]");

		return msg;
	}

	//Client�� �������� ����!
	public void sendResponse(String resp) throws Exception{

		//������ Byte[]��Ʈ�� ����
		DataOutputStream dos = new DataOutputStream(socket_.getOutputStream());
		//(1) ���� �ڷḦ byte[]�� ��ȯ
		byte[] respByte = resp.getBytes(ENCODING);
		//(2) ���� �ڷ��� ���̸� ����
		int retLen = respByte.length;
		//(3) ���� �ڷ��� ���̸� ���� ����
		dos.writeInt(retLen);
		dos.flush();
		//(4) ���� �ڷḦ ����
		dos.write(respByte);
		dos.flush();

		ServerLog.getInstance().info(this.getClass().getName(), "");
	}
}
