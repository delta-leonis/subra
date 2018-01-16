package io.leonis.subra.ipc.serialization.protobuf;

import com.google.protobuf.Message;
import io.leonis.zosma.game.engine.Deducer;
import java.net.DatagramPacket;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * The class DatagramDeducer.
 *
 * Deduces a protobuf {@link Message} to a {@link DatagramPacket}.
 *
 * @author Jeroen de Jong
 */
public class DatagramDeducer implements Deducer<Message, DatagramPacket> {

  @Override
  public Publisher<DatagramPacket> apply(final Publisher<Message> messagePublisher) {
    return Flux.from(messagePublisher)
        .map(message -> new DatagramPacket(message.toByteArray(), message.getSerializedSize()));
  }
}
