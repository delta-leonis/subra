package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import io.leonis.subra.game.data.Player;
import java.lang.reflect.Type;

/**
 * Class for handling serialization of Player objects.
 *
 * @author Ryan Meulenkamp
 */
public class PlayerSerializer implements JsonSerializer<Player> {
  @Override
  public JsonElement serialize(
      final Player src,
      final Type typeOfSrc,
      final JsonSerializationContext context
  ) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.add("teamColor", context.serialize(src.getTeamColor()));
    jsonObject.add("x", context.serialize(src.getX()));
    jsonObject.add("y", context.serialize(src.getY()));
    jsonObject.addProperty("timeStamp", src.getTimestamp());
    jsonObject.addProperty("orientation", src.getOrientation());
    jsonObject.addProperty("orientationVelocity", src.getOrientationVelocity());
    jsonObject.addProperty("id", src.getId());
    return jsonObject;
  }
}
