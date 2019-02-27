package com.example.jvhe.petus.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jvhe.petus.Adapter.CafeRecyclerAdapter;
import com.example.jvhe.petus.Class.AreaSelectDialog;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.R;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.CafeItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;


public class CafeListFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "CafeListFragment";
    private static final int MAP_SELECT = 7001;

    public static CafeListFragment newInstance() {
        CafeListFragment fragment = new CafeListFragment();
        return fragment;
    }

    TextView tv_area, tv_area_cafe;

    FrameLayout fl_show;
    ImageView iv_map_show, iv_grid_show;
    boolean is_map_shown = false;

    // 지도 관련 변수
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.482991, 126.9742317);
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;

    private GoogleMap googleMap = null;
    private MapView mapView = null;
    private GoogleApiClient googleApiClient = null;
    private Marker currentMarker = null;

//    // 스피너 관련 변수
//    Spinner spinner_location;
//    SpinnerAdapter spinnerAdapter;
//    final ArrayList<String> location_list = new ArrayList<>();

    // 커스텀 다이얼로그 관련 변수
    AreaSelectDialog areaSelectDialog;

    // 그리드뷰 관련 변수
    RecyclerView recycler_view_grid;
    CafeRecyclerAdapter cafeRecyclerAdapter;
    GridLayoutManager gridLayoutManager;
    ArrayList<CafeItem> cafeItemArrayList;

    // 클러스터링 관련 변수.
    ClusterManager<CafeItem> clusterManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

//        getMyLocation();
//        // 위치 서비스 확인
//        if (!checkLocationServicesStatus()) {
//            Toast.makeText(getContext(), "위치 권한 획득아직 안됨", Toast.LENGTH_SHORT).show();
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_ENABLE_REQUEST_CODE);
//
////            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////            builder.setTitle("위치 서비스 비활성화");
////            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
////                    "위치 설정을 수정하십시오.");
////            builder.setCancelable(true);
////            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
////                @Override
////                public void onClick(DialogInterface dialogInterface, int i) {
////                    Intent callGPSSettingIntent =
////                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
////                }
////            });
////            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
////                @Override
////                public void onClick(DialogInterface dialogInterface, int i) {
////                    dialogInterface.cancel();
////                }
////            });
////            builder.create().show();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cafe_list, container, false);

//        ImageView iv_map_icon = view.findViewById(R.id.iv_map_icon);
//        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(iv_map_icon);
//        Glide.with(this).load(R.drawable.pin).into(gifImage);

        tv_area = view.findViewById(R.id.tv_area);
        tv_area_cafe = view.findViewById(R.id.tv_area_cafe);


        // 지역선택 다이얼로그 관련 작동
//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해
//        int width = dm.widthPixels; //디바이스 화면 너비

        areaSelectDialog = new AreaSelectDialog(getContext());
//        WindowManager.LayoutParams wm = areaSelectDialog.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
//        wm.copyFrom(areaSelectDialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미
//        wm.width = (int) (width / 1.2);  //화면 너비의 절반


        LinearLayout ll_area = view.findViewById(R.id.ll_area);
        ll_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areaSelectDialog.show();
            }
        });

        areaSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tv_area.setText(areaSelectDialog.getValue());
                tv_area_cafe.setText(areaSelectDialog.getValue() + " 카페");
            }
        });

        mapView = view.findViewById(R.id.map);
        mapView.getMapAsync(this);
//


        // 스피너 관련 선언
//        spinner_location = view.findViewById(R.id.spinner_location);
//        location_list.add("내 주변");
//        location_list.add("전체");
//        location_list.add("서울");
//        location_list.add("경기");
//        location_list.add("인천");
//        location_list.add("부산");
//        location_list.add("대구");
//        location_list.add("광주");
//        location_list.add("대전");
//        location_list.add("울산");
//        location_list.add("강원");
//        location_list.add("충남");
//        location_list.add("충북");
//        location_list.add("경남");
//        location_list.add("경북");
//        location_list.add("전남");
//        location_list.add("전북");
//        location_list.add("세종");
//        location_list.add("제주");
//
//        spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, location_list);
//        spinner_location.setAdapter(spinnerAdapter);
//
//        spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "선택 아이템: "+spinner_location.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        // 그리드뷰 임시 데이터
        cafeItemArrayList = new ArrayList<>();
