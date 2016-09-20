package com.socket.requesthandler;

import org.apache.log4j.Logger;
import com.socket.communicator.Communicator;
import com.socket.executor.CommandExecutor;
import com.socket.executor.CommandExecutorJob;

public class CommandRequestHandler extends CommandManager implements RequestHandler {

	static Logger logger = Logger.getLogger(CommandRequestHandler.class);

	public CommandRequestHandler() throws Exception{
		this( new CommandExecutorJob() );
	}

	public CommandRequestHandler(CommandExecutor defaultExecutor) throws Exception {
		super(defaultExecutor);
	}

	public void processRequest(String request, Communicator communicator) throws Exception {

		//�ϴ� request�� command�� �����ϰ� ����, ���߽� command�� ����ȭ�Ѵ�.
		String command = request;

		try{
			this.execute(command, request, communicator);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
