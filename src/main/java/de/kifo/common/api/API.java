package de.kifo.common.api;

import com.google.gson.Gson;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.Playlist;
import de.kifo.common.api.model.Song;
import de.kifo.common.api.model.User;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

import static com.google.gson.reflect.TypeToken.getParameterized;
import static java.lang.Boolean.parseBoolean;
import static java.lang.System.currentTimeMillis;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class API {

    private final String API_BASE_URL = "http://localhost:8080";

    /**
     * {@link User}
     */

    public void updateUser(User user) {
        boolean userPresent = nonNull(getUserById(user.getId()));

        String jsonRequest = getJsonByObject(user);
        if (userPresent) {
            sendPutRequest("/javabot/user/update", jsonRequest);
        }
    }

    public void createUserIfNotPresent(Long userId, String name) {
        String jsonRequest = getJsonByObject(new User(userId, name, null, currentTimeMillis(), null));

        if (isNull(getUserById(userId))) {
            sendPostRequest("/javabot/user/add", jsonRequest);
        }
    }

    public User getUserById(Long id) {
        return getObjectByJson(sendGetRequest("/javabot/user/get/" + id), User.class);
    }

    public boolean isUserRegistered(User user) {
        return getObjectByJson(sendPutRequest("/javabot/user/registered", getJsonByObject(user)), Boolean.class);
    }

    public List<User> getAllUsers() {
        return getObjectListByJson(sendGetRequest("/javabot/user/get-all"), User.class);
    }

    public void deleteUser(Long id) {
        sendDeleteRequest("/javabot/user/remove/" + id);
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
        return getObjectListByJson(sendGetRequest("/javabot/song/get-all/" + userId), Song.class);
    }

    public List<Song> getAllSongs() {
        return getObjectListByJson(sendGetRequest("/javabot/song/get-all"), Song.class);
    }

    public void updateSong(Long usedId, String name) {
        Song song = getSongListByUserId(usedId).stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> new Song(1L, usedId, 0L, currentTimeMillis(), name));

        song.setTimesPlayed(song.getTimesPlayed() + 1);
        song.setLastPlayDate(currentTimeMillis());

        String jsonRequest = getJsonByObject(song);
        sendPutRequest("/javabot/song/update/" + song.getUserId(), jsonRequest);
    }

    /**
     * {@link Playlist}
     */

    public Playlist getPlaylistByName(String name) {
        return getObjectByJson(sendGetRequest("/javabot/playlist/get/" + name), Playlist.class);
    }

    public List<Playlist> getPlaylistsListByUserId(Long userId) {
        return getObjectListByJson(sendGetRequest("/javabot/playlist/get-all/" + userId), Playlist.class);
    }

    public boolean updatePlaylist(String name, Playlist playlist) {
        return parseBoolean(sendPutRequest("/javabot/playlist/update/" + name, getJsonByObject(playlist)));
    }

    public boolean deletePlaylist(String name) {
        return parseBoolean(sendDeleteRequest("/javabot/playlist/delete/" + name));
    }

    /**
     * {@link HistoryEntry}
     */

    public boolean createHistoryEntry(HistoryEntry historyEntry) {
        return parseBoolean(sendPutRequest("/javabot/history/" + historyEntry.getUserId(), getJsonByObject(historyEntry)));
    }

    public Collection<HistoryEntry> getHistoryEntriesByUserId(Long userId) {
        return getObjectListByJson("/javabot/history/" + userId, HistoryEntry.class);
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
                    .uri(new URI(API_BASE_URL + uri))
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
                    .uri(new URI(API_BASE_URL + uri))
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
                    .uri(new URI(API_BASE_URL + uri))
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
                    .uri(new URI(API_BASE_URL + uri))
                    .DELETE()
                    .build();

            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "";
        }
    }
}