//        cafeItemArrayList.add(new CafeItem("테스트1"));
//        cafeItemArrayList.add(new CafeItem("테스트2"));
//        cafeItemArrayList.add(new CafeItem("테스트3"));
//        cafeItemArrayList.add(new CafeItem("테스트4"));
//        cafeItemArrayList.add(new CafeItem("테스트5"));
//        cafeItemArrayList.add(new CafeItem("테스트6"));
//        cafeItemArrayList.add(new CafeItem("테스트7"));
//        cafeItemArrayList.add(new CafeItem("테스트8"));
//        cafeItemArrayList.add(new CafeItem("테스트9"));
//        cafeItemArrayList.add(new CafeItem("테스트10"));
//        cafeItemArrayList.add(new CafeItem("테스트11"));

        // 그리드뷰 관련 선언
        recycler_view_grid = view.findViewById(R.id.recycler_view_grid);
        cafeRecyclerAdapter = new CafeRecyclerAdapter(getContext(), cafeItemArrayList);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recycler_view_grid.setAdapter(cafeRecyclerAdapter);
        recycler_view_grid.setLayoutManager(gridLayoutManager);

        iv_map_show = view.findViewById(R.id.iv_map_show);
        iv_grid_show = view.findViewById(R.id.iv_grid_show);

        fl_show = view.findViewById(R.id.fl_show);
        fl_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_map_shown) {
                    mapView.setVisibility(View.INVISIBLE);
                    recycler_view_grid.setVisibility(View.VISIBLE);
                    iv_map_show.setVisibility(View.VISIBLE);
                    iv_grid_show.setVisibility(View.INVISIBLE);
                } else {
                    mapView.setVisibility(View.VISIBLE);
                    recycler_view_grid.setVisibility(View.INVISIBLE);
                    iv_map_show.setVisibility(View.INVISIBLE);
                    iv_grid_show.setVisibility(View.VISIBLE);
                }
                is_map_shown = !is_map_shown;
            }
        });

        iv_grid_show.setVisibility(View.INVISIBLE);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MAP_SELECT) {
                String area = "";
                if (data.getStringExtra("area") != null) {
                    area = data.getStringExtra("area");
                    tv_area.setText(area);
                    tv_area_cafe.setText(area + " 카페");

                    if (area.equals("내 주변")) {
                        LatLng current_place = new LatLng(getMyLocation().getLatitude(), getMyLocation().getLongitude());
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current_place));
//                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                }
            }
        }
    }


    //

    //    private static final int UPDATE_INTERVAL_MS = 15000;
//    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;
//
    //
//    private final static int MAXENTRIES = 5;
//    private String[] LikelyPlaceNames = null;
//    private String[] LikelyAddresses = null;
//    private String[] LikelyAttributions = null;
//    private LatLng[] LikelyLatLngs = null;
//

    //사용자 위치 수신기
    private LocationManager locationManager;
//    private LocationListener locationListener;
    /**
     * 사용자의 위치를 수신
     */
    private static final int REQUEST_CODE_LOCATION = 2;

    //
//    boolean mLocationPermissionGranted = false;

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//액티비티가 처음 생성될 때 실행되는 함수

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.setVisibility(View.INVISIBLE);
        }

    }

    //
    @Override
    public void onMapReady(GoogleMap googleMap) {


//        -------------------------------------------------
//        LatLng InuGu = new LatLng(37.483948, 126.971745);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//
//        markerOptions.position(InuGu);
//
//        markerOptions.title("이누구");
//
//        markerOptions.snippet("애견카페입니다~");
//
//        googleMap.addMarker(markerOptions);
//        LatLng CoolPet = new LatLng(37.4843742, 126.9714925);
//        markerOptions = new MarkerOptions();
//
//        markerOptions.position(CoolPet);
//
//        markerOptions.title("쿨펫 미용학원");
//
//        markerOptions.snippet("애견 미용학원이에요~ 애견카페입니다~");
//
//        googleMap.addMarker(markerOptions);
//
//        LatLng PetShop = new LatLng(37.483208, 126.973121);
//        markerOptions = new MarkerOptions();
//
//        markerOptions.position(PetShop);
//
//        markerOptions.title("펫샵");
//
//        markerOptions.snippet("사랑스러운 애견을 위한 전문 쇼핑몰");
//
//        googleMap.addMarker(markerOptions);
//        PetShop = new LatLng(37.4843502, 126.9707162);
//        markerOptions = new MarkerOptions();
//
//        markerOptions.position(PetShop);
//
//        markerOptions.title("써니네 애견카페");
//
//        markerOptions.snippet("써니네로 놀러오세요~");
//
//        googleMap.addMarker(markerOptions);
//
//        ---------------------------------------------------------


//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(InuGu));
//
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

// OnMapReadyCallback implements 해야 map.getMapAsync(this); 사용가능. this 가 OnMapReadyCallback


        this.googleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에 지도의 초기위치를 서울로 이동
        setCurrentLocation(null, "위치정보 가져올 수 없음", "위치 퍼미션과 GPS 활성 여부 확인");

        //나침반이 나타나도록 설정
        googleMap.getUiSettings().setCompassEnabled(true);
        // 매끄럽게 이동함
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        //  API 23 이상이면 런타임 퍼미션 처리 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 사용권한체크
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //사용권한이 없을경우
                //권한 재요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //사용권한이 있는경우
                if (googleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        } else {

            if (googleApiClient == null) {
                buildGoogleApiClient();
            }
            googleMap.setMyLocationEnabled(true);
        }
// 중요함

        LatLng current_place;
//        new LatLng(getMyLocation().getLatitude(),
//                getMyLocation().getLongitude());
        if (getMyLocation() == null)
            current_place = new LatLng(StaticData.my_lat, StaticData.my_lng);
        else {
            current_place = new LatLng(getMyLocation().getLatitude(), getMyLocation().getLongitude());
            StaticData.my_lat = getMyLocation().getLatitude();
            StaticData.my_lng = getMyLocation().getLongitude();
        }
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(current_place);
//
//        markerOptions.title("현재 위치");
//
//        markerOptions.snippet("현재 위치입니다.");
//
//        googleMap.addMarker(markerOptions);
//
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_place, 15));

