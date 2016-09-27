package com.socket.executor;

import org.apache.log4j.Logger;
import com.socket.ServerLog;
import com.socket.Stopable;
import com.socket.communicator.Communicator;

public class CommandExecutorJob implements CommandExecutor{

	static Logger logger = Logger.getLogger(CommandExecutorJob.class);
	private boolean nowRun = false;

	public void execute(String request, Communicator communicator) throws Exception {

		try{
			nowRun = true;

			communicator.sendResponse(request); //ó�������� Client�� ����!
			nowRun = false;
		}catch(Exception e){
			nowRun = false;
			logger.info("execute() error", e);
			e.printStackTrace();
		}
	}
	
	public int stop() {

		ServerLog.getInstance().info(this.getClass().getName(), "stop�� ���ɼ����մϴ�.");

		if( nowRun ) return Stopable.STAT_SOON;
		return  Stopable.STAT_IMMEDIATE;
	}
}
