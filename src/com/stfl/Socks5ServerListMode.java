package com.stfl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.stfl.misc.Config;
import com.stfl.network.NioLocalServer;

public class Socks5ServerListMode {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Socks5ServerListMode.class.getName());

	public static void main(String[] args) {
		startSocks5();
	}

	static List<Config> configList = new ArrayList<>();

	public static List<Config> configFinishList = new ArrayList<>();

	public static void startSocks5() {
		JSONParser jp = new JSONParser();
		JSONObject configs = null;
		try {
			Object parse = jp.parse(new FileReader(new File("gui-config.json")));
			configs = (JSONObject) parse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray jsonArray = (JSONArray) configs.get("configs");
		for (Object object : jsonArray) {
			JSONObject jo = (JSONObject) object;
			Config config = new Config(jo);
			configList.add(config);
			ServerThread mt = new ServerThread(config);
			mt.start();
		}
		while (configFinishList.size() < jsonArray.size()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
