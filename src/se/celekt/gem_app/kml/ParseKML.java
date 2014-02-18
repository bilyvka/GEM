package se.celekt.gem_app.kml;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.util.Logger;

/**
 * Created by alisa on 6/7/13.
 */
public class ParseKML {
    private GoogleMap mGoogleMap;
    private String mapUrl;
    private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
    private KmlRoot kml;
    private List<Placemark> markers;
    private List<Style> styles;

    public ParseKML( String apUrl) {

        mapUrl = apUrl;
        try {
        InputStream input = new URL(mapUrl).openStream();
        Serializer serializer = new Persister();
        kml = serializer.read(KmlRoot.class, input);
        markers = kml.getDocument().getPlacemarks();
        styles = kml.getDocument().getStyles();
        } catch (IOException e) {
            e.printStackTrace();
            myLogger.log(Logger.SEVERE,"ERROR read kml file" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            myLogger.log(Logger.SEVERE,"ERROR parse kml file" + e.getMessage());
        }
    }

    public void drawMap(GoogleMap mGoogleMap){

            this.mGoogleMap = mGoogleMap;

            for(Placemark placemark:markers){
                if(placemark.getCoordinates() != null){
                    addMarker(mGoogleMap,placemark.getLat(),placemark.getLon(),placemark.getName(),setIcon(placemark.getStyleUrl()));
                }
                else {
                    addPolygon(mGoogleMap,placemark.getPolygon(),placemark.getStyleUrl());
                }
            }

            adjustMapZoomCenter();



    }
    private void addMarker(GoogleMap map, double lat, double lon, String title,BitmapDescriptor bitmapDescriptor) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .icon(bitmapDescriptor));
    }

    public static BitmapDescriptor getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(myBitmap);
            return bitmapDescriptor;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public BitmapDescriptor setIcon(String styleUrl){
        myLogger.log(Logger.INFO,"styleUrl " + styleUrl);
        // List<StyleMap> styleMaps = kml.getDocument().getStyleMaps();

        //for(StyleMap styleMap:styleMaps){
        //   String styleId = styleMap.getPairList().get(0).getStyleUrl();
        //  if(styleId != null && styleMap.getId().equals(styleUrl)){
        for(Style style:styles){

            if(styleUrl.equals("#" + style.getId())){

                return getBitmapFromURL(style.getHref());
            }
        }
        //   }
        //  }
        return BitmapDescriptorFactory.defaultMarker();
    }

    private void adjustMapZoomCenter(){
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;


        for(Placemark placemark:markers){
            maxLat = Math.max(placemark.getLat(), maxLat);

            if(placemark.getLat() != 0.0){
                minLat = Math.min(placemark.getLat(), minLat);
            }

            maxLon = Math.max(placemark.getLon(), maxLon);

            if(placemark.getLon() != 0.0){
                minLon = Math.min(placemark.getLon(), minLon);
            }

        }
        myLogger.log(Logger.INFO,"maxLat  " + maxLat);
        myLogger.log(Logger.INFO,"minLon  " + minLon);

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng((maxLat + minLat) / 2, (maxLon + minLon) / 2));
        myLogger.log(Logger.INFO,"Cener  " + new LatLng((maxLat + minLat) / 2, (maxLon + minLon) / 2));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
        mGoogleMap.moveCamera(center);
        mGoogleMap.animateCamera(zoom);

    }

    private void addPolygon( GoogleMap map,Polygon polygon,String styleUrl){
        PolygonOptions options = new PolygonOptions();
        options.addAll(polygon.cord());
        options.strokeColor(getColor(styleUrl));
        map.addPolygon(options);
    }

    private int getColor(String styleUrl){
        for(Style style:styles){
            if(styleUrl.equals("#" + style.getId())){
                myLogger.log(Logger.INFO,"Color " + style.getLine_color());
                if(style.getLine_color() != null){
                    return Color.parseColor(style.getLine_color());
                }
                else  return Color.BLACK;
            }
        }
        return Color.BLACK;
    }

    public Map<String,Integer> getPoints(){

        Map<String,Integer> points = new HashMap<String, Integer>();
        if(markers != null){
        for (Placemark placemark: markers){
            if(placemark.getCoordinates() != null){

                points.put(placemark.getName(),0);
            }
        }
        }
        return points;
    }
}
