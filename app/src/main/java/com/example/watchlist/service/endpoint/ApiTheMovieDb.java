package com.example.watchlist.service.endpoint;


import com.example.watchlist.themoviedb.Genre;
import com.example.watchlist.themoviedb.Movie;
import com.example.watchlist.themoviedb.MovieDetails;
import com.example.watchlist.themoviedb.TvDetails;
import com.example.watchlist.themoviedb.TvEpisodeDetails;
import com.example.watchlist.themoviedb.TvSeasonDetails;
import com.example.watchlist.themoviedb.TvShow;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * It has all the endpoint for the TMDb.
 */

public interface ApiTheMovieDb {

    @GET("tv/airing_today")
    Call<TvShow.TvShowsResults> toDayShows(@Query("page") int page);

    @GET("tv/popular")
    Call<TvShow.TvShowsResults> popularShows(@Query("page") int page);

    @GET("tv/on_the_air")
    Call<TvShow.TvShowsResults> onAirShows(@Query("page") int page);

    @GET("tv/top_rated")
    Call<TvShow.TvShowsResults> ratedShows(@Query("page") int page);

    @GET("tv/{tv_id}")
    Call<TvDetails> tvDetails(@Path("tv_id") long tv_id);

    @GET("tv/{tv_id}/season/{season_number}")
    Call<TvSeasonDetails> seasonDetails(@Path("tv_id") long tv_id, @Path("season_number") int season_number);

    @GET("tv/{tv_id}/season/{season_number}/episode/{episode_number}")
    Call<TvEpisodeDetails> episodeDetails(@Path("tv_id") long tv_id, @Path("season_number") int season_number, @Path("episode_number") int episode_number);


    @GET("genre/tv/list")
    Call<Genre.GenreResults> genreTv();

    @GET("genre/movie/list")
    Call<Genre.GenreResults> genreMovies();


    @GET("movie/top_rated")
    Call<Movie.MoviesResults> ratedMovies(@Query("page") int page, @Query("region") String region);

    @GET("movie/now_playing")
    Call<Movie.MoviesResults> nowPlayingMovies(@Query("page") int page,@Query("region") String region);

    @GET("movie/popular")
    Call<Movie.MoviesResults> popularMovies(@Query("page") int page,@Query("region") String region);

    @GET("movie/upcoming")
    Call<Movie.MoviesResults> upcomingMovies(@Query("page") int page,@Query("region") String region);

    @GET("movie/{movie_id}")
    Call<MovieDetails> movieDetails(@Path("movie_id") long movie_id);

    @GET("search/tv")
    Call<TvShow.TvShowsResults> searchTvShows(@Query("query") String query, @Query("page") int page);

    @GET("search/movie")
    Call<Movie.MoviesResults> searchMovie(@Query("query") String query, @Query("page") int page,@Query("region") String region);



}

