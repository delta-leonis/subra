package io.leonis.subra.game.data;

import org.robocup.ssl.Geometry.GeometryData;

/**
 * The Interface DetectionSupplier.
 *
 * Contains a {@link GeometryData}.
 * 
 * @author Jeroen de Jong
 */
public interface GeometrySupplier {

  /**
   * @return The robocup ssl detection packet.
   */
  GeometryData getGeometry();
}
