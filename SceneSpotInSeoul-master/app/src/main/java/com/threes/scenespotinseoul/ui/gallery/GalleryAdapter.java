package com.threes.scenespotinseoul.ui.gallery;

import static com.threes.scenespotinseoul.ui.scene.PictureActivity.FLAG_USER_IMAGE;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_IMAGE_FLAGS;
import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SCENE_ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.model.Scene;
import com.threes.scenespotinseoul.ui.scene.PictureActivity;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

  private Activity mActivity;
  private List<Scene> captured;

  GalleryAdapter(Activity activity, List<Scene> captured) {
    this.mActivity = activity;
    this.captured = captured;
  }

  @NonNull
  public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_gallery_recyclerview, parent, false);
    return new GalleryViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
    Context context = holder.itemView.getContext();
    Log.v("캡쳐된 이미지", captured.get(position).getUploadedImage());
    RequestOptions requestOptions = new RequestOptions().optionalCenterCrop();
    if (captured != null) {
      Glide.with(context)
          .load(Uri.parse(captured.get(position).getUploadedImage()))
          .apply(requestOptions)
          .into(holder.image);
    }

    holder.itemView.setOnClickListener(
        v -> {
          Bundle options = ActivityOptionsCompat
              .makeSceneTransitionAnimation(mActivity, holder.image, "transitionImage").toBundle();
          Intent intent = new Intent(context, PictureActivity.class);
          intent.putExtra(EXTRA_SCENE_ID, captured.get(position).getUuid());
          intent.putExtra(EXTRA_IMAGE_FLAGS, FLAG_USER_IMAGE);
          context.startActivity(intent, options);
        });
  }

  @Override
  public int getItemCount() {
    if (captured != null) {
      return captured.size();
    } else {
      return 0;
    }
  }

  public class GalleryViewHolder extends RecyclerView.ViewHolder {

    ImageView image;

    public GalleryViewHolder(View itemView) {
      super(itemView);
      image = itemView.findViewById(R.id.gallery_recycler_image);
    }
  }
}
