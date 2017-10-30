package io.leonis.subra.game.data;

import java.util.Map;
import io.leonis.subra.game.data.*;

/**
 * The Interface Strategy.
 *
 * @author Rimon Oz
 */
public interface Strategy {
  interface Supplier {
    Map<Player, PlayerCommand> getStrategy();
  }
}
