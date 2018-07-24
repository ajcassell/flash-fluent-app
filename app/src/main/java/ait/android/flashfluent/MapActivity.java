package ait.android.flashfluent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ait.android.flashfluent.data.AppDatabaseLanguages;
import ait.android.flashfluent.data.Language;

public class MapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, AskDialog.LanguageHandler {

    private LocationManager locationManager;

    private GoogleMap mMap;
    private Button btnSelectFromList;
    public static HashMap<String, String> countryLanguage = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        makeHashMap();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnSelectFromList = findViewById(R.id.btnSelectFromList);

        btnSelectFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, LanguageSelectionActivity.class);
                MapActivity.this.startActivity(intent);
                MapActivity.this.finish();
            }
        });

        requestNeededPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
        } else {
            startLocationMonitoring();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                startLocationMonitoring();
            } else {
                Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationMonitoring() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationMonitoring() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationMonitoring();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hungary = new LatLng(47, 19);
        Marker myMarker = mMap.addMarker(new MarkerOptions().position(hungary).title(getString(R.string.hungary)).snippet(""));
        myMarker.setDraggable(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    Geocoder gc = new Geocoder(MapActivity.this, Locale.getDefault());
                    List<Address> address = null;
                    address = gc.getFromLocation(latLng.latitude, latLng.longitude, 2);

                    mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(address.get(0).getCountryName())
                            .snippet(address.get(0).getCountryName()));

                    final String country = address.get(0).getCountryName();

                    // Tells user if language has already been added or is not supported - before going to the dialog
                    final String languagePossible = countryLanguage.get(country);
                    new Thread() {
                        @Override
                        public void run() {
                            List<Language> langs = AppDatabaseLanguages.getAppDatabaseLanguages(MapActivity.this).languageDao().getLanguageWithThisName(languagePossible);

                            if (countryLanguage.get(country) != null) { // meaning language of this country IS supported
                                if (langs.isEmpty()) { // meaning languagePossible is new!
                                    showAskDialog(country);
                                } else {
                                    Snackbar.make(findViewById(R.id.activity_map), R.string.must_select_new_language, Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Snackbar.make(findViewById(R.id.activity_map), R.string.language_not_supported, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hungary));
    }

    private void showAskDialog(String country) {
        AskDialog askDialog = new AskDialog();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.country), country);
        askDialog.setArguments(bundle);
        askDialog.show(getSupportFragmentManager(), getString(R.string.ask_dialog));

    }

    @Override
    public void onNewLanguageCreated(String languageName) {
        //. . . .
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_info:
                showInfoDialog();
                break;
        }
        return true;
    }

    private void showInfoDialog() {
        new InfoDialog().show(getSupportFragmentManager(), getString(R.string.info_dialog));
    }

    private void makeHashMap() {

        countryLanguage.put("Afghanistan", "Pashto");
        countryLanguage.put("Albania", "Albanian");
        countryLanguage.put("Algeria", "Arabic");
        countryLanguage.put("Andorra", "Catalan");
        countryLanguage.put("Angola", "Portuguese");
        countryLanguage.put("Anguilla", "English");
        countryLanguage.put("Antigua", "English");
        countryLanguage.put("Barbuda", "English");
        countryLanguage.put("Argentina", "Spanish");
        countryLanguage.put("Armenia", "Armenian");
        countryLanguage.put("Australia", "English");
        countryLanguage.put("Austria", "German");
        countryLanguage.put("Azerbaijan", "Azerbaijani");


        countryLanguage.put("Bahamas", "English");
        countryLanguage.put("Bahrain", "Arabic");
        countryLanguage.put("Bangladesh", "Bengali");
        countryLanguage.put("Barbados", "English");
        countryLanguage.put("Belarus", "Belarusian");
        //countryLanguage.put("Belgium");
        countryLanguage.put("Belize", "English");
        countryLanguage.put("Benin", "French");
        countryLanguage.put("Bermuda", "English");
        //countryLanguage.put("Bhutan");
        countryLanguage.put("Bolivia", "Spanish");
        countryLanguage.put("Bosnia and Herzegovina", "Croatian");
        countryLanguage.put("Botswana", "English");
        countryLanguage.put("Brazil", "Portuguese");
        countryLanguage.put("Brunei Darussalam", "Malay");
        countryLanguage.put("Bulgaria", "Bulgarian");
        countryLanguage.put("Burkina Faso", "French");
        countryLanguage.put("Burma", "Burmese");
        countryLanguage.put("Burundi", "French");

        countryLanguage.put("Cambodia", "Khmer");
        countryLanguage.put("Cameroon", "French");
        countryLanguage.put("Canada", "English");
        countryLanguage.put("Cape Verde", "Portguese");
        countryLanguage.put("Cayman Islands", "English");
        countryLanguage.put("Central African Republic", "French");
        countryLanguage.put("Chad", "French");
        countryLanguage.put("Chile", "Spanish");
        countryLanguage.put("China", "Chinese");
        countryLanguage.put("Colombia", "Spanish");
        countryLanguage.put("Comoros", "Arabic");
        countryLanguage.put("Congo", "French");
        countryLanguage.put("Costa Rica", "Spanish");
        countryLanguage.put("Croatia", "Croatian");
        countryLanguage.put("Cuba", "Spanish");
        countryLanguage.put("Cyprus", "Turkish");
        countryLanguage.put("Czech Republic", "Czech");

        countryLanguage.put("Democratic Republic of Congo", "French");
        countryLanguage.put("Denmark", "Danish");
        countryLanguage.put("Djibouti", "Arabic");
        countryLanguage.put("Dominica", "English");
        countryLanguage.put("Dominican Republic", "Spanish");

        countryLanguage.put("Ecuador", "Spanish");
        countryLanguage.put("Egypt", "Arabic");
        countryLanguage.put("El Salvador", "Spanish");
        countryLanguage.put("Equatorial Guinea", "Spanish");
        countryLanguage.put("Eritrea", "Arabic");
        countryLanguage.put("Estonia", "Estonian");
        countryLanguage.put("Ethiopia", "Amharic");

        countryLanguage.put("Fiji", "English");
        countryLanguage.put("Finland", "Finnish");
        countryLanguage.put("France", "French");
        countryLanguage.put("French Guiana", "French");

        countryLanguage.put("Gabon", "French");
        countryLanguage.put("Gambia", "English");
        countryLanguage.put("Georgia", "Georgian");
        countryLanguage.put("Germany", "German");
        countryLanguage.put("Ghana", "English");
        countryLanguage.put("Great Britain", "English");
        countryLanguage.put("Greece", "Greek");
        countryLanguage.put("Grenada", "English");
        countryLanguage.put("Guadeloupe", "French");
        countryLanguage.put("Guatemala", "Spanish");
        countryLanguage.put("Guinea", "French");
        countryLanguage.put("Guinea-Bissau", "Portuguese");
        countryLanguage.put("Guyana", "English");

        countryLanguage.put("Haiti", "Haitian Creole");
        countryLanguage.put("Honduras", "Spanish");
        countryLanguage.put("Hungary", "Hungarian");

        countryLanguage.put("Iceland", "Icelandic");
        countryLanguage.put("India", "Hindi");
        countryLanguage.put("Indonesia", "Indonesian");
        countryLanguage.put("Iran", "Persian");
        countryLanguage.put("Iraq", "Kurdish");
        countryLanguage.put("Israel and the Occupied Territories", "Hebrew");
        countryLanguage.put("Italy", "Italian");
        countryLanguage.put("Ivory Coast", "French");

        //countryLanguage.put("Jamaica");
        countryLanguage.put("Japan", "Japanese");
        countryLanguage.put("Jordan", "Arabic");

        countryLanguage.put("Kazakhstan", "Kazakh");
        countryLanguage.put("Kenya", "Swahili");
        countryLanguage.put("Kuwait", "Arabic");
        countryLanguage.put("Kyrgyz Republic", "Kyrgyz");

        countryLanguage.put("Laos", "Lao");
        countryLanguage.put("Latvia", "Latvian");
        countryLanguage.put("Lebanon", "Arabic");
        countryLanguage.put("Lesotho", "English");
        countryLanguage.put("Liberia", "English");
        countryLanguage.put("Libya", "Arabic");
        countryLanguage.put("Liechtenstein", "German");
        countryLanguage.put("Lithuania", "Lithuanian");
        countryLanguage.put("Luxembourg", "Luxembourgish");

        countryLanguage.put("Republic of Macedonia", "Macedonian");
        countryLanguage.put("Madagascar", "Malagasy");
        countryLanguage.put("Malawi", "Chichewa");
        countryLanguage.put("Malaysia", "Malay");
        //countryLanguage.put("Maldives");
        countryLanguage.put("Mali", "French");
        countryLanguage.put("Malta", "Maltese");
        countryLanguage.put("Martinique", "French");
        countryLanguage.put("Mauritania", "Arabic");
        countryLanguage.put("Mauritius", "English");
        countryLanguage.put("Mayotte", "French");
        countryLanguage.put("Mexico", "Spanish");
        countryLanguage.put("Moldova", "Romanian");
        countryLanguage.put("Republic of Monaco", "French");
        countryLanguage.put("Mongolia", "Mongolian");
        //countryLanguage.put("Montenegro");
        countryLanguage.put("Montserrat", "English");
        countryLanguage.put("Morocco", "Arabic");
        countryLanguage.put("Mozambique", "Portuguese");

        countryLanguage.put("Namibia", "English");
        countryLanguage.put("Nepal", "Nepali");
        countryLanguage.put("Netherlands", "Dutch");
        countryLanguage.put("New Zealand", "English");
        countryLanguage.put("Nicaragua", "Spanish");
        countryLanguage.put("Niger", "French");
        countryLanguage.put("Democratic Republic of Korea", "Korean");
        countryLanguage.put("Norway", "Norwegian");

        countryLanguage.put("Oman", "Arabic");

        countryLanguage.put("Pacific Islands", "English");
        countryLanguage.put("Pakistan", "Urdu");
        countryLanguage.put("Panama", "Spanish");
        countryLanguage.put("Papua New Guinea", "English");
        countryLanguage.put("Paraguay", "Spanish");
        countryLanguage.put("Peru", "Spanish");
        countryLanguage.put("Philippines", "Filipino");
        countryLanguage.put("Poland", "Polish");
        countryLanguage.put("Portugal", "Portuguese");
        countryLanguage.put("Puerto Rico", "Spanish");

        countryLanguage.put("Qatar", "Arabic");

        countryLanguage.put("Reunion", "French");
        countryLanguage.put("Romania", "Romanian");
        countryLanguage.put("Russian Federation", "Russian");
        countryLanguage.put("Rwanda", "French"); // eh

        countryLanguage.put("Saint Kitts and Nevis", "English");
        countryLanguage.put("Saint Lucia", "English");
        countryLanguage.put("Saint Vincent's and Grenadines", "English");
        countryLanguage.put("Samoa", "Samoan");
        countryLanguage.put("Sao Tome and Principe", "Portuguese");
        countryLanguage.put("Saudi Arabia", "Arabic");
        countryLanguage.put("Senegal", "French");
        countryLanguage.put("Serbia", "Serbian");
        countryLanguage.put("Seychelles", "French");
        countryLanguage.put("Sierra Leone", "English");
        //countryLanguage.put("Singapore");
        countryLanguage.put("Slovak Republic", "Slovak");
        countryLanguage.put("Slovenia", "Slovenian");
        countryLanguage.put("Solomon Islands", "English");
        countryLanguage.put("Somalia", "Somali");
        countryLanguage.put("South Africa", "English");
        countryLanguage.put("Republic of Korea", "Korean");
        countryLanguage.put("South Sudan", "English");
        countryLanguage.put("Spain", "Spanish");
        countryLanguage.put("Sri Lanka", "Sinhala");
        countryLanguage.put("Sudan", "Arabic");
        countryLanguage.put("Suriname", "Dutch");
        countryLanguage.put("Swaziland", "English");
        countryLanguage.put("Sweden", "Swedish");
        countryLanguage.put("Switzerland", "German");
        countryLanguage.put("Syria", "Arabic");

        countryLanguage.put("Tajikistan", "Tajik");
        countryLanguage.put("Tanzania", "Swahili");
        countryLanguage.put("Thailand", "Thai");
        countryLanguage.put("Timor Leste", "Portuguese");
        countryLanguage.put("Togo", "French");
        countryLanguage.put("Trinidad and Tobago", "English");
        countryLanguage.put("Tunisia", "Arabic");
        countryLanguage.put("Turkey", "Turkish");
        countryLanguage.put("Turkmenistan", "Turkish");
        countryLanguage.put("Turks and Caicos Islands", "English");

        countryLanguage.put("Uganda", "Swahili");
        countryLanguage.put("Ukraine", "Ukrainian");
        countryLanguage.put("United Arab Emirates", "Arabic");
        countryLanguage.put("United States of America", "English");
        countryLanguage.put("Uruguay", "Spanish");
        countryLanguage.put("Uzbekistan", "Uzbek");

        countryLanguage.put("Venezuela", "Spanish");
        countryLanguage.put("Vietnam", "Vietnamese");
        countryLanguage.put("Virgin Islands (UK)", "English");
        countryLanguage.put("Virgin Islands (US)", "English");

        countryLanguage.put("Yemen", "Arabic");

        countryLanguage.put("Zambia", "English");
        countryLanguage.put("Zimbabwe", "Chichewa");



    }
}
