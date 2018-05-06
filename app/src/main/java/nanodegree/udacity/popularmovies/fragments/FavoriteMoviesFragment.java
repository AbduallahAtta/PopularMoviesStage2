package nanodegree.udacity.popularmovies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.udacity.popularmovies.R;
import nanodegree.udacity.popularmovies.adapters.FavoriteAdapter;
import nanodegree.udacity.popularmovies.database.MoviesContract;


public class FavoriteMoviesFragment extends Fragment implements FavoriteAdapter.onFavoriteMovieClick, FavoriteAdapter.onFavoriteMovieLongClick {
    @BindView(R.id.movieRecyclerView)
    RecyclerView mFavoriteMoviesRecyclerView;
    @BindView(R.id.emptyState)
    LinearLayout mEmptyState;
    Context mContext;
    RecyclerView.LayoutManager mMoviesLayoutManager;
    private FavoriteAdapter mAdapter;

    public FavoriteMoviesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View favoriteMoviesView = inflater.inflate(R.layout.main_movies_fragment, container, false);
        ButterKnife.bind(this, favoriteMoviesView);
        mContext = getContext();
        mMoviesLayoutManager = new GridLayoutManager(mContext, 2);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(0, null, cursorLoaderCallbacks);
        return favoriteMoviesView;
    }

    private void setupViews(Cursor favoriteMoviesCursor) {
        mAdapter = new FavoriteAdapter(mContext, favoriteMoviesCursor, this, this);
        mFavoriteMoviesRecyclerView.setLayoutManager(mMoviesLayoutManager);
        mFavoriteMoviesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onFavoriteMovieClickListener(int Id) {

    }

    @Override
    public void onFavoriteMovieLongClickListener(final int Id) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.delete_movie_alert_dialog_title))
                .setContentText(getString(R.string.delete_movie_alert_dialog_message))
                .setCancelText(getString(R.string.delete_movie_alert_dialog_negative_button_label))
                .setConfirmText(getString(R.string.delete_movie_alert_dialog_positive_button_label))
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteMovie(Id);
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void deleteMovie(int id) {
        String idToDelete = Integer.toString(id);
        Uri MOVIE_WITH_ID = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon()
                .appendEncodedPath(idToDelete)
                .build();
        mContext.getContentResolver().delete(MOVIE_WITH_ID, null, null);

    }

    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext,
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() != 0) {
                setupViews(data);
            } else {
                mFavoriteMoviesRecyclerView.setVisibility(View.GONE);
                mEmptyState.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };


}