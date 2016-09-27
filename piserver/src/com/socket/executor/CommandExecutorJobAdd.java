package com.socket.executor;

import org.apache.log4j.Logger;
import com.socket.Stopable;
import com.socket.communicator.Communicator;

public class CommandExecutorJobAdd implements CommandExecutor{

	static Logger logger = Logger.getLogger(CommandExecutorJob.class);
	private boolean nowRun = false;

	public void execute(String request, Communicator communicator) throws Exception {

		try{
			nowRun = true;
		
			request = "CommandExecutorJobAdd request [" + request + "]";

			communicator.sendResponse(request);
			nowRun = false;
		}catch(Exception e){
			nowRun = false;
			logger.info("execute() error", e);
			e.printStackTrace();
		}
	}

	/*
	 * CommunicatorUTF�� stop�ؾ�  STAT_IMMEDIATE�� ��ȯ!
	 * ������ �Ʒ� stop()�Լ��� ��ü���� ���� ������ ����!
	 */
	public int stop() {
		if( nowRun ) return Stopable.STAT_SOON;
		return  Stopable.STAT_IMMEDIATE;
	}
}
