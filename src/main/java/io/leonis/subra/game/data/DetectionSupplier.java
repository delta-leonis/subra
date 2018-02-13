package io.leonis.subra.game.data;

import org.robocup.ssl.Detection.DetectionFrame;

/**
 * The Interface DetectionSupplier.
 *
 * Contains a {@link DetectionFrame}.
 * 
 * @author Jeroen de Jong
 */
public interface DetectionSupplier {

  /**
   * @return The robocup ssl detection packet.
   */
  DetectionFrame getDetection();
}
