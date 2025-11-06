package de.kifo.common.api;

import com.google.gson.Gson;
import de.kifo.common.api.model.HistoryEntryDTO;
import de.kifo.common.api.model.PlaylistDTO;
import de.kifo.common.api.model.SongDTO;
import de.kifo.common.api.model.UserDTO;
import de.kifo.common.api.model.VoiceChannelOnlineSessionDTO;
import de.kifo.common.api.model.utils.PasswordDTO;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.google.gson.reflect.TypeToken.getParameterized;
import static de.kifo.JavaBot.BOT_API_KEY;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class API {

    private final String API_BASE_URL = "http://localhost:8080/api/v1";

    /**
     * {@link UserDTO}
     */

    public void updateUser(UserDTO userDTO) {
        String jsonRequest = getJsonByObject(userDTO);
        sendPutRequest("/users/update", jsonRequest);
    }

    public void createUser(Long userId, String name) {
        String jsonRequest = getJsonByObject(new UserDTO(userId, name, currentTimeMillis()));
        sendPostRequest("/users/add", jsonRequest);
    }

    public void setPasswordForUser(UserDTO userDto, PasswordDTO passwordDTO) {
        String jsonRequest = getJsonByObject(passwordDTO);
        sendPostRequest("/users/password/" + userDto.getId(), jsonRequest);
    }

    public UserDTO getUserById(Long id) {
        return getObjectByJson(sendGetRequest("/users/get/" + id), UserDTO.class);
    }

    /**
     * {@link SongDTO}
     */

    /**
     * Returns a list of every song that was ever used by a user.
     *
     * @param userId The id of the user
     * @return The List of every song
     */
    public List<SongDTO> getSongListByUserId(Long userId) {
        return getObjectListByJson(sendGetRequest("/songs/get-all/" + userId), SongDTO.class);
    }

    /**
     * This methode can be used to receive a users favourite songs (25 most liked)
     */
    public List<SongDTO> getRecommendedSongsByUserId(Long userId) {
        return getObjectListByJson(sendGetRequest("/songs/get-recommended/" + userId), SongDTO.class);
    }

    public void updateSong(Long userId, String name, @Nullable String uri) {
        SongDTO songDTO = getSongListByUserId(userId).stream()
                .filter(song -> (nonNull(song.getUri()) && song.getUri().equals(uri)) ||
                        song.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> new SongDTO(null, userId, 0L, currentTimeMillis(), name, uri));

        songDTO.setTimesPlayed(songDTO.getTimesPlayed() + 1);
        songDTO.setLastPlayDate(currentTimeMillis());

        String jsonRequest = getJsonByObject(songDTO);
        sendPutRequest("/songs/update", jsonRequest);
    }

    /**
     * {@link PlaylistDTO}
     */

    public List<PlaylistDTO> getAllPlaylistsByUser(Long userId) {
        return getObjectListByJson(sendGetRequest("/javabot/playlist/get-by-user/" + userId), PlaylistDTO.class);
    }

    public PlaylistDTO getPlaylist(Long playlistId) {
        return getObjectByJson(sendGetRequest("/javabot/playlist/get-by-id/" + playlistId), PlaylistDTO.class);
    }

    /**
     * {@link HistoryEntryDTO}
     */

    public boolean createHistoryEntry(HistoryEntryDTO historyEntryDTO) {
        return parseBoolean(sendPutRequest("/history/create", getJsonByObject(historyEntryDTO)));
    }

    /**
     *  {@link VoiceChannelOnlineSessionDTO}
     */

    public void addVoiceChannelOnlineSessionToUser(VoiceChannelOnlineSessionDTO session) {
        String jsonRequest = getJsonByObject(session);
        sendPutRequest("/users/voice-session", jsonRequest);
    }

    public long getVoiceSessionSecondsForTimePeriod(Long userId, VoiceChannelOnlineSessionDTO.TimePeriod timePeriod) {
        String uri = format("/users/voice-session/%d?timePeriod=%s", userId, timePeriod.name());
        String response = sendGetRequest(uri);

        return ofNullable(getObjectByJson(response, Long.class)).orElse(-1L);
    }

    public long getVoiceSessionSecondsForTimePeriodAndGuild(Long userId, VoiceChannelOnlineSessionDTO.TimePeriod timePeriod,
                                                            Long guildId) {
        String uri = format("/users/voice-session-for-guild/%d?timePeriod=%s&guildId=%d",
                userId, timePeriod, guildId);
        String response = sendGetRequest(uri);

        return ofNullable(getObjectByJson(response, Long.class)).orElse(-1L);
    }

    public long getVoiceSessionSecondsForTimePeriodAndChannelInGuild(Long userId, VoiceChannelOnlineSessionDTO.TimePeriod timePeriod,
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
