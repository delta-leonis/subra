package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Set;
import java.util.stream.*;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Detection.DetectionRobot;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class PlayersDeducer
 *
 * Reads all agents from a {@link org.robocup.ssl.Detection.DetectionFrame}
 *
 * @author Jeroen de Jong
 */
public class PlayersDeducer implements Deducer<WrapperPacket, Set<Player>> {
  @Override
  public Publisher<Set<Player>> apply(final Publisher<WrapperPacket> detectionFramePublisher) {
    return Flux.from(detectionFramePublisher)
        .filter(WrapperPacket::hasDetection)
        .map(WrapperPacket::getDetection)
        .map(packet ->
            Stream.of(packet)
                .flatMap(input -> Stream.concat(
                    input.getRobotsYellowList().stream()
                        .map(robot -> this.createPlayer(TeamColor.YELLOW, input.getTCapture(), robot)),
                    input.getRobotsBlueList().stream()
                        .map(robot -> this.createPlayer(TeamColor.BLUE, input.getTCapture(), robot))))
                .collect(Collectors.toSet()));
  }

  private Player createPlayer(final TeamColor teamColor, final Double timestamp, final DetectionRobot robotData) {
    return new Player.State(
        robotData.getRobotId(),
        timestamp,
        robotData.getX(),
        robotData.getY(),
        robotData.getOrientation(),
        teamColor);
  }
}
