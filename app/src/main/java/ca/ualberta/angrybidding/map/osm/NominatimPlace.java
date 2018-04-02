package ca.ualberta.angrybidding.map.osm;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.postphere.map.LocationArea;
import com.postphere.map.LocationPoint;
import com.slouple.lib.JsonRequest;
import com.slouple.lib.JsonResponseListener;
import com.slouple.util.Listener;

import java.util.ArrayList;

public class NominatimPlace {
    private long placeID;
    private LocationPoint locationPoint;
    private String displayName;
    private int placeRank;
    private double importance;
    private LocationArea boundingBox;

    public NominatimPlace(JsonObject jsonObject){
        placeID = jsonObject.get("place_id").getAsLong();
        double lat = jsonObject.get("lat").getAsDouble();
        double lon = jsonObject.get("lon").getAsDouble();
        locationPoint = new LocationPoint(lat, lon);
        displayName = jsonObject.get("display_name").getAsString();
        placeRank = jsonObject.get("place_rank").getAsInt();
        importance = jsonObject.get("importance").getAsDouble();
        JsonArray boundingBoxArra = jsonObject.get("boundingbox").getAsJsonArray();
        double lat1 = boundingBoxArra.get(0).getAsDouble();
        double lat2 = boundingBoxArra.get(1).getAsDouble();
        double lon1 = boundingBoxArra.get(2).getAsDouble();
        double lon2 = boundingBoxArra.get(3).getAsDouble();
        boundingBox = new LocationArea(new LocationPoint(lat1, lon1), new LocationPoint(lat2, lon2));
    }

    public long getPlaceID(){
        return placeID;
    }

    public LocationPoint getLocationPoint(){
        return locationPoint;
    }

    public String getDisplayName(){
        return displayName;
    }

    public int getPlaceRank(){
        return placeRank;
    }

    public double getImportance(){
        return importance;
    }

    public LocationArea getBoundingBox(){
        return boundingBox;
    }

    public int getZ(int w, int h, int minZ, int maxZ){
        LocationPoint minPoint = getBoundingBox().getMin();
        LocationPoint maxPoint = getBoundingBox().getMax();
        minPoint.setZ(maxZ);
        maxPoint.setZ(maxZ);
        double wP = Math.abs(maxPoint.getX() - minPoint.getX());
        double hP = Math.abs(maxPoint.getY() - minPoint.getY());
        double wN = Math.log(wP / w) / Math.log(2);
        double hN = Math.log(hP / h) / Math.log(2);
        double n = Math.max(wN, hN);
        return (int) Math.floor(maxZ - n);
    }

    public static void search(String query, Context context, final Listener<ArrayList<NominatimPlace>> listener){
        search(query, null, context, listener);
    }


    public static void search(String query, LocationArea area, Context context, final Listener<ArrayList<NominatimPlace>> listener){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("nominatim.openstreetmap.org");
        builder.path("search");
        builder.appendQueryParameter("q", query);
        builder.appendQueryParameter("format", "jsonv2");
        builder.appendQueryParameter("accept-language", "*");
        if(area != null){
            builder.appendQueryParameter("viewbox",
                    area.getMin().getLongitude() + "," +
                    area.getMin().getLatitude() + "," +
                    area.getMax().getLongitude() + "," +
                    area.getMax().getLatitude()
                    );
        }
        JsonRequest request = new JsonRequest(builder.build().toString(), new JsonResponseListener() {

            @Override
            public void onSuccess(JsonElement element) {
                ArrayList<NominatimPlace> places = new ArrayList<>();
                JsonArray jsonArray = element.getAsJsonArray();
                for(int i = 0; i < jsonArray.size(); i++){
                    places.add(new NominatimPlace(jsonArray.get(i).getAsJsonObject()));
                }
                listener.onSuccess(places);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        }, context);
        request.submit();
    }

}
