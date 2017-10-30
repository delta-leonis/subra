package io.leonis.subra.game.data;

import java.io.Serializable;
import java.util.Set;
import lombok.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import io.leonis.algieba.*;
import io.leonis.algieba.spatial.Moving;
import io.leonis.algieba.statistic.*;

/**
 * The Interface Ball.
 *
 * This interface describes the functionality of a ball.
 *
 * @author Rimon Oz
 */
public interface Ball extends Spatial, Moving, Temporal, Serializable {

  @Override
  default long getTimestamp() {
    // fixed point conversion
    return Math.round(1000000L * this.getState().getMean().getDouble(0, 0));
  }

  /**
   * @return The {@link Distribution state distribution} of the ball.
   */
  Distribution getState();

  /**
   * @return The X-position coordinate of the {@link Ball}.
   */
  default double getX() {
    return this.getState().getMean().getDouble(1, 0);
  }

  /**
   * @return The Y-position coordinate of the {@link Ball}.
   */
  default double getY() {
    return this.getState().getMean().getDouble(2, 0);
  }

  /**
   * @return The Z-position coordinate of the {@link Ball}.
   */
  default double getZ() {
    return this.getState().getMean().getDouble(3, 0);
  }

  /**
   * @return The X-velocity coordinate of the {@link Ball}.
   */
  default double getXVelocity() {
    return this.getState().getMean().getDouble(4, 0);
  }

  /**
   * @return The Y-velocity coordinate of the {@link Ball}.
   */
  default double getYVelocity() {
    return this.getState().getMean().getDouble(5, 0);
  }

  /**
   * @return The Z-velocity coordinate of the {@link Ball}.
   */
  default double getZVelocity() {
    return this.getState().getMean().getDouble(6, 0);
  }

  @Override
  default INDArray getPosition() {
    return this.getState().getMean().get(NDArrayIndex.interval(1, 3), NDArrayIndex.all());
  }

  @Override
  default INDArray getVelocity() {
    return this.getState().getMean().get(NDArrayIndex.interval(4, 7), NDArrayIndex.all());
  }

  interface SetSupplier {
    Set<Ball> getBalls();
  }

  interface Supplier {
    Ball getBall();
  }

  @Value
  @AllArgsConstructor
  class State implements Ball {
    private final Distribution state;

    public State(
        final double timestamp,
        final double x,
        final double y,
        final double z,
        final double velocityX,
        final double velocityY,
        final double velocityZ
    ) {
      this(new SimpleDistribution(Nd4j.create(
          new double[]{
              timestamp,
              x,
              y,
              z,
              velocityX,
              velocityY,
              velocityZ
          },
          new int[]{7, 1}), Nd4j.eye(7)));
    }
  }
}
