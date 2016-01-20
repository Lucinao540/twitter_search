package twitter4j.examples;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import twitter4j.ExtendedMediaEntity;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBUtil {
	public static MongoClient mongoClient = new MongoClient("127.0.0.1");
	public static MongoDatabase database = mongoClient.getDatabase("t_f_test");
	public static MongoCollection<Document> tweet_collection = database.getCollection("tweet");
	public static MongoCollection<Document> user_collection = database.getCollection("user");

	public static void main(String[] args) {
		Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1).append("info", new Document("x", 203).append("y", 102));
		MongoCollection<Document> collection = database.getCollection("test");
		collection.insertOne(doc);
		Document myDoc = collection.find().first();
		System.out.println(myDoc.toJson());
	}

	public static void saveTweet(Document doc) {
		tweet_collection.insertOne(doc);
	}

	public static boolean isNewUser(long id) {
		Document doc = new Document().append("uid", id);
		FindIterable<Document> find = user_collection.find((doc));
		if (find.first() == null) {
			return true;
		}
		return false;
	}

	public static void saveUser(User user) {
		Document doc = new Document();
		doc.append("BiggerProfileImageURL", user.getBiggerProfileImageURL());
		doc.append("CreatedAt", user.getCreatedAt());
		doc.append("Description", user.getDescription());

		URLEntity[] descriptionURLEntities = user.getDescriptionURLEntities();
		List<Document> docList1 = new ArrayList<>();
		for (URLEntity urlEntity : descriptionURLEntities) {
			Document ent = new Document();
			ent.append("DisplayURL", urlEntity.getDisplayURL());
			ent.append("ExpandedURL", urlEntity.getExpandedURL());
			ent.append("URL", urlEntity.getURL());
			ent.append("Text", urlEntity.getText());
			docList1.add(ent);
		}
		doc.append("DescriptionURLEntities", docList1);
		doc.append("FavouritesCount", user.getFavouritesCount());
		doc.append("FollowersCount", user.getFollowersCount());
		doc.append("FriendsCount", user.getFriendsCount());
		doc.append("uid", user.getId());
		doc.append("lang", user.getLang());
		doc.append("ListedCount", user.getListedCount());
		doc.append("Location", user.getLocation());
		doc.append("name", user.getName());
		doc.append("TimeZone", user.getTimeZone());
		doc.append("URL", user.getURL());
		URLEntity urlEntity = user.getURLEntity();
		Document doc2 = new Document();
		doc2.append("DisplayURL", urlEntity.getDisplayURL());
		doc2.append("ExpandedURL", urlEntity.getExpandedURL());
		doc2.append("Text", urlEntity.getText());
		doc2.append("URL", urlEntity.getURL());
		doc.append("urlEntity", doc2);
		doc.append("isVerified", user.isVerified());
		user_collection.insertOne(doc);
	}

	public static void saveTweet(Status tweet, Long ptid, String string) {
		try {
			Document doc = new Document();
			doc.append("s_keyword", string);
			doc.append("text", tweet.getText());
			if (ptid != null) {
				doc.append("ptid", ptid);
			}
			doc.append("user_name", tweet.getUser().getName());
			doc.append("user_id", tweet.getUser().getId());
			doc.append("tid", tweet.getId());
			doc.append("source", tweet.getSource());
			if (tweet.getGeoLocation() != null) {
				doc.append("geoLocation", tweet.getGeoLocation().getLatitude() + "#" + tweet.getGeoLocation().getLongitude());
			}
			doc.append("pubtime", tweet.getCreatedAt());
			doc.append("inserttime", new Date());
			HashtagEntity[] hashtagEntities = tweet.getHashtagEntities();
			String tag = "";
			for (HashtagEntity hashtagEntity : hashtagEntities) {
				tag += hashtagEntity.getText() + "#";
			}
			doc.append("hashtag", tag);
			doc.append("favoriteCount", tweet.getFavoriteCount());
			doc.append("RetweetCount", tweet.getRetweetCount());
			doc.append("CurrentUserRetweetId", tweet.getCurrentUserRetweetId());
			doc.append("InReplyToUserId", tweet.getInReplyToUserId());
			doc.append("InReplyToStatusId", tweet.getInReplyToStatusId());
			doc.append("InReplyToScreenName", tweet.getInReplyToScreenName());
			doc.append("QuotedStatusId", tweet.getQuotedStatusId());
			Place place = tweet.getPlace();
			if (place != null) {
				doc.append("Place", place.getCountry());
				doc.append("PlaceName", place.getFullName());
				doc.append("StreetAddress", place.getStreetAddress());
			}
			// doc.append("Scopes", tweet.getScopes());
			long[] contributors = tweet.getContributors();
			String cb="";
			for (long l : contributors) {
				cb+=l+"#";
			}
			doc.append("Contributors", cb);
			MediaEntity[] mediaEntities = tweet.getMediaEntities();
			List<Document> docList = new ArrayList<>();
			for (MediaEntity mediaEntity : mediaEntities) {
				Document dd = new Document();
				dd.put("DisplayURL", mediaEntity.getDisplayURL());
				dd.put("ExpandedURL", mediaEntity.getExpandedURL());
				dd.put("id", mediaEntity.getId());
				dd.put("MediaURL", mediaEntity.getMediaURL());
				dd.put("MediaURLHttps", mediaEntity.getMediaURLHttps());
				dd.put("Text", mediaEntity.getText());
				dd.put("Type", mediaEntity.getType());
				docList.add(dd);
			}
			doc.append("mediaEntities", docList);

			doc.append("lang", tweet.getLang());
			UserMentionEntity[] userMentionEntities = tweet.getUserMentionEntities();
			List<Document> docList3 = new ArrayList<>();
			for (UserMentionEntity userMentionEntity : userMentionEntities) {
				Document dd = new Document();
				dd.put("id", userMentionEntity.getId());
				dd.put("name", userMentionEntity.getName());
				dd.put("screenname", userMentionEntity.getScreenName());
				docList3.add(dd);
			}
			doc.append("user_mentions", docList3);
			ExtendedMediaEntity[] extendedMediaEntities = tweet.getExtendedMediaEntities();
			List<Document> docList2 = new ArrayList<>();
			for (ExtendedMediaEntity mediaEntity : extendedMediaEntities) {
				Document dd = new Document();
				dd.put("DisplayURL", mediaEntity.getDisplayURL());
				dd.put("ExpandedURL", mediaEntity.getExpandedURL());
				dd.put("id", mediaEntity.getId());
				dd.put("MediaURL", mediaEntity.getMediaURL());
				dd.put("MediaURLHttps", mediaEntity.getMediaURLHttps());
				dd.put("Text", mediaEntity.getText());
				dd.put("Type", mediaEntity.getType());
				docList2.add(dd);
			}
			doc.append("ExtendedMediaEntities", docList2);
			doc.append("isPossiblySensitive", tweet.isPossiblySensitive());
			doc.append("isRetweet", tweet.isRetweet());
			doc.append("isRetweeted", tweet.isRetweeted());
			tweet_collection.insertOne(doc);
		} catch (Exception e) {
			if (e.getMessage() == null || !e.getMessage().contains("dupli")) {
				e.printStackTrace();
			}
		}
	}
}
