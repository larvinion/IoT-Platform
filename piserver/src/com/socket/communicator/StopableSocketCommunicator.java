package com.socket.communicator;

import java.net.Socket;
import com.socket.ServerLog;

public abstract class StopableSocketCommunicator implements Communicator {

	protected Socket socket_ = null;

	public StopableSocketCommunicator(Socket socket){
		setSocket_(socket);
	}

	public int stop() {

		try{
			ServerLog.getInstance().info(this.getClass().getName(), "stop���ɼ���, ������ �����ִٸ� �ݽ��ϴ�.");

			if(getSocket_() != null) getSocket_().close();
			return STAT_IMMEDIATE;
		}catch(Exception e){
			ServerLog.getInstance().info(this.getClass().getName(), "stop() error", e);
			e.printStackTrace();
			//return STAT_NO;
			return STAT_IMMEDIATE;
		}finally{
			ServerLog.getInstance().info(this.getClass().getName(), "stop �Ϸ�");
		}
	}

	private void setSocket_(Socket socket_) {
		this.socket_ = socket_;
	}

	protected Socket getSocket_() {
		return socket_;
	}
}
