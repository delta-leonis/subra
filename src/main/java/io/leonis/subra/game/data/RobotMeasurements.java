package io.leonis.subra.game.data;

import io.leonis.subra.game.data.Player.PlayerIdentity;
import java.util.Map;
import lombok.Value;

/**
 * The Class RobotMeasurements.
 *
 * This class represents a collection of measurements for a {@link PlayerIdentity specific robot}.
 *
 * @author Rimon Oz
 */
public interface RobotMeasurements {
  PlayerIdentity getPlayerIdentity();
  Map<String, Double> getMeasurements();

  interface Supplier {
    RobotMeasurements getRobotMeasurements();
  }

  @Value
  class State implements RobotMeasurements {
    private final PlayerIdentity playerIdentity;
    private final Map<String, Double> measurements;
  }
}