//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // 현재 위치 기준으로, 구글에서 반경 5키로미터 근처의 동물카페 검색 결과를 가져와서 지도 위에 보여줄 것이다.
        String data = "";
        String url = StaticData.url + "login_android.php";
        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + getMyLocation().getLatitude() + "," + getMyLocation().getLongitude() + "&radius=2000&keyword=%EC%95%A0%EA%B2%AC%EC%B9%B4%ED%8E%98&key=AIzaSyAWheKGum5zVeWuuW9efB9glFYHdO5Yfy8&language=ko";
        HttpAsync httpasync = new HttpAsync(data, url, handler);
        httpasync.execute();

        // 클러스터 매니져 생성
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.getMarkerCollection().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
//                startActivity(new Intent(getContext(), CafeInformationActivity.class).putExtra("cafe", );
            }
        });


    }

    //
    private Location getMyLocation() {
        Location currentLocation;
        currentLocation = new Location("");
        currentLocation.setLatitude(StaticData.my_lat);
        currentLocation.setLongitude(StaticData.my_lng);
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청 프래그먼트에선 Fragment의 requestPermissions를 써야 한다.
//            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
//            getMyLocation();
        } else {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 200, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 200, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
                Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
//                Toast.makeText(getContext(), "longtitude=" + lng + ", latitude=" + lat, Toast.LENGTH_SHORT).show();
            } else {
                currentLocation = new Location("");
                currentLocation.setLatitude(StaticData.my_lat);
                currentLocation.setLongitude(StaticData.my_lng);
            }
        }
        Log.e(TAG, "getMyLocation is called!\nlat:" + currentLocation.getLatitude() + "lng:" + currentLocation.getLongitude());
        return currentLocation;
    }

    // 권한 획득 이후 결과처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e(TAG, "위치 권한 스위치문");
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                Log.e(TAG, "위치 권한 획득 검사 " + grantResults.length + " " + grantResults[0]);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(TAG, "위치 권한 획득 성공!");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getMyLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }


    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        googleApiClient.connect();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        if (location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(currentLocation);
//            markerOptions.title(markerTitle);
//            markerOptions.snippet(markerSnippet);
//            markerOptions.draggable(true);
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            currentMarker = this.googleMap.addMarker(markerOptions);

            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(DEFAULT_LOCATION);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        currentMarker = this.googleMap.addMarker(markerOptions);

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    //
//
//    private void getDeviceLocation() {
//        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this.getActivity(),
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//        // A step later in the tutorial adds the code to get the device location.
//    }
//

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!checkLocationServicesStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }
//
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(UPDATE_INTERVAL_MS);
//        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(getActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                LocationServices.FusedLocationApi
//                        .requestLocationUpdates(googleApiClient, locationRequest, this);
//            }
//        } else {
//            LocationServices.FusedLocationApi
//                    .requestLocationUpdates(googleApiClient, locationRequest, this);
//
//            this.googleMap.getUiSettings().setCompassEnabled(true);
//            this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        }
//
//    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    //
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = new Location("");
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude((DEFAULT_LOCATION.longitude));

        setCurrentLocation(location, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS활성 여부 확인");
    }

    //
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged call..");

        StaticData.my_lat = location.getLatitude();
        StaticData.my_lng = location.getLongitude();
