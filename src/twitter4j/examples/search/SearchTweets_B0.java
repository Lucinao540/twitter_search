/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twitter4j.examples.search;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.examples.DBUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.stfl.Socks5ServerSingalChangeMode;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class SearchTweets_B0 {
	/**
	 * Usage: java twitter4j.examples.search.SearchTweets [query]
	 * 
	 * @param args
	 *            search query
	 */
	public static void main(String[] args) {
		start();
	}
	static	Socks5ServerSingalChangeMode s5 = new Socks5ServerSingalChangeMode();
	static{
		s5.startSocks5();
	}

	private static void start() {
		int start = 0, end = 0;
		HashSet<String> keys = new HashSet<>();
	
		try {
			JSONReader reader = new JSONReader(new FileReader("config"));
			JSONObject readObject = (JSONObject) reader.readObject();
			JSONArray jsonArray = readObject.getJSONArray("keywords");
			for (Object object : jsonArray) {
				keys.add(object.toString());
			}
			start = readObject.getInteger("start");
			end = readObject.getInteger("end");
			reader.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < start - end; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.DATE, -1 * start + i);
			for (String string : keys) {
				try {
					bw.write(string + "    " + sdf.format(instance.getTime()) + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(string + "    " + sdf.format(instance.getTime()));
				searchTweet(string, sdf.format(instance.getTime()),string);
				// System.out.println(string+"  "+sdf.format(instance.getTime()));
			}
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}
	static	int count=0;
	private static void searchTweet(String key, String date, String string) {
		Twitter twitter=null;
		twitter = newTw(twitter);
		count=0;
		int searchOunt=0;
		// Twitter retwitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(key);// List<Status> statuses =
											// retwitter.getRetweets(tweet.getId());
			query.setUntil(date);
			QueryResult result;
			do {
				result = searchTry(twitter, query);
				searchOunt++;
				System.out.print(searchOunt+"\t");
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					saveTw(tweet, null,string+"#");
					count++;
					if(forbid==true){
						System.out.println(date+"  "+string+"  "+count +"##forbid");
						bw.write(date+"  "+string+"  "+count +"##forbid"+ "\n");
					}
					forbid=false;
					bw.write("save TW:" + tweet.getId() + "  " + new Date() + "\n");
//					System.out.println(tweet.getText());
					bw.flush();
					// List<Status> statuses =
					// retwitter.getRetweets(tweet.getId());
					// for (Status status : statuses) {
					// saveTw(status,tweet.getId());
					// }
					if(count%1000==0){
						System.out.println(date+"  "+string+"  "+count );
						bw.write(date+"  "+string+"  "+count+ "\n");
					}
				}
		
			} while ((query = getNext(result)) != null);
			System.out.println(date+"  "+string+"  "+count +"###"+new Date());
			bw.write(date+"  "+string+"  "+count+ "###\n");
			// bw.flush();
			// bw.close();
			
		} catch (Exception te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
		}
	}

	private static Twitter newTw(Twitter twitter) {
		try {
			twitter = new TwitterFactory().getInstance();
//			twitter.getConfiguration()
		} catch (Exception e) {
			e.printStackTrace();
			return newTw(twitter);
		}
		return twitter;
	}

	private static Query getNext(QueryResult result) {
		try {
			return result.nextQuery();
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return getNext(result);
		}
	}

	static BufferedWriter bw;
	static {
		try {
			bw = new BufferedWriter(new FileWriter(new File("tw.log")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveTw(Status tweet, Long ptid, String string) {
		try {
			try {
				if (DBUtil.isNewUser(tweet.getUser().getId())) {
					DBUtil.saveUser(tweet.getUser());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			DBUtil.saveTweet(tweet, ptid,string);
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static boolean forbid=false;
	private static QueryResult searchTry(Twitter twitter, Query query)   {
		try {
			QueryResult result;
			result = twitter.search(query);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return result;
		} catch (Exception e) {
			s5.changeProxy(count);
			e.printStackTrace();
			forbid=true;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return searchTry(twitter, query);
		}
	}
}
