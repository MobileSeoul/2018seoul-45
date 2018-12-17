package com.threes.scenespotinseoul.ui.scene;

import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnMain;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_LOCATION_ID;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.AppDatabase;
import com.threes.scenespotinseoul.data.model.Location;
import com.threes.scenespotinseoul.ui.map.NMapPOIflagType;
import com.threes.scenespotinseoul.ui.map.NMapViewerResourceProvider;
import com.threes.scenespotinseoul.utilities.Utils;
import java.util.Objects;

/**
 * Created by Chun on 2018-08-16.
 */
public class DetailToMapActivity extends NMapActivity implements AppCompatCallback {

  private final String CLIENT_ID = "bu0cuRVRS2yONcn8FGxq";

  private NMapView mapView; // 지도 화면 View
  private NMapPOIdata poiData;
  private NMapOverlayManager nMapOverlayManager;
  private NMapViewerResourceProvider mMapViewerResourceProvider;
  private NMapContext mMapContext;
  private NMapController mapController;

  private AppCompatDelegate mAppCompatDelegate;

  private String location_id;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAppCompatDelegate = AppCompatDelegate.create(this, this);
    mAppCompatDelegate.onCreate(savedInstanceState);
    mAppCompatDelegate.setContentView(R.layout.activity_detail_tomap);

    ActionBar supportActionBar = mAppCompatDelegate.getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
      supportActionBar.setHomeButtonEnabled(true);
    }

    location_id = getIntent().getStringExtra(EXTRA_LOCATION_ID);

    mapView = findViewById(R.id.mapView);
    mapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
    mapView.setClickable(true);
    mapView.setEnabled(true);

    mapController = mapView.getMapController();

    mMapViewerResourceProvider = new NMapViewerResourceProvider(this, this::showLayout);
    nMapOverlayManager =
        new NMapOverlayManager(
            Objects.requireNonNull(this),
            mapView,
            mMapViewerResourceProvider); // null 자리에 mMapViewerResourceProiveder 로 오버레이 아이템들을 내맘대로
    // 꾸밀 수 있다.

    //nMapLocationManager = new NMapLocationManager(this);

    mapView.setOnMapViewTouchEventListener(new NMapView.OnMapViewTouchEventListener() {
      @Override
      public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

      }

      @Override
      public void onLongPressCanceled(NMapView nMapView) {

      }

      @Override
      public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

      }

      @Override
      public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

      }

      @Override
      public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

      }

      @Override
      public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

      }
    });

    AppDatabase db = AppDatabase.getInstance(this);
    runOnDiskIO(() -> {
      Location location = db.locationDao().loadById(location_id);
      runOnMain(() -> {
        if (location != null) {
          double lon = location.getLon();
          double lat = location.getLat();
          String name = location.getName();

          mAppCompatDelegate.setTitle(name);

          poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
          poiData.beginPOIdata(1);

          poiData.addPOIitem(
              lon,
              lat,
              name,
              NMapPOIflagType.PIN,
              0);

          poiData.endPOIdata();
          nMapOverlayManager.createPOIdataOverlay(poiData, null);
          mapController.setMapCenter(lon, lat);
          mapController.setZoomLevel(13);
        }
      });
    });
  }

  private void showLayout(int i) {
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_detail_to_map, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    } else if (item.getItemId() == R.id.action_launch) {
      launchMap();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSupportActionModeStarted(ActionMode actionMode) {
    // No-op
  }

  @Override
  public void onSupportActionModeFinished(ActionMode actionMode) {
    // No-op
  }

  @Nullable
  @Override
  public ActionMode onWindowStartingSupportActionMode(Callback callback) {
    return null;
  }

  private void launchMap() {
    runOnDiskIO(() -> {
      Location location = AppDatabase.getInstance(this).locationDao().loadById(location_id);
      runOnMain(() -> {
        if (location != null) {
          Utils.launchExternalMap(this, location);
        }
      });
    });
  }
}
