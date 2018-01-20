package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Set;
import lombok.Value;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Geometry.GeometryData;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class GeometryDataDeducer.
 *
 * This class represents a {@link Deducer} of {@link GeometryData} from {@link WrapperPacket}.
 *
 * @author Rimon Oz
 */
public class GeometryDataDeducer implements Deducer<WrapperPacket, GeometryData> {
  @Override
  public Publisher<GeometryData> apply(final Publisher<WrapperPacket> detectionFramePublisher) {
    return Flux.from(detectionFramePublisher)
        .filter(WrapperPacket::hasGeometry)
        .map(WrapperPacket::getGeometry);
  }

  @Value
  public static class Geometry implements Field.Supplier, Goal.SetSupplier {
    private final Field field;
    private final Set<Goal> goals;
  }
}
