package io.leonis.subra.game.data;

import io.leonis.subra.game.data.Player.PlayerIdentity;
import java.util.Map;
import lombok.Value;

/**
 * The Class PlayerMeasurements.
 *
 * This class represents a collection of measurements for a {@link PlayerIdentity specific robot}.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 */
public interface PlayerMeasurements {

  /**
   * @return The identity of the player.
   */
  PlayerIdentity getPlayerIdentity();

  /**
   * @return Measurements identified by a unique string.
   */
  Map<String, Double> getMeasurements();

  /**
   * The Interface Supplier.
   *
   * Supplies a {@link PlayerMeasurements}.
   *
   * @author Jeroen de Jong
   */
  interface Supplier {

    /**
     * @return The measurements of a specific player.
     */
    PlayerMeasurements getPlayerMeasurements();
  }

  @Value
  class State implements PlayerMeasurements {
    private final PlayerIdentity playerIdentity;
    private final Map<String, Double> measurements;
  }
}
