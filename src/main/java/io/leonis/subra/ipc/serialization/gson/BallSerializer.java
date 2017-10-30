package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import java.lang.reflect.Type;
import io.leonis.subra.game.data.Ball;

/**
 * Class for handling serialization of Ball objects.
 *
 * @author Ryan Meulenkamp
 */
final class BallSerializer implements JsonSerializer<Ball> {
  @Override
  public JsonElement serialize(
      final Ball ball,
      final Type typeOfSrc,
      final JsonSerializationContext context
  ) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("timestamp", ball.getTimestamp());
    jsonObject.addProperty("x", ball.getX());
    jsonObject.addProperty("y", ball.getY());
    jsonObject.addProperty("z", ball.getZ());
    jsonObject.addProperty("xVelocity", ball.getXVelocity());
    jsonObject.addProperty("yVelocity", ball.getYVelocity());
    jsonObject.addProperty("zVelocity", ball.getZVelocity());
    return jsonObject;
  }
}