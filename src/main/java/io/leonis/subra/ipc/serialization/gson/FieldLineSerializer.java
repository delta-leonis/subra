package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import java.lang.reflect.Type;
import io.leonis.subra.game.data.FieldLine;

/**
 * Class for handling serialization of FieldLine objects.
 *
 * @author Ryan Meulenkamp
 */
public class FieldLineSerializer implements JsonSerializer<FieldLine> {
  @Override
  public JsonElement serialize(
      final FieldLine field,
      final Type type,
      final JsonSerializationContext context
  ) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("xStart", field.getXStart());
    jsonObject.addProperty("yStart", field.getYStart());
    jsonObject.addProperty("xEnd", field.getXEnd());
    jsonObject.addProperty("yEnd", field.getYEnd());
    jsonObject.addProperty("thickness", field.getThickness());
    return jsonObject;
  }
}
