package io.leonis.subra.ipc.serialization.json;

import com.google.gson.*;
import io.leonis.subra.ipc.serialization.gson.PlayerCommandSerializer;
import java.util.function.Function;
import io.leonis.subra.game.data.*;

/**
 * @author Jeroen de Jong
 */
public class PlayerCommandWriter implements Function<PlayerCommand, String> {
  private final Gson gson;

  public PlayerCommandWriter() {
    this.gson = new GsonBuilder()
        .registerTypeAdapter(PlayerCommand.class, new PlayerCommandSerializer())
        .create();
  }

  @Override
  public String apply(final PlayerCommand playerCommand) {
    return this.gson.toJson(playerCommand);
  }
}
