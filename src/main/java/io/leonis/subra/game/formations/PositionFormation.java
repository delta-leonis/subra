package io.leonis.subra.game.formations;

import io.leonis.subra.game.data.Player;
import io.leonis.subra.game.data.Player.Identity;
import io.leonis.zosma.game.Formation;
import java.util.Map;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * The Interface PositionFormation.
 *
 * This interface describes the functionality of a formation in which {@link Player SSL robots}
 * are assigned positions on the field.
 *
 * @author Rimon Oz
 */
@Value
@Slf4j
public class PositionFormation implements Formation<Player.Identity, INDArray> {
  private final Map<Player.Identity, INDArray> positions;

  @Override
  public INDArray getFormationFor(final Player.Identity player) {
    return this.getPositions().get(player);
  }
}
