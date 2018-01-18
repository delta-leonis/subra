package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Set;
import java.util.stream.*;
import org.nd4j.linalg.factory.Nd4j;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Geometry.*;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class GoalsDeducer
 *
 * Reads all goaldata from a {@link GeometryFieldSize}.
 *
 * @author Jeroen de Jong
 */
public class GoalsDeducer implements Deducer<WrapperPacket, Set<Goal>> {
  @Override
  public Publisher<Set<Goal>> apply(final Publisher<WrapperPacket> geometryPublisher) {
    return Flux.from(geometryPublisher)
        .filter(WrapperPacket::hasGeometry)
        .map(WrapperPacket::getGeometry)
        .filter(GeometryData::hasField)
        .map(GeometryData::getField)
        .map(input -> Stream.of(input)
            .flatMap(packet -> Stream.of(
                this.createGoal(TeamColor.BLUE, CardinalDirection.NORTH, packet),
                this.createGoal(TeamColor.YELLOW, CardinalDirection.SOUTH, packet)))
            .collect(Collectors.toSet()));
  }

  private Goal createGoal(
      final TeamColor teamColor,
      final CardinalDirection direction,
      final GeometryFieldSize geometryData
  ) {
    switch (direction) {
      case NORTH:
        return new Goal.State(
            Nd4j.create(
                new float[]{
                    geometryData.getGoalWidth(),
                    geometryData.getGoalDepth(),
                    ((geometryData.getFieldLength() + geometryData.getGoalDepth()) / 2f),
                    0
                }),
            teamColor,
            direction);
      case SOUTH:
        return new Goal.State(
            Nd4j.create(
                new float[]{
                    geometryData.getGoalWidth(),
                    geometryData.getGoalDepth(),
                    -1f * ((geometryData.getFieldLength() + geometryData.getGoalDepth()) / 2f),
                    0
                }),
            teamColor,
            direction);
      case EAST:
        return new Goal.State(
            Nd4j.create(
                new float[]{
                    geometryData.getGoalWidth(),
                    geometryData.getGoalDepth(),
                    0,
                    ((geometryData.getFieldWidth() + geometryData.getGoalWidth()) / 2f)
                }),
            teamColor,
            direction);
      case WEST:
        return new Goal.State(
            Nd4j.create(
                new float[]{
                    geometryData.getGoalWidth(),
                    geometryData.getGoalDepth(),
                    0,
                    -1f * ((geometryData.getFieldWidth() + geometryData.getGoalWidth()) / 2f)
                }),
            teamColor,
            direction);
      default:
        return new Goal.State(Nd4j.zeros(1, 4), TeamColor.NONE, CardinalDirection.NORTH);
    }
  }
}
