package io.leonis.subra.game.data;

import io.leonis.algieba.*;
import io.leonis.algieba.statistic.*;
import io.leonis.zosma.game.Agent;
import java.io.Serializable;
import java.util.Set;
import lombok.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

/**
 * The Interface Player.
 *
 * This interface represents a {@link Agent robot} in a Small Size League game.
 *
 * @author Rimon Oz
 */
public interface Player extends Spatial, Agent, Temporal, Serializable {

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
   * @return The orientation of the {@link Agent}.
   */
  default double getOrientation() {
    return this.getState().getMean().getDouble(3, 0);
  }

  @Override
  default INDArray getPosition() {
    return this.getState().getMean().get(NDArrayIndex.interval(1, 4), NDArrayIndex.all());
  }

  @Override
  default long getTimestamp() {
    // fixed point conversion
    return Math.round(1000000L * this.getState().getMean().getDouble(0, 0));
  }

  /**
   * @return The identity for the {@link Player}, containing only its identifier and team color.
   */
  default Player.Identity getIdentity() {
    return new Player.Identity(this.getId(), this.getTeamColor());
  }

  /**
   * @return The {@link TeamColor} of the {@link Team} to which this agent belongs.
   */
  TeamColor getTeamColor();

  interface SetSupplier {
    Set<Player> getPlayers();
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
        final TeamColor teamColor
    ) {
      this(
          id,
          new SimpleDistribution(Nd4j.create(
              new double[]{
                  timestamp,
                  x,
                  y,
                  orientation
              },
              new int[]{4, 1}), Nd4j.eye(4)),
          teamColor);

    }
  }

  @Value
  class Identity implements Agent {
    private final int id;
    private final TeamColor teamColor;
  }
}
