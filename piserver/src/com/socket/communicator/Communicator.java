package com.socket.communicator;

import java.io.IOException;
import com.socket.Stopable;

public interface Communicator extends Stopable {

	public String receiveRequest() throws IOException;
	public void sendResponse(String resp) throws Exception;

}
