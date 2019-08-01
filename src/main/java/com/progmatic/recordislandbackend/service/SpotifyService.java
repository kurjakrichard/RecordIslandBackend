package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.dao.ArtistRepository;
import com.progmatic.recordislandbackend.dao.UserRepository;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.domain.SpotifyAccessToken;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.SpotifyPlaylistDto;
import com.progmatic.recordislandbackend.dto.SpotifyPlaylistResponseDto;
import com.progmatic.recordislandbackend.dto.SpotifyTrackResponseDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.exception.SpotifyTokenNotFoundExcepion;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import java.net.URI;
import com.wrapper.spotify.model_objects.specification.SavedAlbum;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.library.GetCurrentUsersSavedAlbumsRequest;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.library.SaveAlbumsForCurrentUserRequest;
import com.wrapper.spotify.requests.data.playlists.AddTracksToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;


@Service
public class SpotifyService {

    private final RecordIslandProperties properties;
    private final SpotifyAccessToken spotifyAccestoken;
    private final RecordIslandProperties recordIslandProperties;
    private URI spotifyRedirectUri;
    private final UserService userService;
    private ArtistRepository artistRepository;
    private UserRepository userRepository;

    @Autowired
    public SpotifyService(RecordIslandProperties properties, SpotifyAccessToken spotifyAccestoken, UserService userService,
            ArtistRepository artistRepository, UserRepository userRepository, RecordIslandProperties recordIslandProperties) {
        this.properties = properties;
        this.spotifyAccestoken = spotifyAccestoken;
        this.userService = userService;
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
        this.recordIslandProperties = recordIslandProperties;
        this.spotifyRedirectUri = SpotifyHttpManager.makeUri(recordIslandProperties.getSpotifyRedirectUrl());

    }

