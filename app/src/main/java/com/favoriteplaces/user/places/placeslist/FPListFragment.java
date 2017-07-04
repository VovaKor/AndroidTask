package com.favoriteplaces.user.places.placeslist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.favoriteplaces.R;
import com.favoriteplaces.domain.models.FavoritePlace;
import com.favoriteplaces.user.places.add.AddFavoritePlaceActivity;
import com.favoriteplaces.user.places.details.FPDetailsActivity;
import com.favoriteplaces.utils.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 03.07.17.
 */

public class FPListFragment extends Fragment implements FPListContract.View{


    private FPListContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private PlacesAdapter mListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public FPListFragment() {
        // Requires empty public constructor
    }

    public static FPListFragment newInstance() {
        return new FPListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new PlacesAdapter(new ArrayList<FavoritePlace>(0), mItemListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull FPListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.place_frag, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.place_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mListAdapter);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_place);

       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openAddPlaceUI();
            }
        });

        return root;
    }

    PlaceItemListener mItemListener = new PlaceItemListener() {

        @Override
        public void onPlaceTitleClick(FavoritePlace place) {
            mPresenter.openPlaceDetails(place.getId());
        }
    };

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.place_refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showPlaces(List<FavoritePlace> places) {
        mListAdapter.replaceData(places);

        mRecyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void showLoadingPlacesError() {
        showMessage(getString(R.string.loading_places_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFPDetailsUI(String id) {
        Intent intent = new Intent(getContext(), FPDetailsActivity.class);
        intent.putExtra(getString(R.string.EXTRA_PLACE_ID), id);
        startActivity(intent);
    }

    @Override
    public void showAddFPUI(String mUserId) {
        Intent intent = new Intent(getContext(), AddFavoritePlaceActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USER_ID), mUserId);
        startActivity(intent);
    }

    private class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

        private List<FavoritePlace> mPlaces;
        private PlaceItemListener mItemListener;

        public PlacesAdapter(List<FavoritePlace> places, PlaceItemListener itemListener) {
            setList(places);
            mItemListener = itemListener;
        }

        public void replaceData(List<FavoritePlace> places) {
            setList(places);
            notifyDataSetChanged();
        }

        private void setList(List<FavoritePlace> places) {
            mPlaces = checkNotNull(places);
        }


        @Override
        public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_item, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;

        }

        @Override
        public void onBindViewHolder(PlacesAdapter.ViewHolder holder, int position) {

            final FavoritePlace place = mPlaces.get(position);
            if (place.getPhoto()!=null){
                Bitmap bitmap = mPresenter.createImageBitmap(place.getPhoto(), getResources().getDisplayMetrics().density);
                holder.mImageView.setImageBitmap(bitmap);
            }else {
                holder.mImageView.setImageBitmap(null);
            }
            holder.mTitleView.setText(place.getTitle());

            holder.mTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onPlaceTitleClick(place);
                }
            });


        }

        @Override
        public int getItemCount() {
            return mPlaces.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mImageView;
            public TextView mTitleView;

            public ViewHolder(View v) {
                super(v);
                mTitleView = (TextView) v.findViewById(R.id.place_item_title);
                mImageView = (ImageView) v.findViewById(R.id.place_item_thumbnail);
            }
        }
    }

    public interface PlaceItemListener {

        void onPlaceTitleClick(FavoritePlace place);
    }

}
