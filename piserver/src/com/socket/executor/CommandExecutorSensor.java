package com.socket.executor;

import org.apache.log4j.Logger;
import com.socket.Stopable;
import com.socket.communicator.Communicator;

public class CommandExecutorSensor implements CommandExecutor{

	static Logger logger = Logger.getLogger(CommandExecutorSensor.class);
	private boolean nowRun = false;

	public void execute(String request, Communicator communicator) throws Exception {

		try{
			nowRun = true;
			/*
			 *  request.
			 */
			request = "CommandExecutorSensor ! request=[" + request + "]";

			communicator.sendResponse(request);
			nowRun = false;
		}catch(Exception e){
			nowRun = false;
			logger.info("execute() error", e);
			e.printStackTrace();
		}
	}

	/*
	 * CommunicatorUTF stop  STAT_IMMEDIATE!
	 * stop()!
	 */
	public int stop() {
		if( nowRun ) return Stopable.STAT_SOON;
		return  Stopable.STAT_IMMEDIATE;
	}
}
