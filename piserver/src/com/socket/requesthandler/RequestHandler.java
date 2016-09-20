package com.socket.requesthandler;

import com.socket.Stopable;
import com.socket.communicator.Communicator;

public interface RequestHandler extends Stopable{

	public void processRequest( String request, Communicator communicator ) throws Exception;

}
