package io.leonis.subra.ipc.serialization.gson;

import com.google.gson.*;
import io.leonis.algieba.Temporal;
import io.leonis.subra.game.data.*;
import java.lang.reflect.Type;

/**
 * @author Jeroen de Jong
 */
public class GameSerializer<G extends Player.SetSupplier & Goal.SetSupplier & Field.Supplier & Ball.SetSupplier & Referee.Supplier & Temporal>
    implements JsonSerializer<G> {
  @Override
  public JsonElement serialize(final G src, final Type typeOfSrc,
      final JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    object.add("agents", context.serialize(src.getAgents()));
    object.add("balls", context.serialize(src.getBalls()));
    object.add("goals", context.serialize(src.getGoals()));
    object.add("field", context.serialize(src.getField()));
    object.add("referee", context.serialize(src.getReferee()));
    object.add("timestamp", context.serialize(src.getTimestamp()));
    return object;
  }
}
