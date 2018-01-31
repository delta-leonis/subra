package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.algieba.geometry.*;
import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.serialization.protobuf.vision.GeometryDeducer.GeometryFrame;
import io.leonis.zosma.game.engine.Deducer;
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
public class GeometryDeducer implements Deducer<WrapperPacket, GeometryFrame> {

  @Override
  public Publisher<GeometryFrame> apply(final Publisher<WrapperPacket> detectionFramePublisher) {
    return Flux.from(detectionFramePublisher)
        .filter(WrapperPacket::hasGeometry)
        .map(WrapperPacket::getGeometry)
        .map(geom ->
            new GeometryFrame(
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
                new GoalDimension.State(
                    Vectors.columnVector(
                        geom.getField().getGoalWidth(),
                        geom.getField().getGoalDepth()))));
  }

  @Value
  public static class GeometryFrame implements Field.Supplier, GoalDimension.Supplier {
    private final Field field;
    private final GoalDimension goalDimension;
  }
}
