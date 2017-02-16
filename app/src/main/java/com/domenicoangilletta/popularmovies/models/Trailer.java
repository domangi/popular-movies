package com.domenicoangilletta.popularmovies.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by domangi on 11/02/17.
 */

public class Trailer {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    private final String YOUTUBE_BASE_URL="https://www.youtube.com/watch?v=";

    public Trailer(String id, String name, String key, String site, String type){
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public String getName(){
        return name;
    }

    public String getSite() {return site; };
    public String getType() {return type; };

    public String getId(){
        return id;
    }

    public String getKey(){
        return key;
    }
    /*
        Converts the movie instance to a json object and returns it stringified
     */
    public String toJson(){
        String json_string = "";
        try {
            JSONObject trailer_json = new JSONObject();
            trailer_json.put("id", id);
            trailer_json.put("key", key);
            trailer_json.put("name", name);
            trailer_json.put("site", site);
            trailer_json.put("type", type);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return json_string;
    }

    public String getVideoLink(){
        return YOUTUBE_BASE_URL + key;
    }
}
