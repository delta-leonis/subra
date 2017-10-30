package io.leonis.subra.game.data;

import java.io.Serializable;
import java.util.Set;
import lombok.Value;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;
import io.leonis.algieba.Spatial;
import io.leonis.algieba.geometry.CardinalDirection;

/**
 * The Interface Goal.
 *
 * This interface describes the functionality of a goal.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 * @author Ryan Meulenkamp
 */
public interface Goal extends Spatial, Serializable {
  /**
   * @return The {@link CardinalDirection} to which the back of the goal is facing.
   */
  CardinalDirection getCardinalDirection();

  /**
   * @return The {@link TeamColor color} of the goal.
   */
  TeamColor getTeamColor();

  /**
   * @return The width of the goal in mm.
   */
  default double getWidth() {
    return this.getState().getDouble(0, 0);
  }

  /**
   * @return The {@link INDArray state vector} of the goal.
   */
  INDArray getState();

  /**
   * @return The depth of the goal in mm.
   */
  default double getDepth() {
    return this.getState().getDouble(1, 0);
  }

  @Override
  default INDArray getPosition() {
    return this.getState().get(NDArrayIndex.interval(2, 4), NDArrayIndex.all());
  }

  /**
   * @return The X-position coordinate of the {@link Goal}.
   */
  default double getX() {
    return this.getState().getDouble(2, 0);
  }

  /**
   * @return The Y-position coordinate of the {@link Goal}.
   */
  default double getY() {
    return this.getState().getDouble(3, 0);
  }

  interface SetSupplier {
    Set<Goal> getGoals();
  }

  @Value
  class State implements Goal {
    private final INDArray state;
    private final TeamColor teamColor;
    private final CardinalDirection cardinalDirection;
  }
}
