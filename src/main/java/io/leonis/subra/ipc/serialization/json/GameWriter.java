package io.leonis.subra.ipc.serialization.json;

import com.google.gson.*;
import io.leonis.subra.ipc.serialization.gson.*;
import java.util.function.Function;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Goal.SetSupplier;
import io.leonis.algieba.Temporal;

/**
 * @author Jeroen de Jong
 */
public class GameWriter<G extends Player.SetSupplier & SetSupplier & Field.Supplier & Ball.SetSupplier & Referee.Supplier & Temporal>
    implements Function<G, String> {
  private final Gson gson;

  public GameWriter() {
    this.gson = new GsonBuilder()
        .registerTypeAdapter(Player.class, new MovingPlayerSerializer())
        .registerTypeAdapter(FieldArc.class, new FieldArcSerializer())
        .registerTypeAdapter(FieldLine.class, new FieldLineSerializer())
        .registerTypeAdapter(Field.class, new FieldSerializer())
        .registerTypeAdapter(Referee.class, new RefereeSerializer())
        .registerTypeAdapter(Team.class, new TeamSerializer())
        .registerTypeAdapter(Goal.class, new GoalSerializer())
        .create();
  }

  @Override
  public String apply(final G game) {
    return gson.toJson(game);
  }
}
