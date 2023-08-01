package main.java.audio.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GetPlaylistSpotify {

    private static final String playlistId = "5GOQSS6SUFDD6SyMfVRMA5";
    private static final SpotifyApi spotifyApi = SpotifyApiConnection.getSpotifyApi();
    private static final GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId)
            .build();


    public static void getPlaylist_Async() {
        try {
            final CompletableFuture<Playlist> playlistFuture = getPlaylistRequest.executeAsync();

            final Playlist playlist = playlistFuture.join();

            System.out.println("Name: " + playlist.getName());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SpotifyApiConnection.clientCredentials_Async(); // Fetch the access token
        getPlaylist_Async();
    }
}