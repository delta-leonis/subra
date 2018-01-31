package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import io.leonis.subra.game.data.*;
import java.lang.reflect.Type;

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
    jsonObject.add("fieldHalf", context.serialize(src.getFieldHalf()));
    jsonObject.add("teamIdentity", context.serialize(src.getTeamIdentity()));
    return jsonObject;
  }
}
