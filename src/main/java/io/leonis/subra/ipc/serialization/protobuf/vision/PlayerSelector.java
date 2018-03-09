package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.subra.game.data.Player;
import io.leonis.subra.game.data.Team.TeamIdentity;
import io.reactivex.functions.Function;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.robocup.ssl.Detection.*;

/**
 * @author jeroen.dejong.
 */
@AllArgsConstructor
public class PlayerSelector implements Function<DetectionFrame, Set<Player>> {
  private final Function<DetectionFrame, List<DetectionRobot>> selector;
  private final TeamIdentity teamIdentity;

  @Override
  public Set<Player> apply(final DetectionFrame detectionFrame) throws Exception {
    return selector.apply(detectionFrame).stream().map(robot ->
        new Player.State(
            robot.getRobotId(),
            detectionFrame.getTCapture(),
            robot.getX(),
            robot.getY(),
            robot.getOrientation(),
            teamIdentity)).collect(Collectors.toSet());
  }
}
