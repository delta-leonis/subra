package io.leonis.subra.game.data;

import io.leonis.algieba.spatial.Moving;
import io.leonis.algieba.statistic.*;
import io.leonis.zosma.game.Agent;
import java.util.Set;
import lombok.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

public interface MovingPlayer extends Player, Moving {

  /**
   * @return The X-velocity coordinate of the {@link Agent}.
   */
  default double getXVelocity() {
    return this.getState().getMean().getDouble(4, 0);
  }

  /**
   * @return The Y-velocity coordinate of the {@link Agent}.
   */
  default double getYVelocity() {
    return this.getState().getMean().getDouble(5, 0);
  }

  /**
   * @return The orientation velocity of the {@link Agent}.
   */
  default double getOrientationVelocity() {
    return this.getState().getMean().getDouble(6, 0);
  }

  @Override
  default INDArray getVelocity() {
    return this.getState().getMean().get(NDArrayIndex.interval(4, 7), NDArrayIndex.all());
  }

  interface SetSupplier {
    Set<MovingPlayer> getPlayers();
  }

  @Value
  @AllArgsConstructor
  class State implements MovingPlayer {
    private final int id;
    private final Distribution state;
    private final TeamColor teamColor;

    public State(
        final int id,
        final double timestamp,
        final double x,
        final double y,
        final double orientation,
        final double velocityX,
        final double velocityY,
        final double velocityR,
        final TeamColor teamColor
    ) {
      this(
          id,
          new SimpleDistribution(Nd4j.create(
              new double[]{
                  timestamp,
                  x,
                  y,
                  orientation,
                  velocityX,
                  velocityY,
                  velocityR
              },
              new int[]{7, 1}), Nd4j.eye(7)),
          teamColor);

    }
  }
}
