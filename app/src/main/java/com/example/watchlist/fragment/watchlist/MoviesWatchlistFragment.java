package com.example.watchlist.fragment.watchlist;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.watchlist.R;
import com.example.watchlist.adapter.MoviesAdapter;
import com.example.watchlist.database.MovieDatabaseUtil;
import com.example.watchlist.database.MovieWatch;
import com.example.watchlist.service.client.NetworkChecker;
import com.example.watchlist.service.request.ReqMovies;
import com.example.watchlist.shareInfo.Cache;
import com.example.watchlist.shareInfo.GerneList;
import com.example.watchlist.themoviedb.Movie;
import com.example.watchlist.utils.BackgroundPoster;
import com.example.watchlist.utils.Pagination;
import com.example.watchlist.utils.PopUpMsg;
import com.example.watchlist.utils.Time;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesWatchlistFragment extends Fragment {
    private static final String TAG ="MoviesWatchFrag";

    private Context context;
    private Time time;
    private Pagination pagination;
    private List<MovieWatch> movieWatchList;

    private ImageView poster;
    private MoviesAdapter moviesAdapter;




    public MoviesWatchlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movies_watchlist, container, false);

        context = getContext();

        poster = (ImageView) v.findViewById(R.id.poster_watchlist_movie_imageView);
        time = new Time();
        pagination = new Pagination();

        RecyclerView onAirTvShowsRecycler = (RecyclerView) v.findViewById(R.id.watchlist_movie_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        onAirTvShowsRecycler.setLayoutManager(layoutManager);
        moviesAdapter = new MoviesAdapter(context,getActivity().getSupportFragmentManager());
        onAirTvShowsRecycler.setAdapter(moviesAdapter);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        movieWatchList = MovieDatabaseUtil.getallMovie();
        if(!Cache.NowPlayingMovie.isEmpty() && !time.isOverTime(Cache.NowPlayingMovie.getTime(),time.ONE_HOUR)){
            displayData(Cache.NowPlayingMovie.getMovieList());
        }else{
            Cache.NowPlayingMovie.clear();
            Cache.NowPlayingMovie.setTime(time.getTimeInMillis());
            reqToDayMovie();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    /**
     * Sends Http Request that request now playing movies
     */
    private void reqToDayMovie(){
        if(NetworkChecker.isOnline(context)) {
            ReqMovies.nowPlayingMovies(pagination.getCurrentPage(), resNowPlayingMovies());
        }
        else {
            PopUpMsg.toastMsg("Network isn't avilable",context);
        }

    }

    /**
     * Receiving Respond from the backend server.
     *
     */
    public Callback resNowPlayingMovies(){
        return new Callback<Movie.MoviesResults>(){
            @Override
            public void onResponse(Call<Movie.MoviesResults> call, Response<Movie.MoviesResults> response) {
                if(response.isSuccessful()){
                    pagination.setTotalPages(response.body().getTotalPages());

                    Cache.NowPlayingMovie.addToMovie(response.body().getResults());

                    pagination.setCurrentPage(pagination.getCurrentPage()+1);

                    if (pagination.getCurrentPage() > pagination.getTotalPages()){
                        displayData(Cache.NowPlayingMovie.getMovieList());

                    }else{
                        reqToDayMovie();
                    }

                }else {
                    PopUpMsg.displayErrorMsg(context);
                }
            }

            @Override
            public void onFailure(Call<Movie.MoviesResults> call, Throwable t) {
                PopUpMsg.displayErrorMsg(context);

            }
        };
    }

    public List<Movie> filterOutData(List<Movie> movieList, List<MovieWatch> movieWatchList){
        List<Movie> newMovieList = new ArrayList<>();

        for (Movie movie : movieList){
            for(MovieWatch watch : movieWatchList){
                if(movie.getId() == watch.getMovieId()){
                    newMovieList.add(movie);
                    break;
                }
            }
        }
        return newMovieList;
    }

    /**
     * Display the now playing movies on the screen;
     * @param movieList tvShowList contains movies results.
     */
    public void displayData(List<Movie> movieList){

        List<Movie> newMovieList = filterOutData(movieList,movieWatchList);

        if(newMovieList.size() != 0) {
            if (GerneList.getGenreMovieList() != null) {
                moviesAdapter.addAllGenre(GerneList.getGenreMovieList());
            }
            if (moviesAdapter.isEmpty() && newMovieList.size() != 0) {
                BackgroundPoster.setRandomBackPosterMovie(newMovieList, context, poster);
            }
            moviesAdapter.addAll(newMovieList);

        }

    }


}
