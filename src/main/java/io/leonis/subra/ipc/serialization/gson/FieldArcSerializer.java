package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import java.lang.reflect.Type;
import io.leonis.subra.game.data.FieldArc;

/**
 * Class for handling serialization of FieldArc objects.
 *
 * @author Ryan Meulenkamp
 */
public final class FieldArcSerializer implements JsonSerializer<FieldArc> {
  @Override
  public JsonElement serialize(
      final FieldArc field,
      final Type type,
      final JsonSerializationContext context
  ) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("x", field.getX());
    jsonObject.addProperty("y", field.getY());
    jsonObject.addProperty("startAngle", field.getStartAngle());
    jsonObject.addProperty("stopAngle", field.getStopAngle());
    jsonObject.addProperty("thickness", field.getThickness());
    jsonObject.addProperty("radius", field.getRadius());
    return jsonObject;
  }
}
