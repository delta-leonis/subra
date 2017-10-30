package io.leonis.subra.game.data;

import java.io.Serializable;
import java.util.Set;
import lombok.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import io.leonis.zosma.game.Agent;
import io.leonis.algieba.*;
import io.leonis.algieba.geometry.Orientation;
import io.leonis.algieba.spatial.Moving;
import io.leonis.algieba.statistic.*;

/**
 * The Interface Player.
 *
 * This interface represents a {@link Agent robot} in the  of a Small Size League game.
 *
 * @author Rimon Oz
 */
public interface Player extends Spatial, Moving, Orientation, Agent, Temporal, Serializable {

  /**
   * @return The {@link TeamColor} of the {@link Team} to which this agent belongs.
   */
  TeamColor getTeamColor();

  default double getX() {
    return this.getState().getMean().getDouble(1, 0);
  }

  /**
   * @return The {@link Distribution state distribution} of the {@link Player}.
   */
  Distribution getState();

  /**
   * @return The Y-position coordinate of the {@link Agent}.
   */
  default double getY() {
    return this.getState().getMean().getDouble(2, 0);
  }

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

  /**
   * @return The orientation of the {@link Agent}.
   */
  default double getOrientation() {
    return this.getState().getMean().getDouble(3, 0);
  }

  @Override
  default INDArray getPosition() {
    return this.getState().getMean().get(NDArrayIndex.interval(1, 3), NDArrayIndex.all());
  }

  @Override
  default INDArray getVelocity() {
    return this.getState().getMean().get(NDArrayIndex.interval(4, 6), NDArrayIndex.all());
  }

  @Override
  default long getTimestamp() {
    // fixed point conversion
    return Math.round(1000000L * this.getState().getMean().getDouble(0, 0));
  }

  interface SetSupplier extends Agent.SetSupplier<Player> {
    Set<Player> getAgents();
  }

  @Value
  @AllArgsConstructor
  class State implements Player {
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
