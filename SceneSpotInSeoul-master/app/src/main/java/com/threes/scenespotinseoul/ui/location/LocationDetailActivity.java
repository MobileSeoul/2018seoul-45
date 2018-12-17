package com.threes.scenespotinseoul.ui.location;

import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnMain;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_LOCATION_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SEARCH_KEYWORD;
import static com.threes.scenespotinseoul.utilities.ItemOffsetDecorationKt.DIR_RIGHT;
import static com.threes.scenespotinseoul.utilities.ItemOffsetDecorationKt.OFFSET_NORMAL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.AppDatabase;
import com.threes.scenespotinseoul.data.model.Location;
import com.threes.scenespotinseoul.data.model.LocationTag;
import com.threes.scenespotinseoul.data.model.Media;
import com.threes.scenespotinseoul.data.model.Scene;
import com.threes.scenespotinseoul.data.model.Tag;
import com.threes.scenespotinseoul.ui.map.Hashtag;
import com.threes.scenespotinseoul.ui.scene.PictureActivity;
import com.threes.scenespotinseoul.ui.search.SearchActivity;
import com.threes.scenespotinseoul.utilities.ItemOffsetDecoration;
import com.threes.scenespotinseoul.utilities.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationDetailActivity extends AppCompatActivity {

  private String location_id;

  private ImageView mLocation_image;
  private TextView mLocation_hash_tag;
  private TextView mLocation_name;
  private TextView mLocation_address;
  private TextView mLocation_detail;
  private TextView mLocation_simpleText;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);

    mLocation_image = findViewById(R.id.iv_hero);
    mLocation_hash_tag = findViewById(R.id.tv_tag);
    mLocation_name = findViewById(R.id.tv_title);
    mLocation_address = findViewById(R.id.tv_caption);
    mLocation_detail = findViewById(R.id.tv_description);
    mLocation_simpleText = findViewById(R.id.tv_simple);

    mLocation_address.setVisibility(View.VISIBLE);

    TextView tvCategory1 = findViewById(R.id.tv_category1);
    TextView tvCategory2 = findViewById(R.id.tv_category2);
    ImageButton btnNavigateUp = findViewById(R.id.btn_navigate_up);
    ImageButton btnLaunchMap = findViewById(R.id.btn_launch);
    RecyclerView recyclerView_scene = findViewById(R.id.rv_category1);
    RecyclerView recyclerView_media = findViewById(R.id.rv_category2);

    tvCategory1.setText(R.string.category_location_scenes);
    tvCategory2.setText(R.string.category_location_media);

    btnNavigateUp.setOnClickListener(view -> finish());

    btnLaunchMap.setVisibility(View.VISIBLE);
    btnLaunchMap.setOnClickListener(view -> runOnDiskIO(() -> {
      Location location = AppDatabase.getInstance(this).locationDao().loadById(location_id);
      runOnMain(() -> {
        if (location != null) {
          Utils.launchExternalMap(this, location);
        }
      });
    }));

    // 해당 미디어 명장면 리사이클러뷰 처리
    recyclerView_scene.addItemDecoration(new ItemOffsetDecoration(DIR_RIGHT, OFFSET_NORMAL));
    recyclerView_media.addItemDecoration(new ItemOffsetDecoration(DIR_RIGHT, OFFSET_NORMAL));

    mLocation_simpleText.setVisibility(View.GONE);

    mLocation_image.setOnClickListener(view -> {
      Bundle options = ActivityOptionsCompat
          .makeSceneTransitionAnimation(this,
              mLocation_image, "transitionImage"
          ).toBundle();
      Intent intent = new Intent(this, PictureActivity.class);
      intent.putExtra(EXTRA_LOCATION_ID, location_id);
      startActivity(intent, options);
    });

    //TEST를 위한 주석처리
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra(EXTRA_LOCATION_ID)) {
      location_id = intent.getStringExtra(EXTRA_LOCATION_ID);
    }

    // 해시태그 헬퍼 시도해봄
    //        mHashTagHelper =
    // HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), new
    // HashTagHelper.OnHashTagClickListener() {
    //            @Override
    //            public void onHashTagClicked(String hashTag) {
    //                Toast.makeText(getApplicationContext(), hashTag, Toast.LENGTH_SHORT).show();
    //            }
    //        });

    // 미디어 데이터 가져옴 Executor 사용
    AppDatabase db = AppDatabase.getInstance(this);
    runOnDiskIO(
        () -> {
          // List<Media> media = db.mediaDao().loadAll();
          Location mLocation = db.locationDao().loadById(location_id);
          List<Scene> scenes = db.sceneDao().loadByLocationId(location_id);
          List<LocationTag> locationTags = db.locationTagDao()
              .loadByLocationId(mLocation.getUuid());
          HashSet<Media> medias = new HashSet<>();
          for (Scene _scene : scenes) {
            medias.add(db.mediaDao().loadById(_scene.getMediaId()));
          }

          List<Tag> tags = new ArrayList<>();
          for (int i = 0; i < locationTags.size(); i++) {
            tags.add(db.tagDao().loadById(locationTags.get(i).getTagId()));
            Log.e("태그", tags.get(i).getName());
          }

          runOnMain(
              () -> {
                LocationSceneAdapter adapter_scene = new LocationSceneAdapter(scenes);
                recyclerView_scene.setAdapter(adapter_scene);
                recyclerView_scene.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                recyclerView_scene.setHasFixedSize(true);
                LocationMediaAdapter adapter_media = new LocationMediaAdapter(medias);
                recyclerView_media.setAdapter(adapter_media);
                recyclerView_media.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                recyclerView_media.setHasFixedSize(true);

                //LocationMediaAdapter

                // 미디어 대표 이미지 세팅
                RequestOptions requestOptions =
                    new RequestOptions().placeholder(android.R.color.darker_gray).centerCrop();

                Glide.with(this)
                    .asBitmap()
                    .load(mLocation.getImage())
                    .apply(requestOptions)
                    .listener(new RequestListener<Bitmap>() {
                      @Override
                      public boolean onLoadFailed(@Nullable GlideException e, Object model,
                          Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                      }

                      @Override
                      public boolean onResourceReady(Bitmap resource, Object model,
                          Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                          Palette.from(resource).generate(palette -> {
                            if (palette != null) {
                              getWindow().setStatusBarColor(palette.getDarkVibrantColor(
                                  ContextCompat.getColor(LocationDetailActivity.this,
                                      R.color.colorPrimaryDark)));
                            }
                          });
                        }
                        return false;
                      }
                    })
                    .into(mLocation_image);

                // 미디어 타이틀 세팅
                mLocation_name.setText(mLocation.getName());

                // 미디어 타이틀 세팅
                mLocation_name.setText(mLocation.getName());

                mLocation_address.setText(mLocation.getAddress());
                // 미디어 상세설명 세팅
                mLocation_detail.setText(mLocation.getDesc());

                if (mLocation_detail.getLineCount() > 2) {
                  int tLineCount = mLocation_detail.getLineCount();
                  mLocation_detail.setMaxLines(2);
                  mLocation_detail.setEllipsize(TextUtils.TruncateAt.END);

                  mLocation_detail.setOnClickListener(
                      v -> {
                        mLocation_detail.setMaxLines(tLineCount);
                        mLocation_simpleText.setVisibility(View.VISIBLE);
                      });

                  // 미디어 간략히보기 이벤트
                  mLocation_simpleText.setOnClickListener(
                      v -> {
                        mLocation_detail.setMaxLines(2);
                        mLocation_simpleText.setVisibility(View.GONE);
                      });
                }

                // 미디어 해시티그 세팅
                StringBuilder mTag = new StringBuilder();
                for (Tag tag : tags) {
                  mTag.append("#").append(tag.getName()).append(" ");
                }
                ArrayList<int[]> hashtagSpans = getSpans(mTag.toString(), '#');
                SpannableString tagsContent = new SpannableString(mTag.toString());
                int i;
                for (i = 0; i < hashtagSpans.size(); i++) {
                  int[] span = hashtagSpans.get(i);
                  int hashTagStart = span[0];
                  int hashTagEnd = span[1];
                  Hashtag jhashTag = new Hashtag(this);
                  jhashTag.setOnClickEventListener(data -> {
                    Log.e("result.info", data);
                    Intent intent1 = new Intent(LocationDetailActivity.this, SearchActivity.class);
                    intent1.putExtra(EXTRA_SEARCH_KEYWORD, data);
                    startActivity(intent1);
                  });
                  tagsContent.setSpan(jhashTag, hashTagStart, hashTagEnd, 0);
                }
                if (mLocation_hash_tag != null) {
                  mLocation_hash_tag.setMovementMethod(LinkMovementMethod.getInstance());
                  mLocation_hash_tag.setText(tagsContent);
                }
              });
        });
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
