
package com.androidtask.admin;

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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidtask.R;
import com.androidtask.domain.models.User;
import com.androidtask.admin.actions.AdminActionsActivity;
import com.androidtask.utils.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link User}s.
 */
public class AdminFragment extends Fragment implements AdminContract.View {


    private AdminContract.Presenter mPresenter;

   // private UsersAdapter ;

  //  private LinearLayout mUsersView;

    private RecyclerView mRecyclerView;
    private UsersAdapter mListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public AdminFragment() {
        // Requires empty public constructor
    }

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new UsersAdapter(new ArrayList<User>(0), mItemListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AdminContract.Presenter presenter) {
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
        View root = inflater.inflate(R.layout.user_frag, container, false);

//        // Set up users view
//        ListView listView = (ListView) root.findViewById(R.id.users_list);
//        listView.setAdapter(mListAdapter);

       // mUsersView = (LinearLayout) root.findViewById(R.id.users_LL);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
       // mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mListAdapter);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_delete_user);

        fab.setImageResource(R.drawable.ic_delete);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteCheckedUsers();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.bg_main),
                ContextCompat.getColor(getActivity(), R.color.white),
                ContextCompat.getColor(getActivity(), R.color.input_register_bg)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mRecyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadUsers(false);
            }
        });

        return root;
    }


    /**
     * Listener for clicks on users in the ListView.
     */
    UserItemListener mItemListener = new UserItemListener() {

        @Override
        public void onMarkUserClick(User markedUser) {
            mPresenter.markUser(markedUser);
        }

        @Override
        public void onUncheckUserClick(User uncheckedUser) {
            mPresenter.uncheckUser(uncheckedUser);
        }

        @Override
        public void onUserNicknameClick(User user) {
            mPresenter.openActionsDialog(user);
        }
    };

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showUsers(List<User> users) {
        mListAdapter.replaceData(users);

        mRecyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void showMarkedUser() {
        showMessage(getString(R.string.user_marked_toDelete));
    }

    @Override
    public void showCheckedUsersCleared() {
        showMessage(getString(R.string.checked_users_cleared));
    }

    @Override
    public void showLoadingUsersError() {
        showMessage(getString(R.string.loading_users_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showUserUnchecked() {
        showMessage(getString(R.string.user_unchecked));
    }

    @Override
    public void showAdminActionsUI(String id) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        Intent intent = new Intent(getContext(), AdminActionsActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USER_ID), id);
        startActivity(intent);
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        private List<User> mUsers;
        private UserItemListener mItemListener;

        public UsersAdapter(List<User> users, UserItemListener itemListener) {
            setList(users);
            mItemListener = itemListener;
        }

        public void replaceData(List<User> users) {
            setList(users);
            notifyDataSetChanged();
        }

        private void setList(List<User> users) {
            mUsers = checkNotNull(users);
        }


        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_item, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;

        }

        @Override
        public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final User user = mUsers.get(position);
                if (user.getThumbnail()!=null){
                    Bitmap bitmap = mPresenter.createImageBitmap(user.getThumbnail(), getResources().getDisplayMetrics().density);
                    holder.mImageView.setImageBitmap(bitmap);
                }else {
                    holder.mImageView.setImageBitmap(null);
                }

                holder.mTitleView.setText(user.getNick_name());
                holder.mCheckBox.setChecked(user.isMarked());
                if (user.isMarked()) {
                    holder.itemView.setBackgroundDrawable(holder.itemView.getContext()
                            .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
                } else {
                    holder.itemView.setBackgroundDrawable(holder.itemView.getContext()
                            .getResources().getDrawable(R.drawable.touch_feedback));
                }

                holder.mTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemListener.onUserNicknameClick(user);
                    }
                });

                holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!user.isMarked()) {
                            mItemListener.onMarkUserClick(user);
                        } else {
                            mItemListener.onUncheckUserClick(user);
                        }
                    }
                });


        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mImageView;
            public TextView mTitleView;
            public CheckBox mCheckBox;

            public ViewHolder(View v) {
                super(v);
                mTitleView = (TextView) v.findViewById(R.id.user_item_title);
                mCheckBox = (CheckBox) v.findViewById(R.id.user_checkBox);
                mImageView = (ImageView) v.findViewById(R.id.user_item_thumbnail);
            }
        }
    }

    public interface UserItemListener {

        void onMarkUserClick(User markedUser);

        void onUncheckUserClick(User activatedUser);

        void onUserNicknameClick(User user);
    }

}
