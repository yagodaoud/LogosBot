package main.java.audio.spotify;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class SpotifyApiConnection {
    private static final String clientId ="52f8a96cc2184e13b51960b4e08fdfec";
    private static final String clientSecret = "e3b12a0a15374654a5cedfedbaa59f0f";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();

    private SpotifyApiConnection() {
    }

    public static SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }
    public static void clientCredentials_Async() {
        try {
            final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                    .build();

            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            System.out.println(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
            e.printStackTrace();
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
}





