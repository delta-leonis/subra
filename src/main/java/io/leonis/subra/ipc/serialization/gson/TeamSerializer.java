package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import java.lang.reflect.Type;
import io.leonis.subra.game.data.Team;

/**
 * Class for handling serialization of Team objects.
 *
 * @author Ryan Meulenkamp
 */
public class TeamSerializer implements JsonSerializer<Team> {
  @Override
  public JsonElement serialize(
      final Team src,
      final Type typeOfSrc,
      final JsonSerializationContext context
  ) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("name", src.getName());
    jsonObject.addProperty("score", src.getScore());
    jsonObject.addProperty("redCardCount", src.getRedCardCount());
    jsonObject.addProperty("yellowCardCount", src.getYellowCardCount());
    jsonObject.add("yellowCards", context.serialize(src.getYellowCards()));
    jsonObject.addProperty("timeOutsLeft", src.getTimeOutsLeft());
    jsonObject.addProperty("goalieNumber", src.getGoalieNumber());
    return jsonObject;
  }
}
