package io.leonis.subra.game.data;

import io.leonis.algieba.Spatial;
import io.leonis.subra.game.data.Team.TeamIdentity;
import java.util.Set;
import lombok.Value;
import lombok.experimental.Delegate;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author Jeroen de Jong
 */
public interface PositionedGoal extends GoalDimension, Spatial, PlayDirection.Supplier {

  /**
   * @return The {@link TeamIdentity} defending to this goal.
   */
  TeamIdentity getTeamIdentity();

  /**
   * @return The X-position coordinate of the {@link GoalDimension}.
   */
  default double getX() {
    return this.getState().getDouble(2, 0);
  }

  /**
   * @return The Y-position coordinate of the {@link GoalDimension}.
   */
  default double getY() {
    return this.getState().getDouble(3, 0);
  }

  interface SetSupplier {
    Set<PositionedGoal> getGoals();
  }

  @Value
  class State implements PositionedGoal {
    @Delegate
    private final GoalDimension dimensions;
    private final INDArray position;
    private final TeamIdentity teamIdentity;
    private final PlayDirection playDirection;
  }
}
