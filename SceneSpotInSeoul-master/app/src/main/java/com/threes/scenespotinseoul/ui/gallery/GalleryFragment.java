package com.threes.scenespotinseoul.ui.gallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.threes.scenespotinseoul.R;
import com.threes.scenespotinseoul.data.AppDatabase;
import java.util.Objects;

public class GalleryFragment extends Fragment {

  TextView mTvNoPictures;
  RecyclerView recyclerView;
  GalleryAdapter adapter;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_gallery, container, false);
    mTvNoPictures = view.findViewById(R.id.tv_no_pictures);
    recyclerView = view.findViewById(R.id.recycler);
    AppDatabase db = AppDatabase.getInstance(Objects.requireNonNull(getContext()));
    db.sceneDao()
        .loadAllAreUploadedWithLive()
        .observe(
            this,
            scene -> {
              if (scene != null && !scene.isEmpty()) {
                adapter = new GalleryAdapter(getActivity(), scene);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setHasFixedSize(true);
                mTvNoPictures.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
              } else {
                mTvNoPictures.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
              }
            });
    return view;
  }
}
