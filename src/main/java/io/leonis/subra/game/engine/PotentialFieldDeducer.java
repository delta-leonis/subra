package io.leonis.subra.game.engine;

import io.leonis.subra.game.data.Strategy;
import io.leonis.subra.game.data.Strategy.Supplier;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import io.leonis.zosma.game.engine.Deducer;
import java.util.stream.*;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.reactivestreams.Publisher;
import io.leonis.subra.game.data.*;
import io.leonis.algieba.spatial.*;
import reactor.core.publisher.Flux;

/**
 * The Class PotentialFieldDeducer.
 *
 * @author Rimon Oz
 */
@Value
public class PotentialFieldDeducer<G extends Player.SetSupplier & Field.Supplier & Ball.SetSupplier>
    implements Deducer<G, Supplier> {
  private final TeamColor teamColor;
  private final INDArray origin;
  private final double opponentSourceScale;
  private final double allySourceScale;
  private final double ballSinkScale;

  @Override
  public Publisher<Strategy.Supplier> apply(
      final Publisher<G> inputPublisher
  ) {
    return Flux.from(inputPublisher)
        .map(game -> {
          final AggregatedPotentialField aggregatedPotentialField = new AggregatedPotentialField(
              this.origin,
              Stream.concat(
                  game.getAgents().stream()
                      .map(player -> new GaussianPotentialField(
                          player.getPosition(),
                          player.getTeamColor().equals(this.getTeamColor())
                              ? getAllySourceScale()
                              : getOpponentSourceScale(),
                          false)),
                  game.getBalls().stream()
                      .map(ball ->
                          new GaussianPotentialField(
                              ball.getPosition(),
                              this.getBallSinkScale(),
                              true)))
                  .collect(Collectors.toSet()));

          return () -> game.getAgents().stream()
              .filter(player -> player.getTeamColor().equals(this.getTeamColor()))
              .collect(Collectors.toMap(
                  Player::getIdentity,
                  player -> new PlayerCommand.State(
                      aggregatedPotentialField.getForce(player.getPosition()),
                      0,
                      0,
                      0)));
        });
  }
}
