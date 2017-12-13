package io.leonis.subra.game.data;

import io.leonis.subra.game.data.Player.PlayerIdentity;
import java.util.Map;

/**
 * The Interface Strategy.
 *
 * @author Rimon Oz
 */
public interface Strategy {
  interface Supplier {
    Map<PlayerIdentity, PlayerCommand> getStrategy();
  }
}
