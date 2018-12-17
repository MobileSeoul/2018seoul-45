package com.threes.scenespotinseoul.ui.scene;

import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnMain;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_IMAGE_FLAGS;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_LOCATION_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_MEDIA_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SCENE_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.LOCATION_TABLE;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.MEDIA_TABLE;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.SCENE_TABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.AppDatabase;
import com.threes.scenespotinseoul.data.model.Location;
import com.threes.scenespotinseoul.data.model.Media;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureActivity extends AppCompatActivity {

  public static final int FLAG_PARENT_DETAIL = 1;
  public static final int FLAG_SCENE_IMAGE = 2;
  public static final int FLAG_USER_IMAGE = 4;

  private AppDatabase mDb;

  private PhotoView mPicture;
  private TextView mTvName;
  private TextView mTvDate;
  private ImageButton mBtnNavigateUp;
  private ImageButton mBtnToScene;
  private View mShadowView;
  private ConstraintLayout mContainer;

  private boolean isImmersive;
  private boolean isFromParent;
  private boolean isShowDate;
  private String mItemType;
  private String mItemId;
  private int mImageFlags;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_picture);
    Intent intent = getIntent();
    if (intent != null) {
      mDb = AppDatabase.getInstance(this);
      if (intent.hasExtra(EXTRA_LOCATION_ID)) {
        mItemType = LOCATION_TABLE;
        mItemId = intent.getStringExtra(EXTRA_LOCATION_ID);
        isFromParent = true;
      } else if (intent.hasExtra(EXTRA_MEDIA_ID)) {
        mItemType = MEDIA_TABLE;
        mItemId = intent.getStringExtra(EXTRA_MEDIA_ID);
        isFromParent = true;
      } else if (intent.hasExtra(EXTRA_SCENE_ID) && intent.hasExtra(EXTRA_IMAGE_FLAGS)) {
        mItemType = SCENE_TABLE;
        mItemId = intent.getStringExtra(EXTRA_SCENE_ID);
        mImageFlags = intent.getIntExtra(EXTRA_IMAGE_FLAGS, 0);
        isFromParent = (mImageFlags & FLAG_PARENT_DETAIL) == FLAG_PARENT_DETAIL;
        isShowDate = (mImageFlags & FLAG_USER_IMAGE) == FLAG_USER_IMAGE;
      } else {
        finish();
      }
    } else {
      finish();
    }
    initViews();
    loadData();
  }

  private void initViews() {
    mPicture = findViewById(R.id.into_picture);
    mTvName = findViewById(R.id.tv_name);
    mTvDate = findViewById(R.id.tv_date);
    mContainer = findViewById(R.id.container);
    mShadowView = findViewById(R.id.shadow);

    mBtnNavigateUp = findViewById(R.id.btn_navigate_up);
    mBtnNavigateUp.setOnClickListener(view -> onBackPressed());

    mBtnToScene = findViewById(R.id.btn_to_scene);
    if (!isFromParent) {
      mBtnToScene.setVisibility(View.VISIBLE);
      mBtnToScene.setOnClickListener(view -> {
        Intent intent = new Intent(this, SceneDetailActivity.class);
        intent.putExtra(EXTRA_SCENE_ID, mItemId);
        startActivity(intent);
      });
    }

    if (!isShowDate) {
      mTvDate.setVisibility(View.GONE);
    }

    mPicture.setOnClickListener(view -> {
      if (isImmersive) {
        showSystemUI();
        showUI();
      } else {
        hideSystemUI();
        hideUI();
      }
      isImmersive = !isImmersive;
    });
    showSystemUI();
    supportStartPostponedEnterTransition();
  }

  private void loadData() {
    switch (mItemType) {
      case MEDIA_TABLE:
        runOnDiskIO(() -> {
          Media media = mDb.mediaDao().loadById(mItemId);
          runOnMain(() -> {
            if (media != null) {
              loadImage(media.getImage());
              mTvName.setText(media.getName());
            }
          });
        });
        break;
      case LOCATION_TABLE:
        runOnDiskIO(() -> {
          Location location = mDb.locationDao().loadById(mItemId);
          runOnMain(() -> {
            if (location != null) {
              mTvName.setText(location.getName());
              loadImage(location.getImage());
            }
          });
        });
        break;
      case SCENE_TABLE:
        mDb.sceneDao().loadByIdWithLive(mItemId)
            .observe(this, scene -> {
              if (scene != null) {
                mTvName.setText(scene.getDesc());
                if ((mImageFlags & FLAG_USER_IMAGE) == FLAG_USER_IMAGE) {
                  mTvDate.setText(formatDate(scene.getUploadedDate()));
                  String uploadedImage = scene.getUploadedImage();
                  if (uploadedImage != null) {
                    loadImage(uploadedImage);
                  } else {
                    onBackPressed();
                  }
                } else if ((mImageFlags & FLAG_SCENE_IMAGE) == FLAG_SCENE_IMAGE) {
                  loadImage(scene.getImage());
                }
              }
            });
        break;
    }
  }

  private void loadImage(String url) {
    RequestOptions options = new RequestOptions().optionalFitCenter();
    Glide.with(this)
        .load(Uri.parse(url))
        .apply(options)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Drawable> target, boolean isFirstResource) {
            supportStartPostponedEnterTransition();
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model,
              Target<Drawable> target, DataSource dataSource,
              boolean isFirstResource) {
            supportStartPostponedEnterTransition();
            return false;
          }
        })
        .into(mPicture);
  }

  @SuppressLint("SimpleDateFormat")
  private String formatDate(long timestamp) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd.");
    return dateFormat.format(new Date(timestamp));
  }

  private void showUI() {
    TransitionManager.beginDelayedTransition(mContainer);
    ConstraintSet constraintSet = new ConstraintSet();
    constraintSet.clone(mContainer);
    constraintSet.setVisibility(mShadowView.getId(), View.VISIBLE);
    constraintSet.setVisibility(mBtnNavigateUp.getId(), View.VISIBLE);
    constraintSet.setVisibility(mBtnToScene.getId(), !isFromParent ? View.VISIBLE : View.GONE);
    constraintSet.setVisibility(mTvName.getId(), View.VISIBLE);
    constraintSet.setVisibility(mTvDate.getId(), isShowDate ? View.VISIBLE : View.GONE);
    constraintSet.applyTo(mContainer);
  }

  private void hideUI() {
    TransitionManager.beginDelayedTransition(mContainer);
    ConstraintSet constraintSet = new ConstraintSet();
    constraintSet.clone(mContainer);
    constraintSet.setVisibility(mShadowView.getId(), View.GONE);
    constraintSet.setVisibility(mBtnNavigateUp.getId(), View.GONE);
    constraintSet.setVisibility(mBtnToScene.getId(), View.GONE);
    constraintSet.setVisibility(mTvName.getId(), View.GONE);
    constraintSet.setVisibility(mTvDate.getId(), View.GONE);
    constraintSet.applyTo(mContainer);
  }

  private void showSystemUI() {
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  private void hideSystemUI() {
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  }
}
