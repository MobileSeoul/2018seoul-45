package com.threes.scenespotinseoul.ui.media;

import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_SCENE_ID;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.model.Scene;
import com.threes.scenespotinseoul.ui.media.MediaSceneAdapter.SceneViewHolder;
import com.threes.scenespotinseoul.ui.scene.SceneDetailActivity;
import java.util.List;

public class MediaSceneAdapter extends RecyclerView.Adapter<SceneViewHolder> {

  private List<Scene> scene_relation_L;

  MediaSceneAdapter(List<Scene> scene_relation) {
    this.scene_relation_L = scene_relation;
  }

  @NonNull
  @Override
  public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_content, parent, false);
    return new SceneViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
    Context context = holder.itemView.getContext();

    RequestOptions requestOptions = new RequestOptions().centerCrop();

    Scene curScene = scene_relation_L.get(position);

    Glide.with(context)
        .load(curScene.getImage())
        .apply(requestOptions)
        .into(holder.ivImage);

    holder.tvName.setText(curScene.getDesc());

    holder.itemView.setOnClickListener(
        v -> {
          // 명장면 상세 액티비티로 장면 아이디 값 넘김
          Intent intent = new Intent(context, SceneDetailActivity.class);
          intent.putExtra(EXTRA_SCENE_ID, scene_relation_L.get(position).getUuid());
          context.startActivity(intent);
        });
  }

  @Override
  public int getItemCount() {
    if (scene_relation_L != null) return scene_relation_L.size();
    else return 0;
  }

  class SceneViewHolder extends RecyclerView.ViewHolder {
    ImageView ivImage;
    TextView tvName;

    private SceneViewHolder(View itemView) {
      super(itemView);
      ivImage = itemView.findViewById(R.id.iv_image);
      tvName = itemView.findViewById(R.id.tv_name);
    }
  }
}
