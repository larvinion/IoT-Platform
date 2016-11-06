package com.socket.requesthandler;

import java.util.HashMap;

import com.socket.ServerLog;
import com.socket.Stopable;
import com.socket.communicator.Communicator;
import com.socket.executor.CommandExecutor;

public class CommandManager implements Stopable{

	private HashMap commands_;
	private boolean nowRun = false;
	private CommandExecutor executor = null;
	private CommandExecutor defaultExecutor_;

	private CommandManager() throws Exception{
		throw new Exception("Don't make CommandManger directly.");
	}

	public CommandManager( CommandExecutor defaultExecutor ) throws Exception{
		commands_ = new HashMap();
		defaultExecutor_ = defaultExecutor;
		if(defaultExecutor_ == null) throw new Exception("dafaultExecutor is null.");
	}

	public void addCommand(String command, CommandExecutor commandExecutor){
		commands_.put(command, commandExecutor);
	}

	public void removeCommand(String command){
		commands_.remove(command);
	}

	public void execute(String command, String request, Communicator communicator) throws Exception{

		try{
			nowRun = true;
			executor = findExecutor(command);
			executor.execute(request, communicator);
			nowRun = false;
		}finally{ 
			nowRun = false;
			executor = null;
		}
	}

	private CommandExecutor findExecutor(String command) {

		CommandExecutor executor = (CommandExecutor)commands_.get(command);

		return executor == null ? defaultExecutor_ : executor;
	}

	public int stop() {

		ServerLog.getInstance().info(this.getClass().getName(), "stop.");

		if(nowRun && executor != null){
			return executor.stop(); 
		}
		return Stopable.STAT_IMMEDIATE;
	}
}
