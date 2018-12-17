package com.threes.scenespotinseoul.ui.map;

import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_LOCATION_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SEARCH_KEYWORD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapProjection;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.AppDatabase;
import com.threes.scenespotinseoul.data.model.Location;
import com.threes.scenespotinseoul.data.model.LocationTag;
import com.threes.scenespotinseoul.data.model.Tag;
import com.threes.scenespotinseoul.ui.location.LocationDetailActivity;
import com.threes.scenespotinseoul.ui.search.SearchActivity;
import com.threes.scenespotinseoul.utilities.AppExecutors;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapFragment extends Fragment {

  private static final String CLIENT_ID = "bu0cuRVRS2yONcn8FGxq"; // 애플리케이션 클라이언트 아이디 값

  private ImageButton myloc;
  private NMapContext mMapContext;
  private NMapView mapView;
  private NMapOverlayManager nMapOverlayManager;
  private NMapResourceProvider mMapViewerResourceProvider;
  private NMapView.OnMapStateChangeListener nMapstateListener;
  private NMapController mapController;
  private NMapLocationManager nMapLocationManager;
  private NMapPOIdataOverlay nMapPOIdataOverlay;
  private NMapPOIdata poiData;
  private Gpsinfo gps;
  private double mylat = 0;
  private double mylon = 0;

  private TextView title, tag, godetail;
  private ImageView image;
  private ConstraintLayout frameLayout;
  private CardView cardView;
  private Handler handler = new Handler();

  private ArrayList<String> mTagLists;
  private String location_id;

  @SuppressLint("ClickableViewAccessibility")
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    gps = new Gpsinfo(rootView.getContext());
    myloc = rootView.findViewById(R.id.myloc);
    myloc.setOnClickListener(
        view -> {
          mylat = gps.getLocation().getLatitude();
          mylon = gps.getLocation().getLongitude();
          mapController.setMapCenter(mylon, mylat);
          mapController.setZoomLevel(10);
          NMapPOIdata nMapPOIdata = new NMapPOIdata(2,mMapViewerResourceProvider);
          nMapPOIdata.removeAllPOIdata();
          nMapPOIdata.beginPOIdata(1);
          nMapPOIdata.addPOIitem(mylon, mylat, "내 위치", NMapPOIflagType.FROM,0);
          nMapPOIdata.endPOIdata();
          nMapOverlayManager.createPOIdataOverlay(nMapPOIdata, null);
        });

    title = rootView.findViewById(R.id.title);
    tag = rootView.findViewById(R.id.tag);
    godetail = rootView.findViewById(R.id.godetail);
    godetail.setOnClickListener(
        view -> {
          Intent intent = new Intent(this.getContext(), LocationDetailActivity.class);
          intent.putExtra(EXTRA_LOCATION_ID, location_id);
          startActivity(intent);
        });
    location_id = null;
    image = rootView.findViewById(R.id.image);
    frameLayout = rootView.findViewById(R.id.detailLayout);
    cardView = rootView.findViewById(R.id.cardview);

    mTagLists = new ArrayList<String>();
    return rootView;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mMapContext = new NMapContext(Objects.requireNonNull(getActivity()));
    mMapContext.onCreate();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mapView = Objects.requireNonNull(getView()).findViewById(R.id.mapView);
    mapView.setClientId(CLIENT_ID); // 클라이언트 아이디 설정
    mMapContext.setupMapView(mapView);
  }

  @Override
  public void onStart() {
    super.onStart();
    mMapContext.onStart();
    mapView.setClickable(true);
    mapView.displayZoomControls(true);
    mapView.setEnabled(true);
    // mapView.setFocusable(true);
    // mapView.setFocusableInTouchMode(true);
    // mapView.requestFocus();
    // mapView.setOnMapStateChangeListener(OnMapViewStateChangeListener); //리스너 등록
    mapController = mapView.getMapController();
    mMapViewerResourceProvider = new NMapViewerResourceProvider(getContext(), this::showLayout);
    nMapOverlayManager =
        new NMapOverlayManager(
            Objects.requireNonNull(getContext()),
            mapView,
            mMapViewerResourceProvider); // null 자리에 mMapViewerResourceProiveder 로 오버레이 아이템들을 내맘대로
    // 꾸밀 수 있다.
    NMapProjection nMapProjection;
    nMapLocationManager = new NMapLocationManager(this.getContext());
    mapView.setOnMapViewTouchEventListener(
        new NMapView.OnMapViewTouchEventListener() {
          @Override
          public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {}

          @Override
          public void onLongPressCanceled(NMapView nMapView) {}

          @Override
          public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {}

          @Override
          public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {}

          @Override
          public void onScroll(
              NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {}

          @Override
          public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e("result.info", "tab");
            frameLayout.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.GONE);
          }
        });
    AppDatabase db = AppDatabase.getInstance(getContext());
    AppExecutors executors = new AppExecutors();
    executors
        .diskIO()
        .execute(
            () -> {
              List<Location> locations = db.locationDao().loadAll();
              executors
                  .mainThread()
                  .execute(
                      () -> {
                        // 메인 스레드에서 데이터 처리
                        poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
                        Log.e("result.info", String.valueOf(locations.size()));
                        poiData.beginPOIdata(locations.size());

                        for (int i = 0; i < locations.size(); i++) {
                          poiData.addPOIitem(
                              locations.get(i).getLon(),
                              locations.get(i).getLat(),
                              locations.get(i).getName(),
                              NMapPOIflagType.PIN,
                              i+1);
                        }
                        poiData.endPOIdata();
                        nMapOverlayManager.createPOIdataOverlay(poiData, null);
                      });
            });
    NMapView.OnMapStateChangeListener OnMapViewStateChangeListener =
        new NMapView.OnMapStateChangeListener() {
          @Override
          public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if (nMapError == null) {
              startMyLocation();
            }
          } // 맵 초기화

          @Override
          public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {}

          @Override
          public void onMapCenterChangeFine(NMapView nMapView) {}

          @Override
          public void onZoomLevelChange(NMapView nMapView, int i) {}

          @Override
          public void onAnimationStateChange(NMapView nMapView, int i, int i1) {}
        };
  }

  private void startMyLocation() {
    ActivityCompat.requestPermissions(
        this.getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    nMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
    boolean isMyLocationEnabled = nMapLocationManager.enableMyLocation(true);
    if (!isMyLocationEnabled) {
      Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      startActivity(goToSettings);
    }
  }

  final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener =
      new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(
            NMapLocationManager locationManager, NGeoPoint myLocation) {
          //			if (mMapController != null) {
          //				mMapController.animateTo(myLocation);
          //			}\
          return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {}

        @Override
        public void onLocationUnavailableArea(
            NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {}
      };

  public void showLayout(int id) {
    if (id == 0) {
      return;
    }
    frameLayout.setVisibility(View.VISIBLE);
    cardView.setVisibility(View.VISIBLE);
    String imgurl;
    mTagLists.clear();
    AppDatabase db = AppDatabase.getInstance(Objects.requireNonNull(getContext()));
    AppExecutors executors = new AppExecutors();
    executors
        .diskIO()
        .execute(
            () -> {
              List<Location> locations = db.locationDao().loadAll();
              List<LocationTag> locationTags =
                  db.locationTagDao().loadByLocationId(locations.get(id).getUuid());
              List<Tag> tags = new ArrayList<>();
              for (int i = 0; i < locationTags.size(); i++) {
                tags.add(db.tagDao().loadById(locationTags.get(i).getTagId()));
              }
              executors
                  .mainThread()
                  .execute(
                      () -> {
                        StringBuilder tag_name = new StringBuilder();
                        for (Tag tag : tags) {
                          tag_name.append("#").append(tag.getName()).append(" ");
                          mTagLists.add(tag.getName());
                        }
                        this.title.setText(locations.get(id).getName());
                        //this.tag.setText(tag_name.toString());
                        Glide.with(getContext())
                            .load(locations.get(id).getImage())
                            .apply(RequestOptions.centerCropTransform())
                            .into(image);

                        location_id = locations.get(id).getUuid();
                        setContent();
                      });
            });

  }
    private void setContent(){
        String tag = "";
        int i;
        for(i = 0 ; i <mTagLists.size(); i++){
            tag += "#" + mTagLists.get(i) + " ";
        }
        Log.e("result.info",tag);
        ArrayList<int[]> hashtagSpans = getSpans(tag, '#');
        SpannableString tagsContent = new SpannableString(tag);
        for(i = 0; i < hashtagSpans.size(); i++){
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];
            Hashtag hashTag = new Hashtag(this.getContext());
            hashTag.setOnClickEventListener(new Hashtag.ClickEventListener() {
                @Override public void onClickEvent(String data) {
                    Log.e("result.info",data);
                    Intent intent = new Intent(getView().getContext(), SearchActivity.class);
                    intent.putExtra(EXTRA_SEARCH_KEYWORD, data);
                    startActivity(intent);
                }
            });
            tagsContent.setSpan(hashTag, hashTagStart, hashTagEnd, 0); }
        TextView tags_view = (TextView)getView().findViewById(R.id.tag);
        if(tags_view != null) {
            tags_view.setMovementMethod(LinkMovementMethod.getInstance());
            tags_view.setText(tagsContent);
        }
    }
    public ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();
        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body); // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }
        return spans;
    }
}
