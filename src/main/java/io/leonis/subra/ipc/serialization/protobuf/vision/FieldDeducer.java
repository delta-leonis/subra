package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Geometry.GeometryData;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class FieldDeducer
 *
 * Reads the {@link Field field} from a {@link org.robocup.ssl.Geometry.GeometryFieldSize}
 *
 * @author Jeroen de Jong
 * @author Rimon Oz
 */
@AllArgsConstructor
public class FieldDeducer implements Deducer<WrapperPacket, Field> {
  @Override
  public Publisher<Field> apply(final Publisher<WrapperPacket> visionPublisher) {
    return Flux.from(visionPublisher)
        .map(WrapperPacket::getGeometry)
        .map(GeometryData::getField)
        .map(input ->
            new Field.State(
                input.getFieldWidth(),
                input.getFieldLength(),
                input.getFieldLinesList().stream()
                    .map(fieldLineSegment ->
                        new FieldLine.State(
                            fieldLineSegment.getP1().getX(),
                            fieldLineSegment.getP1().getY(),
                            fieldLineSegment.getP2().getX(),
                            fieldLineSegment.getP2().getY(),
                            fieldLineSegment.getThickness()))
                    .collect(Collectors.toSet()),
                input.getFieldArcsList().stream()
                    .map(fieldCircularArc ->
                        new FieldArc.State(
                            fieldCircularArc.getCenter().getX(),
                            fieldCircularArc.getCenter().getY(),
                            fieldCircularArc.getA1(),
                            fieldCircularArc.getA2(),
                            fieldCircularArc.getThickness(),
                            fieldCircularArc.getRadius()))
                    .collect(Collectors.toSet())));
  }
}
