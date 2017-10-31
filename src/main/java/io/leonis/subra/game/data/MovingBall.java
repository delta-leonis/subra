package io.leonis.subra.game.data;

import io.leonis.algieba.spatial.Moving;
import io.leonis.algieba.statistic.*;
import java.util.Set;
import lombok.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

public interface MovingBall extends Ball, Moving {

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
  default INDArray getVelocity() {
    return this.getState().getMean().get(NDArrayIndex.interval(4, 7), NDArrayIndex.all());
  }

  interface SetSupplier {
    Set<MovingBall> getBalls();
  }

  interface Supplier {
    MovingBall getBall();
  }


  @Value
  @AllArgsConstructor
  class State implements MovingBall {
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
