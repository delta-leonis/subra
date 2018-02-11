package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.algieba.geometry.CardinalDirection;
import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.serialization.protobuf.vision.GeometryDeducer.Geometry;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Set;
import java.util.stream.*;
import lombok.Value;
import org.nd4j.linalg.factory.Nd4j;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Geometry.*;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class GeometryDeducer.
 *
 * This class represents a {@link Deducer} of {@link GeometryData} from {@link WrapperPacket}.
 *
 * @author Rimon Oz
 */
public class GeometryDeducer implements Deducer<WrapperPacket, Geometry> {
  @Override
  public Publisher<Geometry> apply(final Publisher<WrapperPacket> detectionFramePublisher) {
    return Flux.from(detectionFramePublisher)
        .map(WrapperPacket::getGeometry)
        .map(geom ->
            new Geometry(
                new Field.State(
                    geom.getField().getFieldWidth(),
                    geom.getField().getFieldLength(),
                    geom.getField().getFieldLinesList().stream()
                        .map(fieldLineSegment ->
                            new FieldLine.State(
                                fieldLineSegment.getP1().getX(),
                                fieldLineSegment.getP1().getY(),
                                fieldLineSegment.getP2().getX(),
                                fieldLineSegment.getP2().getY(),
                                fieldLineSegment.getThickness()))
                        .collect(Collectors.toSet()),
                    geom.getField().getFieldArcsList().stream()
                        .map(fieldCircularArc ->
                            new FieldArc.State(
                                fieldCircularArc.getCenter().getX(),
                                fieldCircularArc.getCenter().getY(),
                                fieldCircularArc.getA1(),
                                fieldCircularArc.getA2(),
                                fieldCircularArc.getThickness(),
                                fieldCircularArc.getRadius()))
                        .collect(Collectors.toSet())),
                Stream.of(geom)
                    .flatMap(packet -> Stream.of(
                        this.createGoal(TeamColor.BLUE, CardinalDirection.NORTH, packet.getField()),
                        this.createGoal(TeamColor.YELLOW, CardinalDirection.SOUTH, packet.getField())))
                    .collect(Collectors.toSet())));
  }

  @Value
  public static class Geometry implements Field.Supplier, Goal.SetSupplier {
    private final Field field;
    private final Set<Goal> goals;
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