//        searchCurrentPlaces();
    }
//
////    private void searchCurrentPlaces() {
////        @SuppressWarnings("MissingPermission")
////        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
////                .getCurrentPlace(googleApiClient, null);
////        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>(){
////
////            @Override
////            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
////                int i = 0;
////                LikelyPlaceNames = new String[MAXENTRIES];
////                LikelyAddresses = new String[MAXENTRIES];
////                LikelyAttributions = new String[MAXENTRIES];
////                LikelyLatLngs = new LatLng[MAXENTRIES];
////
////                for(PlaceLikelihood placeLikelihood : placeLikelihoods) {
////                    LikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
////                    LikelyAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
////                    LikelyAttributions[i] = (String) placeLikelihood.getPlace().getAttributions();
////                    LikelyLatLngs[i] = placeLikelihood.getPlace().getLatLng();
////
////                    i++;
////                    if(i > MAXENTRIES - 1 ) {
////                        break;
////                    }
////                }
////
////                placeLikelihoods.release();
////
////                Location location = new Location("");
////                location.setLatitude(LikelyLatLngs[0].latitude);
////                location.setLongitude(LikelyLatLngs[0].longitude);
////
////                setCurrentLocation(location, LikelyPlaceNames[0], LikelyAddresses[0]);
////            }
////        });
////
////    }


    // http통신 관련 처리하는 핸들러
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                JSONObject jsonObject;
                String status = "";
                try {
                    jsonObject = new JSONObject(msg.getData().getString("data_arr"));
                    status = jsonObject.getString("status");
                    if (status.equals("OK")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            CafeItem cafeItem = new CafeItem();
                            JSONObject geometry = jObject.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            cafeItem.setLatitude(location.getDouble("lat"));
                            cafeItem.setLongitude(location.getDouble("lng"));
                            cafeItem.setName(jObject.getString("name"));
                            cafeItem.setLocation(jObject.getString("vicinity"));
                            cafeItem.setPlace_id(jObject.getString("place_id"));
                            cafeItem.setDistance(getDistance(StaticData.my_lat, StaticData.my_lng, location.getDouble("lat"), location.getDouble("lng")));


                            // 이미지 링크 받아옴.
                            if (jObject.has("photos")) {
                                JSONArray photos = jObject.getJSONArray("photos");
                                Log.e(TAG, photos.length() + "개 있음");

                                JSONObject getPhtotos = photos.getJSONObject(0);
                                String photo_reference = getPhtotos.getString("photo_reference");
                                cafeItem.setImage_url(photo_reference);
//                                sb.append(photo_reference);
//                                sb.append("&key=...");
//                                    break;
                                Log.e(TAG, "번째 : " + cafeItem.getImage_url());


//                            cafeItem.setImage_url(photos.getString("photo_reference"));
                            } else {
                                cafeItem.setImage_url("");
                            }
//                            LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
//                            MarkerOptions markerOptions = new MarkerOptions();
//                            markerOptions.position(latLng);
//                            markerOptions.title(jObject.getString("name"));
//                            markerOptions.snippet(jObject.getString("vicinity"));
////
//                            googleMap.addMarker(markerOptions);

                            cafeItemArrayList.add(cafeItem);
                            // 지워봄
//                            cafeRecyclerAdapter.notifyDataSetChanged();

                            clusterManager.addItem(cafeItem);


                        }

                        Collections.sort(cafeItemArrayList, new Comparator<CafeItem>() {
                            @Override
                            public int compare(CafeItem o1, CafeItem o2) {
                                return Double.compare(o1.getDistance(), o2.getDistance());
                            }
                        });
                        cafeRecyclerAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                assert data_arr != null;
//                if (data_arr.contains("ZERO_RESULTS")) {
//                    Toast.makeText(getContext(), "(구글)검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data_arr);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }


//                if (data_arr.contains("!!fail!!")) {
////                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data_arr);
//                        SharedPreferences pref = getSharedPreferences("current_user", 0);
//                        SharedPreferences.Editor edit = pref.edit();
//                        edit.putBoolean("is_login", true);
//                        edit.putInt("id_db", Integer.parseInt(obj.getString("id")));
//                        edit.putString("email", obj.getString("email"));
//                        edit.putString("name", obj.getString("name"));
//                        edit.putString("password", obj.getString("password"));
//                        edit.commit();
//                        updateUI();
//                        Toast.makeText(getApplicationContext(), "결과 " + data_arr, Toast.LENGTH_SHORT).show();
//                        finish();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    };


    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }
}
