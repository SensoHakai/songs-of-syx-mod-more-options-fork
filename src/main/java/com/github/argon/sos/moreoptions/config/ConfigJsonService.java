package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * For saving and loading {@link MoreOptionsConfig} as json
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigJsonService {
    private final static Logger log = Loggers.getLogger(ConfigJsonService.class);

    @Getter(lazy = true)
    private final static ConfigJsonService instance = new ConfigJsonService(PATHS.local().SETTINGS);

    private final PATH savePath;

    public Optional<MoreOptionsConfig> loadConfig() {
        return load(savePath, null);
    }

    public Optional<MoreOptionsConfig> loadConfig(MoreOptionsConfig defaultConfig) {
        return load(savePath, defaultConfig);
    }

    public boolean saveConfig(MoreOptionsConfig config) {
        log.debug("Saving configuration into %s", savePath.get().toString());
        log.trace("CONFIG: %s", config);

        try {
            JsonE configJson = new JsonE();
            configJson.add("VERSION", config.getVersion());
            configJson.add("EVENTS_SETTLEMENT", mapBool(config.getEventsSettlement()));
            configJson.add("EVENTS_WORLD", mapBool(config.getEventsWorld()));
            configJson.add("EVENTS_CHANCE", mapInteger(config.getEventsChance()));
            configJson.add("SOUNDS_AMBIENCE", mapInteger(config.getSoundsAmbience()));
            configJson.add("SOUNDS_SETTLEMENT", mapInteger(config.getSoundsSettlement()));
            configJson.add("SOUNDS_ROOM", mapInteger(config.getSoundsRoom()));
            configJson.add("WEATHER", mapInteger(config.getWeather()));
            configJson.add("BOOSTERS", mapInteger(config.getBoosters()));

            // save file exists?
            Path path;
            if (!savePath.exists(MoreOptionsConfig.FILE_NAME)) {
                path = savePath.create(MoreOptionsConfig.FILE_NAME);
                log.debug("Created new configuration file %s", path);
            } else {
                path = savePath.get(MoreOptionsConfig.FILE_NAME);
            }

            boolean success = configJson.save(path);

            log.debug("Saving to %s was successful? %s", path, success);

            return success;
        } catch (Errors.DataError e) {
            log.warn("Could not save configuration into: %s", e.getMessage());
        } catch (Exception e) {
            log.error("Could not save configuration", e);
        }

        return false;
    }

    public Optional<MoreOptionsConfig> load(PATH path, MoreOptionsConfig defaultConfig) {
        log.debug("Loading json config from %s", path.get());
        if (!path.exists(MoreOptionsConfig.FILE_NAME)) {
            // do not load what's not there
            log.debug("Configuration %s" + File.separator + "%s.txt not present", path.get(), MoreOptionsConfig.FILE_NAME);
            return Optional.empty();
        }

        Path loadPath = path.get(MoreOptionsConfig.FILE_NAME);
        return load(loadPath, defaultConfig);
    }

    public Optional<MoreOptionsConfig> load(Path path, MoreOptionsConfig defaultConfig) {
        Json json;

        try {
            json = new Json(path);
        }  catch (Exception e) {
            log.info("Could not load json config from %s", path.toString(), e);
            return Optional.empty();
        }

        return Optional.of(MoreOptionsConfig.builder()
            .version((json.has("VERSION")) ? json.i("VERSION")
                : MoreOptionsConfig.VERSION)

            .eventsWorld((json.has("EVENTS_WORLD")) ? mapBool(json.json("EVENTS_WORLD"))
                : (defaultConfig != null) ? defaultConfig.getEventsWorld() : new HashMap<>())

            .eventsSettlement((json.has("EVENTS_SETTLEMENT")) ? mapBool(json.json("EVENTS_SETTLEMENT"))
                : (defaultConfig != null) ? defaultConfig.getEventsSettlement() : new HashMap<>())

            .eventsChance((json.has("EVENTS_CHANCE")) ? mapInteger(json.json("EVENTS_CHANCE"), 0, 1000)
                : (defaultConfig != null) ? defaultConfig.getEventsChance() : new HashMap<>())

            .soundsAmbience((json.has("SOUNDS_AMBIENCE")) ? mapInteger(json.json("SOUNDS_AMBIENCE"), 0, 100)
                : (defaultConfig != null) ? defaultConfig.getSoundsAmbience() : new HashMap<>())

            .soundsSettlement((json.has("SOUNDS_SETTLEMENT")) ? mapInteger(json.json("SOUNDS_SETTLEMENT"), 0 , 100)
                : (defaultConfig != null) ? defaultConfig.getSoundsSettlement() : new HashMap<>())

            .soundsRoom((json.has("SOUNDS_ROOM")) ? mapInteger(json.json("SOUNDS_ROOM"), 0 , 100)
                : (defaultConfig != null) ? defaultConfig.getSoundsRoom() : new HashMap<>())

            .weather((json.has("WEATHER")) ? mapInteger(json.json("WEATHER"), 0, 100)
                : (defaultConfig != null) ? defaultConfig.getWeather() : new HashMap<>())

            .boosters((json.has("BOOSTERS")) ? mapInteger(json.json("BOOSTERS"), 0, 100)
                : (defaultConfig != null) ? defaultConfig.getBoosters() : new HashMap<>())
            .build());
    }


    private Map<String, Boolean> mapBool(Json json) {
        Map<String, Boolean> configs = new HashMap<>();

        for (String key : json.keys()) {
            boolean value = json.bool(key, true);
            configs.put(key, value);
        }

        return configs;
    }

    private JsonE mapBool(Map<String, Boolean> config) {
        JsonE json = new JsonE();
        config.forEach(json::add);

        return json;
    }

    private Map<String, Integer> mapInteger(Json json, int min, int max) {
        Map<String, Integer> configs = new HashMap<>();

        for (String key : json.keys()) {
            int value = json.i(key, min, max);
            configs.put(key, value);
        }

        return configs;
    }

    private JsonE mapInteger(Map<String, Integer> config) {
        JsonE json = new JsonE();
        config.forEach(json::add);

        return json;
    }
}
