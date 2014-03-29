package com.example.grizzynav.grizzynav;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class GPSstuff extends ActionBarActivity {
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsstuff);
        final TextView textView = (TextView) findViewById(R.id.Info);

        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            float distance=0, dleft=0, speed=0;
            //double newLat, newLong;
            //long lastTime=0,newTime=0;
            //double lastLat = 0, lastLong = 0;
            Location lastKnownG = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            //Location lastKnownN = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            public void onLocationChanged(Location location) {
                count++;

               /* newLat=location.getLatitude();
                newLong=location.getLongitude();
                newTime=location.getTime();*/
                if(isBetterLocation(location,lastKnownG))
                {
                    float[] results = new float[3];
                    Location.distanceBetween(lastKnownG.getLatitude(),lastKnownG.getLongitude(),location.getLatitude(),location.getLongitude(),results);
                    speed=1000*results[0]/(location.getTime()-lastKnownG.getTime());
                    distance += results[0];
                    //textView.setText(location.getProvider()+"\nDistance: " + distance + "\nSpeed: "+speed+"\nEstimated time: "+(distance/speed));
                }
                /*if(count<2)
                {
                    lastLat=newLat;
                    lastLong=newLong;
                    lastTime=newTime;
                }*/

                /*float[] results = new float[3];
                Location.distanceBetween(lastLat,lastLong,newLat,newLong,results);
                speed=1000*results[0]/(newTime-lastTime);
                distance += results[0];*/
                //lastLat=newLat;
                //lastLong=newLong;
                //lastTime=newTime;
                lastKnownG=location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpsstuff, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
