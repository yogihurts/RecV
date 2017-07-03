package internship.irina.com.recv;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.support.v7.app.AlertDialog;
import com.google.android.gms.maps.model.LatLng;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.R.id.list;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    APIInterface apiService;

    String latLngString = "-33.8670522,151.1957362";
    LatLng latLng;


    private List<Item> items;
    //private RecyclerView rv;
    //@BindView(R.id.title) TextView title;
    //rv = (RecyclerView) findViewById(R.id.rv);
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;
    List<PlacesPOJO.CustomA> results;
    RVAdapter adapter;
    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        items = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(items, new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            }
        });

        rv.setAdapter(adapter);

        realm.init(this);
        realm = Realm.getDefaultInstance();

        realm
                .where(Item.class)
                .findAllAsync()
                .addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
                    @Override
                    public void onChange(RealmResults<Item> items) {
                        Log.d("ITEM COUNT", items.size() + "");
                        MainActivity.this.items = items;
                        MainActivity.this.adapter.setItems(MainActivity.this.items);
                        MainActivity.this.adapter.notifyDataSetChanged();
                    }
                });

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            else {
                fetchLocation();
            }
        } else {
            fetchLocation();
        }


        apiService = APIClient.getClient().create(APIInterface.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString().trim();
                String[] split = s.split("\\s+");


                if (split.length != 2) {
                    Toast.makeText(getApplicationContext(), "Please enter text in the required format", Toast.LENGTH_SHORT).show();
                } else
                    fetchStores(split[0], split[1]);
            }
        });

    }

    private void fetchStores(String placeType, String businessName) {

        Call call = apiService.doPlaces(placeType, latLngString, businessName, true, "distance", APIClient.GOOGLE_PLACE_API_KEY);
        call.enqueue(new Callback<PlacesPOJO.Root>() {
            @Override
            public void onResponse(Call<PlacesPOJO.Root> call, Response<PlacesPOJO.Root> response) {
                PlacesPOJO.Root root = response.body();

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error " + response.code() + " found.", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (!root.status.equals("OK")) {
                    Toast.makeText(getApplicationContext(), "No matches found near you", Toast.LENGTH_SHORT).show();

                    return;
                }

                results = root.customA;

                for (int i = 0; i < results.size(); i++) {

                    if (i == 10) {
                        break;
                    }

                    PlacesPOJO.CustomA info = results.get(i);
                    Item it = new Item(info.name, info.vicinity);
                    items.add(it);

                }

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(items);
                realm.commitTransaction();

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PlacesPOJO.Root> call, Throwable t) {
                // Log error here since request failed
                call.cancel();
            }
        });


    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                } else {
                    fetchLocation();
                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void fetchLocation() {

//        SmartLocation.with(this).location()
//               .oneFix()
//                .start(new OnLocationUpdatedListener() {
//                    @Override
//                    public void onLocationUpdated(Location location) {
//                        latLngString = location.getLatitude() + "," + location.getLongitude();
//                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    }
//                });
//    }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}