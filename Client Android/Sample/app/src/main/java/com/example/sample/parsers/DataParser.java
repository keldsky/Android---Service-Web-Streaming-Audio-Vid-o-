package com.example.sample.parsers;

import com.example.sample.model.MP3;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
	

	public static List<MP3> parseFeedXml(String content) {

        try {

            boolean inDataItemTag = false;
            String currentTagName = "";
            MP3 mp3 = null;
            List<MP3> mp3List = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("data")) {
                            inDataItemTag = true;
                            mp3 = new MP3();
                            mp3List.add(mp3);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("data")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;

                    case XmlPullParser.TEXT:
                        if (inDataItemTag && mp3 != null) {
                            switch (currentTagName) {

                                case "titre":
                                    mp3.setTitre(parser.getText());
                                    break;
                                case "album":
                                    mp3.setAuteur(parser.getText());
                                    break;
                                case "genre":
                                    mp3.setGenre(parser.getText());
                                    break;
                                case "auteur":
                                    mp3.setAuteur(parser.getText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }

                eventType = parser.next();

            }

            return mp3List;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
			public static List<String> parseFeedJson(String content) {
                String cmd = null;
                String song = null;

			try {
				JSONArray ar = new JSONArray(content);

				List<String> cList = new ArrayList<>();

                     cmd = ar.getString(0);
                     song = ar.getString(1);
                     cList.add(cmd);
                     cList.add(song);

                     return cList;

			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		
	}
	
}
