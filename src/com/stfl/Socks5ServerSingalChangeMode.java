package com.stfl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.stfl.misc.Config;

public class Socks5ServerSingalChangeMode {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Socks5ServerSingalChangeMode.class.getName());

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
		}
		changeProxy(null);
	}
	private boolean loadProperties(Properties props, String path) {
        FileInputStream fis = null;
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                fis = new FileInputStream(file);
                props.load(fis);
                return true;
            }
        } catch (Exception ignore) {
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ignore) {

            }
        }
        return false;
    }

	static ServerThread mt;

	public static void changeProxy(Integer count) {
		int abs = Math.abs(new Random().nextInt() %configList.size() );
		Config config = configList.get(abs);
		
	     File file = new File("twitter4j.properties");
	     Properties props=new Properties();
	     try {
			props.load(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		config._localPort =Integer.parseInt(props.get("http.proxyPort").toString()) ;
		System.out.println(config);
		if (mt == null) {
		} else {
			System.out.println("change:"+mt.tconfig+"  "+new Date()+"  "+count);
			mt.stopThread();
			mt.interrupt();
		}
		mt = new ServerThread(config);
		mt.start();
	}
}
