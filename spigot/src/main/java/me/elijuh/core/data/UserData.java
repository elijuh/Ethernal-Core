package me.elijuh.core.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UserData {
    private final User user;
    private final YamlFile storage;
    private final Map<String, Object> data;

    public UserData(User user) {
        this.user = user;
        this.storage = new YamlFile(user.getPlayer().getUniqueId().toString(), "userdata");
        data = new HashMap<>();
    }

    public boolean isMessageSoundEnabled() {
        if (storage.getConfig().contains("message-sounds")) {
            return storage.getConfig().getBoolean("message-sounds");
        }
        return true;
    }

    public void add(String key, Object object) {
        data.put(key, object);
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
        return (T) data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(final String key, final T def) {
        return (T) data.getOrDefault(key, def);
    }

    @SuppressWarnings("unchecked")
    public <T> T remove(final String key) {
        return (T) data.remove(key);
    }

}
