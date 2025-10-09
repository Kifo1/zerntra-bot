package de.kifo.common.api;

import com.google.gson.Gson;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.Playlist;
import de.kifo.common.api.model.Song;
import de.kifo.common.api.model.User;
import de.kifo.common.api.model.VoiceChannelOnlineSession;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

import static com.google.gson.reflect.TypeToken.getParameterized;
import static de.kifo.JavaBot.BOT_API_KEY;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class API {

    private final String API_BASE_URL = "http://localhost:8080/api/v1";

    /**
     * {@link User}
     */

    public void updateUser(User user) {
        boolean userPresent = nonNull(getUserById(user.getId()));

        String jsonRequest = getJsonByObject(user);
        if (userPresent) {
            sendPutRequest("/users/update", jsonRequest);
        }
    }

    public void createUserIfNotPresent(Long userId, String name) {
        String jsonRequest = getJsonByObject(new User(userId, name, null, currentTimeMillis(), null));

        if (isNull(getUserById(userId))) {
            sendPostRequest("/users/add", jsonRequest);
        }
    }

    public User getUserById(Long id) {
        return getObjectByJson(sendGetRequest("/users/get/" + id), User.class);
    }

    public boolean isUserRegistered(User user) {
        return getObjectByJson(sendPutRequest("/users/registered", getJsonByObject(user)), Boolean.class);
    }

    public List<User> getAllUsers() {
        return getObjectListByJson(sendGetRequest("/users/get-all"), User.class);
    }

    public void deleteUser(Long id) {
        sendDeleteRequest("/users/remove/" + id);
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
        return getObjectListByJson(sendGetRequest("/songs/get-all/" + userId), Song.class);
    }

    /**
     * This methode can be used to receive a users favourite songs (25 most liked)
     */
    public List<Song> getRecommendedSongsByUserId(Long userId) {
        return getObjectListByJson(sendGetRequest("/songs/get-recommended/" + userId), Song.class);
    }

    public List<Song> getAllSongs() {
        return getObjectListByJson(sendGetRequest("/songs/get-all"), Song.class);
    }

    public void updateSong(Long usedId, String name) {
        Song song = getSongListByUserId(usedId).stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> new Song(null, usedId, 0L, currentTimeMillis(), name));

        song.setTimesPlayed(song.getTimesPlayed() + 1);
        song.setLastPlayDate(currentTimeMillis());

        String jsonRequest = getJsonByObject(song);
        sendPutRequest("/songs/update/" + song.getUserId(), jsonRequest);
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
        return parseBoolean(sendPutRequest("/history/" + historyEntry.getUserId(), getJsonByObject(historyEntry)));
    }

    public Collection<HistoryEntry> getHistoryEntriesByUserId(Long userId) {
        return getObjectListByJson("/history/" + userId, HistoryEntry.class);
    }

    /**
     *  {@link VoiceChannelOnlineSession}
     */

    public void addVoiceChannelOnlineSessionToUser(Long userId, VoiceChannelOnlineSession session) {
        String jsonRequest = getJsonByObject(session);
        sendPutRequest("/users/voice-session/" + userId, jsonRequest);
    }

    public long getVoiceSessionSecondsForTimePeriod(Long userId, VoiceChannelOnlineSession.TimePeriod timePeriod) {
        String uri = format("/users/voice-session/%d?timePeriod=%s", userId, timePeriod.name());
        String response = sendGetRequest(uri);

        return ofNullable(getObjectByJson(response, Long.class)).orElse(-1L);
    }

    public long getVoiceSessionSecondsForTimePeriodAndGuild(Long userId, VoiceChannelOnlineSession.TimePeriod timePeriod,
                                                            Long guildId) {
        String uri = format("/users/voice-session-for-guild/%d?timePeriod=%s&guildId=%d",
                userId, timePeriod, guildId);
        String response = sendGetRequest(uri);

        return ofNullable(getObjectByJson(response, Long.class)).orElse(-1L);
    }

    public long getVoiceSessionSecondsForTimePeriodAndChannelInGuild(Long userId, VoiceChannelOnlineSession.TimePeriod timePeriod,
                                                                     Long guildId, Long channelId) {
        String uri = format("/users/voice-session-for-channel-in-guild/%d?timePeriod=%s&guildId=%d&channelId=%d",
                userId, timePeriod, guildId, channelId);
        String response = sendGetRequest(uri);

        return ofNullable(getObjectByJson(response, Long.class)).orElse(-1L);
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
                    .header("X-API-KEY", BOT_API_KEY)
                    .GET()
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    private String sendPostRequest(String uri, String json) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(API_BASE_URL + uri))
                    .header("Content-Type", "application/json")
                    .header("X-API-KEY", BOT_API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    private String sendPutRequest(String uri, String json) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(API_BASE_URL + uri))
                    .header("Content-Type", "application/json")
                    .header("X-API-KEY", BOT_API_KEY)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    private String sendDeleteRequest(String uri) {
        try {
            HttpRequest httpRequest = newBuilder()
                    .uri(new URI(API_BASE_URL + uri))
                    .header("X-API-KEY", BOT_API_KEY)
                    .DELETE()
                    .build();
            return newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}
