package de.kifo.common.api;

import com.google.gson.Gson;
import de.kifo.common.api.model.Playlist;
import de.kifo.common.api.model.Song;
import de.kifo.common.api.model.User;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.google.gson.reflect.TypeToken.getParameterized;
import static java.lang.Boolean.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;

public class API {

    /**
     * {@link User}
     */

    public void createUser(User user) {
        boolean userPresent = getAllUsers().stream()
                .map(User::getId)
                .filter(userId -> userId.equals(user.getId()))
                .findFirst()
                .isPresent();

        if (!userPresent) {
            String jsonRequest = getJsonByObject(user);
            sendPostRequest("http://localhost:8080/javabot/user/add", jsonRequest);
        }
    }

    public void updateUser(User user) {
        String jsonRequest = getJsonByObject(user);
        sendPutRequest("http://localhost:8080/javabot/user/update", jsonRequest);
    }

    public User getUserById(Long id) {
        return getObjectByJson(sendGetRequest("http://localhost:8080/javabot/user/get/" + id), User.class);
    }

    public List<User> getAllUsers() {
        return getObjectListByJson(sendGetRequest("http://localhost:8080/javabot/user/get-all"), User.class);
    }

    public void deleteUser(Long id) {
        sendDeleteRequest("http://localhost:8080/javabot/user/remove/" + id);
    }

    /**
     * {@link Song}
     */

    /**
     * Returns a list of every song that was ever used by a user.
     *
     * @param userId The id of the user
     * @return The List of every song
     */

    public List<Song> getSongListByUserId(Long userId) {
        return getObjectListByJson(sendGetRequest("http://localhost:8080/javabot/song/get-all/" + userId), Song.class);
    }

    public List<Song> getAllSongs() {
        return getObjectListByJson(sendGetRequest("http://localhost:8080/javabot/song/get-all"), Song.class);
    }

    public void updateSong(Long usedId, String name) {
        Song song = getSongListByUserId(usedId).stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> new Song(1L, usedId, 0L, currentTimeMillis(), name));

        song.setTimesPlayed(song.getTimesPlayed() + 1);
        song.setLastPlayDate(currentTimeMillis());

        String jsonRequest = getJsonByObject(song);
        sendPutRequest("http://localhost:8080/javabot/song/update/" + song.getUserId(), jsonRequest);
    }

    /**
     * {@link Playlist}
     */

    public Playlist getPlaylistByName(String name) {
        return getObjectByJson(sendGetRequest("http://localhost:8080/javabot/playlist/get/" + name), Playlist.class);
    }

    public List<Playlist> getPlaylistsListByUserId(Long userId) {
        return getObjectListByJson(sendGetRequest("http://localhost:8080/javabot/playlist/get-all/" + userId), Playlist.class);
    }

    public boolean updatePlaylist(String name, Playlist playlist) {
        return valueOf(sendPutRequest("http://localhost:8080/javabot/playlist/update/" + name, getJsonByObject(playlist)));
    }

    public boolean deletePlaylist(String name) {
        return valueOf(sendDeleteRequest("http://localhost:8080/javabot/playlist/delete/" + name));
    }

    /**
     *
     Requests
     *
     */

    private String getJsonByObject(Object object) {
        return new Gson().toJson(object);
    }

    public <T> T getObjectByJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public <T> List<T> getObjectListByJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, getParameterized(List.class, clazz).getType());
    }

    private String sendGetRequest(String uri) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "";
        }
    }

    private String sendPostRequest(String uri, String json) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(uri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "";
        }
    }

    private String sendPutRequest(String uri, String json) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(uri))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "";
        }
    }

    private String sendDeleteRequest(String uri) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(uri))
                    .DELETE()
                    .build();

            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "";
        }
    }
}
