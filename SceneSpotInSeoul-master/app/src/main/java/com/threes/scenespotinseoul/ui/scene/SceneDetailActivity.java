package com.threes.scenespotinseoul.ui.scene;

import static com.threes.scenespotinseoul.ui.scene.PictureActivity.FLAG_PARENT_DETAIL;
import static com.threes.scenespotinseoul.ui.scene.PictureActivity.FLAG_SCENE_IMAGE;
import static com.threes.scenespotinseoul.ui.scene.PictureActivity.FLAG_USER_IMAGE;
import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.threes.scenespotinseoul.utilities.AppExecutorsHelperKt.runOnMain;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_IMAGE_FLAGS;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_LOCATION_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SCENE_ID;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SEARCH_KEYWORD;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.SpannableString;
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
import com.threes.scenespotinseoul.data.model.Scene;
import com.threes.scenespotinseoul.data.model.SceneTag;
import com.threes.scenespotinseoul.data.model.Tag;
import com.threes.scenespotinseoul.ui.map.Hashtag;
import com.threes.scenespotinseoul.ui.search.SearchActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SceneDetailActivity extends AppCompatActivity {

  private static final int GALLERY_CODE = 1111;
  private static final int CAMERACODE = 531;
  private static final String[] PERMISSIONS =
      new String[]{
          Manifest.permission.CAMERA,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.ACCESS_FINE_LOCATION
      };
  //map에 전달한 location id 값
  private String idfromScenetoMap;
  private ImageView mediaM;
  private ImageView pic;
  private TextView SceneName;
  private TextView TagName;
  private TextView guid;
  private Uri photoUri;
  private FloatingActionButton fab;
  private ActionBar mActionBar;
  private String mSceneId;

  private ArrayList<String> mTagLists;

  public void onCreate(Bundle savedInstanceStat) {
    super.onCreate(savedInstanceStat);
    setContentView(R.layout.activity_scene_detail);
    mediaM = findViewById(R.id.media_image);
    pic = findViewById(R.id.picture);
    SceneName = findViewById(R.id.Scenename);
    TagName = findViewById(R.id.tagView);
    guid = findViewById(R.id.guide);
    fab = findViewById(R.id.fab);

    ImageButton btnNavigateUp = findViewById(R.id.btn_navigate_up);
    btnNavigateUp.setOnClickListener(view -> finish());

    mediaM.setOnClickListener(view -> {
      Bundle options = ActivityOptionsCompat
          .makeSceneTransitionAnimation(this,
              mediaM, "transitionImage"
          ).toBundle();
      Intent picintent = new Intent(this, PictureActivity.class);
      picintent.putExtra(EXTRA_SCENE_ID, mSceneId);
      picintent.putExtra(EXTRA_IMAGE_FLAGS, FLAG_SCENE_IMAGE | FLAG_PARENT_DETAIL);
      startActivity(picintent, options);
    });

    mTagLists = new ArrayList<String>();

    Intent intent = getIntent();
    if (intent != null && intent.hasExtra(EXTRA_SCENE_ID)) {
      mSceneId = intent.getStringExtra(EXTRA_SCENE_ID);

      if (mSceneId == null) {
        Log.e("DetailActivity", "Can't receive scene id");
        finish();
      }
      AppDatabase db = AppDatabase.getInstance(this);
      db.sceneDao()
          .loadByIdWithLive(mSceneId)
          .observe(
              this,
              scene -> {
                if (scene != null) {
                  mTagLists.clear();
                  getTags(db, scene);

                  SceneName.setText(scene.getDesc());
                  idfromScenetoMap = scene.getLocationId();
                  Glide.with(this)
                      .asBitmap()
                      .load(Uri.parse(scene.getImage()))
                      .apply(new RequestOptions().centerCrop())
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
                                    ContextCompat.getColor(SceneDetailActivity.this,
                                        R.color.colorPrimaryDark)));
                              }
                            });
                          }
                          return false;
                        }
                      })
                      .into(mediaM);

                  guid.setVisibility(View.VISIBLE);

                  if (scene.isUploaded()) {
                    guid.setVisibility(View.GONE);
                    Glide.with(this)
                        .load(Uri.parse(scene.getUploadedImage()))
                        .apply(new RequestOptions())
                        .into(pic);
                  }
                }
              });
    }

    fab.setOnClickListener(
        v -> {
          Intent mapIntent = new Intent(this, DetailToMapActivity.class);
          mapIntent.putExtra(EXTRA_LOCATION_ID, idfromScenetoMap);
          startActivity(mapIntent);
        });

    pic.setOnClickListener(
        v -> {
          AppDatabase db = AppDatabase.getInstance(this);
          CharSequence info[] = new CharSequence[]{"사진을 찍어 업로드", "갤러리에서 업로드", "갤러리에서 보기",
              "업로드된 사진 내리기", "취소"};
          AlertDialog.Builder builder = new AlertDialog.Builder(SceneDetailActivity.this);
          builder.setTitle("사진 업로드");
          builder.setItems(
              info,
              (dialog, which) -> {
                int permissionCheck1;
                int permissionCheck2;
                int permissionCheck3;
                if (which == 0) {
                  Log.v("선택한 것", "사진을 찍어 업로드" + which);
                  permissionCheck1 =
                      ContextCompat.checkSelfPermission(SceneDetailActivity.this, PERMISSIONS[0]);
                  permissionCheck2 =
                      ContextCompat.checkSelfPermission(SceneDetailActivity.this, PERMISSIONS[1]);
                  permissionCheck3 =
                      ContextCompat.checkSelfPermission(SceneDetailActivity.this, PERMISSIONS[2]);
                  if (permissionCheck1 == PackageManager.PERMISSION_DENIED
                      && permissionCheck2 == PackageManager.PERMISSION_DENIED
                      && permissionCheck3 == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(v, "권한이 없어서 기능을 이용할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                  } else {
                    selectPhoto();
                  }
                } else if (which == 1) {
                  Log.v("선택한 것", "갤러리에서 업로드" + which);
                  permissionCheck3 =
                      ContextCompat.checkSelfPermission(SceneDetailActivity.this, PERMISSIONS[2]);
                  if (permissionCheck3 == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(v, "권한이 없어서 기능을 이용할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                  } else {
                    selectGallery();
                  }
                } else if (which == 2) {
                  Log.v("선택한 것", "갤러리에서 보기" + which);
                  runOnDiskIO(
                      () -> {
                        Scene scene = db.sceneDao().loadById(mSceneId);
                        if (scene.getUploadedImage() == null) {
                          Snackbar.make(v, "사진이 없습니다.", Snackbar.LENGTH_SHORT).show();
                        } else {
                          Log.v("전달한 id", mSceneId + "");
                          Intent picintent = new Intent(this, PictureActivity.class);
                          picintent.putExtra(EXTRA_SCENE_ID, mSceneId);
                          picintent
                              .putExtra(EXTRA_IMAGE_FLAGS, FLAG_USER_IMAGE | FLAG_PARENT_DETAIL);
                          startActivity(picintent);
                        }
                      });
                } else if (which == 3) {
                  Log.v("선택한 것", "사진 내리기" + which);
                  runOnDiskIO(
                      () -> {
                        Scene scene = db.sceneDao().loadById(mSceneId);
                        if (scene.getUploadedImage() == null) {
                          Snackbar.make(v, "이전에 올린 사진이 없습니다.", Snackbar.LENGTH_SHORT).show();
                        } else {
                          scene.setUploaded(false);
                          scene.setUploadedImage(null);
                          db.sceneDao().update(scene);
                        }
                      });
                  pic.setImageResource(0);
                  guid.setVisibility(View.VISIBLE);
                } else if (which == 4) {
                  Log.v("선택한 것", "취소" + which);
                  dialog.dismiss();
                }
                dialog.dismiss();
              });
          builder.show();
        });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAMERACODE && resultCode == RESULT_OK) {
      getPictureForPhoto();
    } else if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
      sendPicture(data.getData());
    }
  }


  private void setContent() {
    String tag = "";

    for (int i = 0; i < mTagLists.size(); i++) {
      tag = tag + "#" + mTagLists.get(i) + "";
    }

    ArrayList<int[]> hashtageSpans = getSpans(tag, '#');
    SpannableString tagsContent = new SpannableString(tag);
    for (int i = 0; i < hashtageSpans.size(); i++) {
      int[] span = hashtageSpans.get(i);
      int hashTagStart = span[0];
      int hashTagEnd = span[1];
      Hashtag hashtag = new Hashtag(this);
      hashtag.setOnClickEventListener(new Hashtag.ClickEventListener() {
        @Override
        public void onClickEvent(String data) {
          Intent intent = new Intent(SceneDetailActivity.this, SearchActivity.class);
          intent.putExtra(EXTRA_SEARCH_KEYWORD, data);
          startActivity(intent);
        }
      });
      tagsContent.setSpan(hashtag, hashTagStart, hashTagEnd, 0);
      TagName.setMovementMethod(LinkMovementMethod.getInstance());
      TagName.setText(tagsContent);
    }

  }

  private ArrayList<int[]> getSpans(String body, char prefix) {
    ArrayList<int[]> spans = new ArrayList<int[]>();
    Pattern pattern = Pattern.compile(prefix + "\\w+");
    Matcher matcher = pattern.matcher(body);
    while (matcher.find()) {
      int[] currentSpan = new int[2];
      currentSpan[0] = matcher.start();
      currentSpan[1] = matcher.end();
      spans.add(currentSpan);
    }
    return spans;
  }

  private void getTags(AppDatabase db, Scene scene) {
    runOnDiskIO(
        () -> {
          List<SceneTag> st = db.sceneTagDao().loadBySceneId(scene.getUuid());
          List<Tag> tags = new ArrayList<>();
          for (int i = 0; i < st.size(); i++) {
            tags.add(db.tagDao().loadById(st.get(i).getTagId()));

          }
          runOnMain(
              () -> {
                //String textTag = null;
                StringBuilder tag_name = new StringBuilder();
                for (Tag tag : tags) {
                  tag_name.append("#").append(tag.getName()).append(" ");
                  mTagLists.add(tag.getName());
                }
//                for (int i = 0; i < tags.size(); i++) {
//                  textTag = "#" + tags.get(i).getName();
//                  mTagLists.add(tag.getName());
//                }
                TagName.setText(tag_name);
                setContent();
              });
        });
  }

  private void selectPhoto() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
      if (intent.resolveActivity(getPackageManager()) != null) {
        File photoFile;
        photoFile = createImageFile();
        if (photoFile != null) {
          photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
          intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
          startActivityForResult(intent, CAMERACODE);
        }
      }
    }
  }

  private File createImageFile() {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageName = timeStamp;
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = new File(storageDir + "/" + imageName + ".jpg");
    String currentPhotoPath = image.getAbsolutePath();
    return image;
  }

  private void getPictureForPhoto() {
    AppDatabase db = AppDatabase.getInstance(this);
    runOnDiskIO(
        () -> {
          Scene scene = db.sceneDao().loadById(mSceneId);
          if (scene != null) {
            scene.setUploaded(true);
            scene.setUploadedImage(photoUri.toString());
            scene.setUploadedDate(System.currentTimeMillis());
            db.sceneDao().update(scene);
          }
        });
  }

  private void selectGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    intent.setType("image/*");
    startActivityForResult(intent, GALLERY_CODE);
  }

  private void sendPicture(Uri imgUri) {
    AppDatabase db = AppDatabase.getInstance(this);
    runOnDiskIO(
        () -> {
          Scene scene = db.sceneDao().loadById(mSceneId);
          if (scene != null) {
            scene.setUploaded(true);
            scene.setUploadedImage(imgUri.toString());
            scene.setUploadedDate(System.currentTimeMillis());
            db.sceneDao().update(scene);
          }
        });
  }

//
//  private String getRealPathFromURI(Uri contentUri) {
//    int column_index = 0;
//    String[] proj = {MediaStore.Images.Media.DATA};
//    Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//
//    if (cursor.moveToFirst()) {
//
//      column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//    }
//    return cursor.getString(column_index);
//  }
}


