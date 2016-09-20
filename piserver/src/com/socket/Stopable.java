package com.socket;

public interface Stopable {

	public static int STAT_IMMEDIATE = 0;
	public static int STAT_SOON = 1;
	public static int STAT_NO = -1;

	public int stop();

}
