package com.lxrtalk.texas.texas.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RobotManager implements Runnable {
	private int number;

	/**
	 * 机器人列表
	 */
	private static List<RobotWsClient> robotClientList = new CopyOnWriteArrayList<>();
	/**
	 * 最多允许的机器人个数
	 */
	public static final int MAX_ROBOT_COUNT = 100;
	
	RobotManager(int number) {
		this.number = number;
	}

	public void run() {
		if (robotClientList.size() < MAX_ROBOT_COUNT) {
			for (int i = 0; i < number; i++) {
				RobotWsClient client = new RobotWsClient(true);
				robotClientList.add(client);
				try {
					Thread.sleep(10l);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public synchronized static void init(int number) {
		// 创建机器人线程
		RobotManager m1 = new RobotManager(number);
		Thread t1 = new Thread(m1);
		t1.start();
	}

	public static void main(String[] args) {
		RobotWsClient client = new RobotWsClient(true);
		robotClientList.add(client);
		try {
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
