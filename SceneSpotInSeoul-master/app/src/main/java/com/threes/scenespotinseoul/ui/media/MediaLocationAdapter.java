package com.threes.scenespotinseoul.ui.media;

import static com.threes.scenespotinseoul.utilities.ConstantsKt.EXTRA_LOCATION_ID;

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
import com.threes.scenespotinseoul.data.model.Location;
import com.threes.scenespotinseoul.ui.location.LocationDetailActivity;
import com.threes.scenespotinseoul.ui.media.MediaLocationAdapter.LocationViewHolder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MediaLocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

  private List<Location> location_relation_L;
  private Iterator<Location> location_relation_I;
  private HashSet<Location> location_relation_H;

  MediaLocationAdapter(HashSet<Location> location_relation) {
    this.location_relation_H = location_relation;
    this.location_relation_I = this.location_relation_H.iterator();
    this.location_relation_L = new ArrayList<>();
    while(location_relation_I.hasNext()){
      this.location_relation_L.add(location_relation_I.next());
    }
  }

  @NonNull
  @Override
  public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_content, parent, false);
    return new LocationViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
    Context context = holder.itemView.getContext();

    RequestOptions requestOptions = new RequestOptions().centerCrop();

    Location curLocation = location_relation_L.get(position);

    Glide.with(context)
        .load(curLocation.getImage())
        .apply(requestOptions)
        .into(holder.ivImage);

    holder.tvName.setText(curLocation.getName());

    holder.itemView.setOnClickListener(
        v -> {
          // 명장면 상세 액티비티로 장면 아이디 값 넘김
          Intent intent = new Intent(context, LocationDetailActivity.class);
          intent.putExtra(EXTRA_LOCATION_ID, location_relation_L.get(position).getUuid());
          context.startActivity(intent);
        });
  }

  @Override
  public int getItemCount() {
    if (location_relation_L != null) return location_relation_L.size();
    else return 0;
  }

  class LocationViewHolder extends RecyclerView.ViewHolder {
    ImageView ivImage;
    TextView tvName;

    private LocationViewHolder(View itemView) {
      super(itemView);
      ivImage = itemView.findViewById(R.id.iv_image);
      tvName = itemView.findViewById(R.id.tv_name);
    }
  }
}
