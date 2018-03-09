package io.leonis.subra.game.engine;

import io.leonis.algieba.spatial.*;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Strategy.Supplier;
import io.leonis.subra.game.data.Team.TeamIdentity;
import io.reactivex.functions.Function3;
import java.util.Set;
import java.util.stream.*;
import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * The Class PotentialFieldDeducer.
 *
 * @author Rimon Oz
 */
@AllArgsConstructor
public final class PotentialFieldDeducer
    implements Function3<Set<MovingPlayer>, Set<MovingBall>, Field, Strategy.Supplier> {

  private final TeamIdentity teamIdentity;
  private final INDArray origin;
  private final java.util.function.Function<MovingPlayer, PotentialField> playerFieldGenerator;
  private final java.util.function.Function<MovingBall, PotentialField> ballFieldGenerator;
  private final java.util.function.BiFunction<MovingPlayer, PotentialField, PlayerCommand> commandGenerator;

  @Override
  public Supplier apply(final Set<MovingPlayer> players, final Set<MovingBall> balls,
      final Field field) {
    return () -> players.stream()
        .filter(player -> player.getTeamIdentity().equals(teamIdentity))
        .collect(Collectors.toMap(
            Player::getIdentity,
            player -> this.commandGenerator.apply(player,
                new AggregatedPotentialField(
                    this.origin,
                    Stream.concat(
                        players.stream().map(this.playerFieldGenerator),
                        balls.stream().map(this.ballFieldGenerator))
                        .collect(Collectors.toSet())))));
  }
}