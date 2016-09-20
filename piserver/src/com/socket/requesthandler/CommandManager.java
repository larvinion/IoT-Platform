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

	/*
	 * CommandManager�� �����ڷν� �⺻ó���⸦ ����(���ӹ޴� ��ü���� �ݵ��� �����ؾ���)
	 */
	public CommandManager( CommandExecutor defaultExecutor ) throws Exception{
		commands_ = new HashMap();
		defaultExecutor_ = defaultExecutor;
		if(defaultExecutor_ == null) throw new Exception("�⺻ ó���⸦ �ݵ��� �����ؾ� �մϴ�.");
	}

	/*
	 * ���ɰ����ڿ��� Ư�� ������ ó���ϴ� ����ó���� ����
	 */
	public void addCommand(String command, CommandExecutor commandExecutor){
		commands_.put(command, commandExecutor);
	}

	/*
	 * ���ɰ����ڿ��� Ư�� ������ ó���ϴ� ����ó���⸦ ����
	 */
	public void removeCommand(String command){
		commands_.remove(command);
	}

	/*
	 * Ŭ���̾�Ʈ�� ������ ����
	 */
	public void execute(String command, String request, Communicator communicator) throws Exception{

		try{
			nowRun = true;
			executor = findExecutor(command);
			executor.execute(request, communicator);
			nowRun = false;
		}finally{ //catch�� execute() ���ο� �����Ƿ� �̰������� ����
			nowRun = false;
			executor = null;
		}
	}

	/*
	 * ������ ó���Ҽ� �ִ� ����ó���⸦ ��
	 */
	private CommandExecutor findExecutor(String command) {

		CommandExecutor executor = (CommandExecutor)commands_.get(command);

		//�ش� ������ ó���ϴ� ����ó���Ⱑ ���ϵǾ� ���� ������ defaultExecutor�� ��ȯ�Ѵ�.
		return executor == null ? defaultExecutor_ : executor;
	}

	public int stop() {

		ServerLog.getInstance().info(this.getClass().getName(), "stop������ �����մϴ�.");

		if(nowRun && executor != null){
			return executor.stop(); //executor ������ ������(�� communicator�� stop�ؾ� )STAT_IMMEDIATE ��ȯ
		}
		return Stopable.STAT_IMMEDIATE;
	}
}