    public URI getAuthorizationCodeUriRequest() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(properties.getSpotifyClientId())
                .setClientSecret(properties.getSpotifyClientSecret())
                .setRedirectUri(spotifyRedirectUri)
                .build();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-library-read,user-library-modify,playlist-modify-public,playlist-modify-private")
                //          .state("x4xkmn9pu3j6ukrs8n")
                //                          .scope("user-read-birthdate,user-read-email")
                //          .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri;
    }

    public void getToken(String code) throws SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(properties.getSpotifyClientId())
                .setClientSecret(properties.getSpotifyClientSecret())
                .setRedirectUri(spotifyRedirectUri)
                .build();
        final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

        System.out.println(authorizationCodeCredentials.getExpiresIn());
        spotifyAccestoken.setToken(authorizationCodeCredentials.getAccessToken());
        spotifyAccestoken.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        spotifyAccestoken.setExpiration(authorizationCodeCredentials.getExpiresIn());

    }

    public void refreshToken(String refreshToken) throws SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(properties.getSpotifyClientId())
                .setClientSecret(properties.getSpotifyClientSecret())
                .setRefreshToken(refreshToken)
                .build();
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                .build();
        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

        spotifyAccestoken.setToken(authorizationCodeCredentials.getAccessToken());
        spotifyAccestoken.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        spotifyAccestoken.setExpiration(authorizationCodeCredentials.getExpiresIn());
    }

    public SavedAlbum[] getSavedAlbums() throws SpotifyWebApiException, IOException {
        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();
        final GetCurrentUsersSavedAlbumsRequest getCurrentUsersSavedAlbumsRequest = spotifyApi
                .getCurrentUsersSavedAlbums()
                //          .limit(10)
                //          .market(CountryCode.SE)
                //          .offset(0)
                .build();
        SavedAlbum[] savedAlbums = getCurrentUsersSavedAlbumsRequest.execute().getItems();
        return savedAlbums;
    }

    public SavedTrack[] getSavedTracks() throws SpotifyWebApiException, IOException {

        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();
        final GetUsersSavedTracksRequest getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                //.limit(0)
                //          .market(CountryCode.SE)
                //          .offset(0)
                .build();
        SavedTrack[] savedTracks = getUsersSavedTracksRequest.execute().getItems();
        return savedTracks;
    }

    @Transactional
    public void saveSpotifyArtistFromTracks(SavedTrack[] tracks) throws SpotifyWebApiException, IOException {
        for (SavedTrack track : tracks) {
            ArtistSimplified[] artists = track.getTrack().getArtists();
            Artist artist;
            for (ArtistSimplified artistSimplified : artists) {
                String artistName = artistSimplified.getName();
//                System.out.println("Artist from track: " + artistName);
                try {
                    artist = artistRepository.findByName(artistName).get();
                } catch (NoSuchElementException e) {
                    artist = new Artist(artistName);
                    artistRepository.save(artist);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    User user = (User) userService.loadUserByUsername(username);
                    user.addArtistToLikedArtists(artist);
                    userRepository.save(user);
                    continue;
                }
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = (User) userService.loadUserByUsername(username);
                user.addArtistToLikedArtists(artist);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void saveSpotifyArtistFromAlbums(SavedAlbum[] savedAlbums) {
//        System.out.println("A kedvenc albumok száma: " + savedAlbums.length);
        for (SavedAlbum savedAlbum : savedAlbums) {
            ArtistSimplified[] artists = savedAlbum.getAlbum().getArtists();
            Artist artist;
            for (ArtistSimplified artistSname : artists) {
                String artistName = artistSname.getName();
//                System.out.println("Artist from album: " + artistName);
                try {
                    artist = artistRepository.findByName(artistName).get();
                } catch (NoSuchElementException e) {
                    artist = new Artist(artistName);
                    artistRepository.save(artist);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    User user = (User) userService.loadUserByUsername(username);
                    user.addArtistToLikedArtists(artist);
                    userRepository.save(user);
                }
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = (User) userService.loadUserByUsername(username);
                user.addArtistToLikedArtists(artist);
                userRepository.save(user);
            }
        }
    }

    public String searchForAlbumWithTracks(String artist, String album) throws IOException, SpotifyWebApiException, AlbumNotExistsException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken()); 
        }
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();
        SearchAlbumsRequest searchAlbumsRequest = spotifyApi.searchAlbums(artist + " " + album)
                //          .market(CountryCode.SE)
                //          .limit(10)
                //          .offset(0)
                .build();

        AlbumSimplified[] spotifyAlbum = searchAlbumsRequest.execute().getItems();
        if (spotifyAlbum.length == 0) {
            throw new AlbumNotExistsException(album + " not found!");
        }
        return spotifyAlbum[0].getId();

    }

    public void saveAlbumsForCurrentUser(String artist, String album) throws SpotifyWebApiException, IOException, AlbumNotExistsException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();
        SaveAlbumsForCurrentUserRequest saveAlbumsForCurrentUserRequest = spotifyApi
                .saveAlbumsForCurrentUser(searchForAlbumWithTracks(artist, album))
                .build();

        String string = saveAlbumsForCurrentUserRequest.execute();
    }

    public Set<SpotifyTrackResponseDto> getSpotifyAlbumTracks(String artist, String album) throws SpotifyWebApiException, IOException, AlbumNotExistsException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();

        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(searchForAlbumWithTracks(artist, album))
                //          .limit(10)
                //          .offset(0)
                //          .market(CountryCode.SE)
                .build();

        LinkedHashSet<SpotifyTrackResponseDto> trackResponse = new LinkedHashSet<>();

        try {

            Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();
            TrackSimplified[] tracks = trackSimplifiedPaging.getItems();
            for (TrackSimplified track : tracks) {
                trackResponse.add(new SpotifyTrackResponseDto(track.getName(), track.getPreviewUrl()));
            }

        } catch (IOException | SpotifyWebApiException e) {

        }
        return trackResponse;
    }

    public ArrayList<String> getSpotifyAlbumTracksUris(String artist, String album) throws SpotifyWebApiException, IOException, AlbumNotExistsException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();

        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(searchForAlbumWithTracks(artist, album))
                //          .limit(10)
                //          .offset(0)
                //          .market(CountryCode.SE)
                .build();

        ArrayList<String> uris = new ArrayList<>();

        try {

            System.out.println(getAlbumsTracksRequest.getJson());
            Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();
            TrackSimplified[] tracks = trackSimplifiedPaging.getItems();
            for (TrackSimplified track : tracks) {
                uris.add(track.getUri());
            }

        } catch (IOException | SpotifyWebApiException e) {

        }
        return uris;
    }

    public String[] getSpotifyAlbumTracksUrisArray(String artist, String album) throws SpotifyWebApiException, IOException, AlbumNotExistsException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();

        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(searchForAlbumWithTracks(artist, album))
                //          .limit(10)
                //          .offset(0)
                //          .market(CountryCode.SE)
                .build();

        ArrayList<String> uris = new ArrayList<>();

        System.out.println(getAlbumsTracksRequest.getJson());
        Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();
        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();
        for (TrackSimplified track : tracks) {
            uris.add(track.getUri());
        }

        String[] uriArray = new String[uris.size()];
        for (int i = 0; i < uris.size(); i++) {
            uriArray[i] = uris.get(i);
        }
        return uriArray;
    }

    public ArrayList<SpotifyPlaylistResponseDto> getListOfCurrentUsersPlaylists() throws SpotifyWebApiException, IOException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();

        GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi
                .getListOfCurrentUsersPlaylists()
                //          .limit(10)
                //          .offset(0)
                .build();

        Paging<PlaylistSimplified> playlistSimplifiedPaging = getListOfCurrentUsersPlaylistsRequest.execute();
        PlaylistSimplified[] ps = playlistSimplifiedPaging.getItems();
        ArrayList<SpotifyPlaylistResponseDto> playlistNames = new ArrayList<>();
        for (int i = 0; i < ps.length; i++) {
            playlistNames.add(new SpotifyPlaylistResponseDto(ps[i].getName(), ps[i].getId()));
        }
        return playlistNames;
    }

//    public void getSpotifyPlaylistByName() throws SpotifyWebApiException, IOException {
//        if (spotifyAccestoken.getToken() == null) {
//            throw new SpotifyTokenNotFoundExcepion("Token not found!");
//        }
//
//        if (spotifyAccestoken.isExpired()) {
//            refreshToken(spotifyAccestoken.getRefreshToken());
//        }
//        
//        SpotifyApi spotifyApi = new SpotifyApi.Builder()
//                .setAccessToken(spotifyAccestoken.getToken())
//                .build();
//    }
    
    public void addTracksToSpotifyPlaylist(SpotifyPlaylistDto playlist, String artist, String album) throws IOException, SpotifyWebApiException, AlbumNotExistsException {
        if (spotifyAccestoken.getToken() == null) {
            throw new SpotifyTokenNotFoundExcepion("Token not found!");
        }

        if (spotifyAccestoken.isExpired()) {
            refreshToken(spotifyAccestoken.getRefreshToken());
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(spotifyAccestoken.getToken())
                .build();

        AddTracksToPlaylistRequest addTracksToPlaylistRequest = spotifyApi
                .addTracksToPlaylist(playlist.getId(), getSpotifyAlbumTracksUrisArray(artist, album))
                //          .position(0)

                .build();

        SnapshotResult result = addTracksToPlaylistRequest.execute();
    }

}
