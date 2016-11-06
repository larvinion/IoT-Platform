package com.socket.connectionhandler;

import java.io.EOFException;

import org.apache.log4j.Logger;

import com.socket.ServerLog;
import com.socket.Stopable;
import com.socket.communicator.Communicator;
import com.socket.requesthandler.RequestHandler;

public class NormalConnectionHandler implements ConnectionHandler, Runnable {

	static Logger logger = Logger.getLogger(NormalConnectionHandler.class);

	private boolean stop = false;
	private boolean keepConnect_ = true;
	private int threadID_;
	private Thread thread_;
	private RequestHandler requestHandler_;
	private Communicator communicator_;
	private ExitReceiver exitReceiver_;

	public NormalConnectionHandler(int threadID, ExitReceiver exitReceiver){
		threadID_ = threadID;
		exitReceiver_ = exitReceiver;
	}

	public void keepConnection(Communicator communicator, RequestHandler requestHandler) {

		communicator_ = communicator;
		requestHandler_ = requestHandler;
		keepConnect_ = true;

		thread_ = new Thread(this);
		thread_.start();

	}

	public void run() {

		String msg;

		while(keepConnect_){ 
			try{

				ServerLog.getInstance().info(this.getClass().getName(), "Wait until request message arrive from client [ID:" + threadID_ + "]");

				msg = communicator_.receiveRequest();
				requestHandler_.processRequest(msg, communicator_);

				ServerLog.getInstance().info(this.getClass().getName(), "Handling request message.[ID:" + threadID_ + "]");
			}catch(Exception e){
				ServerLog.getInstance().info(this.getClass().getName(), "Handling is finished.[ID:" + threadID_ + "]");
				if( !(e instanceof EOFException) && !stop ){
					ServerLog.getInstance().info(this.getClass().getName(), "NormalConnectionHandler.run() error[ID:" + threadID_ + "]", e);
					e.printStackTrace();
				}
				break;
			}
		}
		exitReceiver_.reportWhenExit(this);
	}

	public int stop() {

		ServerLog.getInstance().info(this.getClass().getName(), "stop[ID:" + threadID_ + "]");

		keepConnect_ = false;
		stop = true;

		int stat1 = communicator_.stop(); 
		int stat2 = requestHandler_.stop(); 
		if( stat1 == Stopable.STAT_NO || stat2 == Stopable.STAT_NO ) return Stopable.STAT_NO;
		if( stat1 == Stopable.STAT_SOON || stat2 == Stopable.STAT_SOON ) return Stopable.STAT_SOON;

		return Stopable.STAT_IMMEDIATE;
	}
}
