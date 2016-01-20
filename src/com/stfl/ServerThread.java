package com.stfl;

import com.stfl.misc.Config;
import com.stfl.network.NioLocalServer;

public class ServerThread extends Thread {

	public	Config tconfig;

	public ServerThread(Config config) {
		tconfig = config;
	}
	NioLocalServer server;
	Thread t ;
	@Override
	public void run() {
		try {
			server = createServer();
			t = new Thread(server);
			t.start();
			t.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private NioLocalServer createServer() {
		try {
			NioLocalServer server = new NioLocalServer(tconfig);
			Socks5ServerSingalChangeMode.configFinishList.add(tconfig);
			return server;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("Address already in use")) {
				tconfig._localPort += 1;
				createServer();
			}
		}
		return null;
	}
	
	public void stopThread() {
		try {
			server.close();
			t.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
