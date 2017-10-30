package io.leonis.subra.game.rule;

import java.util.Set;
import io.leonis.subra.game.data.*;

/**
 * The Class AgentOutOfBoundsRule.
 *
 * This class describes the rule in Small Size League soccer
 * which determines whether a robot is within the bounds of the field.
 *
 * @author Rimon Oz
 */
public class AgentOutOfBoundsRule<I extends Field.Supplier & Player.SetSupplier>
    implements OutOfBoundsRule<I, Player> {

  @Override
  public Set<Player> getObjectsFromState(final I input) {
    return input.getAgents();
  }

  @Override
  public boolean test(final Field field, final Player possibleViolator) {
    return possibleViolator.getX() > field.getLength() / 2f
        || possibleViolator.getX() < field.getLength() / -2f
        || possibleViolator.getY() > field.getWidth() / 2f
        || possibleViolator.getY() < field.getWidth() / -2f;
  }
}
