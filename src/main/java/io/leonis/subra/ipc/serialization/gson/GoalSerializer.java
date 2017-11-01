package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import java.lang.reflect.Type;
import io.leonis.subra.game.data.Goal;

/**
 * Class for handling serialization of Goal objects.
 *
 * @author Ryan Meulenkamp
 */
public class GoalSerializer implements JsonSerializer<Goal> {
  @Override
  public JsonElement serialize(
      final Goal src,
      final Type typeOfSrc,
      final JsonSerializationContext context
  ) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("x", src.getX());
    jsonObject.addProperty("y", src.getY());
    jsonObject.addProperty("width", src.getWidth());
    jsonObject.addProperty("depth", src.getDepth());
    jsonObject.add("cardinalDirection", context.serialize(src.getCardinalDirection()));
    jsonObject.add("teamColor", context.serialize(src.getTeamColor()));
    return jsonObject;
  }
}
