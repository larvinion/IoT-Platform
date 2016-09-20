package com.socket.executor;

import com.socket.Stopable;
import com.socket.communicator.Communicator;

public interface CommandExecutor extends Stopable{
	public void execute(String request, Communicator communicator) throws Exception;
}
