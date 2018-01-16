package io.leonis.subra.game.engine;

import io.leonis.subra.protocol.Robot;
import io.leonis.zosma.function.LambdaExceptions;
import io.leonis.zosma.game.engine.Deducer;
import java.net.DatagramPacket;
import java.util.Arrays;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * The Class RobotMeasurementsDeducer.
 *
 * This class represents a {@link Deducer} which converts {@link DatagramPacket} to
 * {@link Robot.Measurements}.
 *
 * @author Rimon Oz
 */
public class RobotMeasurementsDeducer implements Deducer<DatagramPacket, Robot.Measurements> {
  @Override
  public Publisher<Robot.Measurements> apply(
      final Publisher<DatagramPacket> datagramPacketPublisher
  ) {
    return Flux.from(datagramPacketPublisher)
        .map(datagramPacket ->
            Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()))
        .map(LambdaExceptions.rethrowFunction(Robot.Measurements::parseFrom))
        // get rid of empty measurements
        .filter(measurementsList -> !measurementsList.getMeasurementsList().isEmpty());
  }
}
