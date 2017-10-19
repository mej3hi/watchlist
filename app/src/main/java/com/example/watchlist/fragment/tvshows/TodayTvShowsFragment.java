package com.example.watchlist.fragment.tvshows;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.watchlist.shareInfo.GerneList;
import com.example.watchlist.R;
import com.example.watchlist.utils.BackgroundPoster;
import com.example.watchlist.utils.Pagination;
import com.example.watchlist.utils.PaginationScrollListener;
import com.example.watchlist.adapter.TvShowsAdapter;
import com.example.watchlist.service.client.NetworkChecker;
import com.example.watchlist.service.request.ReqTvShows;
import com.example.watchlist.service.response.tvShows.ResTodayTvShows;
import com.example.watchlist.themoviedb.TvShow;
import com.example.watchlist.utils.PopUpMsg;
import com.example.watchlist.utils.Time;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayTvShowsFragment extends Fragment {
    private static final String TAG ="TodayTvShowsFrag";

    private Context context;
    private Time time;
    private Pagination pagination;

    private ImageView poster;
    private TvShowsAdapter tvShowsAdapter;


    public TodayTvShowsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_tv_shows, container, false);

        context = getContext();

        poster = (ImageView) view.findViewById(R.id.poster_today_tv_shows_imageView);

        RecyclerView todayShowsRecycler = (RecyclerView) view.findViewById(R.id.today_tv_shows_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        todayShowsRecycler.setLayoutManager(layoutManager);
        if(tvShowsAdapter == null || time == null || pagination == null || time.isOverTime(time.ONE_HOUR)) {
            initialize();
        }
        todayShowsRecycler.setAdapter(tvShowsAdapter);
        todayShowsRecycler.addOnScrollListener(setPageScrollListener(layoutManager));


        Log.d(TAG,"onCreateView");
        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        if(tvShowsAdapter.isEmpty()) {
            time.setFirstTime(time.getTimeInMillis());
            reqToDayShows();
        }else{
            BackgroundPoster.setRandomBackPosterTv(tvShowsAdapter.getTvShowList(), context, poster);
        }
    }


    @Override
    public void onStop() {
        Log.d(TAG,"onStop");
        super.onStop();
    }

    /**
     * Here we initialize the fragment
     */
    public void initialize(){
        time = new Time();
        tvShowsAdapter = new TvShowsAdapter(context, getActivity().getSupportFragmentManager());
        pagination = new Pagination();
    }


    /**
     * It add a pagination scroll listener that ask for more data
     * if it is not the last page and not loading.
     * @param layoutManager LayoutManager contains the LinearLayoutManager.
     * @return It return the PaginationScrollListener.
     */
    private PaginationScrollListener setPageScrollListener(LinearLayoutManager layoutManager){
        return new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                pagination.setLoading(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reqToDayShows();
                    }
                },500);
            }

            @Override
            public boolean isLastPage() {
                return pagination.isLastPage();
            }

            @Override
            public boolean isLoading() {
                return pagination.isLoading();
            }
        };

    }

    /**
     * Sends HttpRequest that request To day tv shows
     */
    private void reqToDayShows(){
        Log.d(TAG,"ná í on today shows");

        if(NetworkChecker.isOnline(context)) {
            ReqTvShows.toDayShows(pagination.getCurrentPage(),resToDayShows());
        }
        else {
            PopUpMsg.toastMsg("Network isn't avilable",getContext());
        }


    }

    /**
     * Receiving Respond from the backend server.
     * @return It return Callback.
     */
    public Callback resToDayShows(){
        return new Callback<TvShow.TvShowsResults>(){
            @Override
            public void onResponse(Call<TvShow.TvShowsResults> call, Response<TvShow.TvShowsResults> response) {
                if(response.isSuccessful()){

                    pagination.setTotalPages(response.body().getTotalPages());

                    pagination.setLoading(false);

                    displayData(response.body());

                    pagination.setCurrentPage(pagination.getCurrentPage()+1);

                    Log.d(TAG,"isLastPage "+pagination.isLastPage()+" isLoading "+pagination.isLoading());

                }else {
                    PopUpMsg.displayErrorMsg(context);
                }
            }

            @Override
            public void onFailure(Call<TvShow.TvShowsResults> call, Throwable t) {
                PopUpMsg.displayErrorMsg(context);

            }
        };
    }


    /**
     * Display the To day tv shows on the screen;
     * @param results Results contains Tv shows results.
     */
    public void displayData(TvShow.TvShowsResults results){
        if(!tvShowsAdapter.isEmpty()){
            tvShowsAdapter.removeLoadingFooter();
        }

        if(GerneList.getGenreTvList() != null){
            tvShowsAdapter.addAllGenre(GerneList.getGenreTvList());
        }

        if(tvShowsAdapter.isEmpty()){
            BackgroundPoster.setRandomBackPosterTv(results.getResults(), context, poster);
        }

        tvShowsAdapter.addAll(results.getResults());

        if(pagination.getCurrentPage() < pagination.getTotalPages()) tvShowsAdapter.addLoadingFooter();
        else pagination.setLastPage(true);
    }





}
