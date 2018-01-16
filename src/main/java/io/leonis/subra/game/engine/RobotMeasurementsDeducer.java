package io.leonis.subra.game.engine;

import io.leonis.subra.game.data.Player.PlayerIdentity;
import io.leonis.subra.game.data.*;
import io.leonis.subra.protocol.Robot;
import io.leonis.subra.protocol.Robot.Measurements;
import io.leonis.zosma.function.LambdaExceptions;
import io.leonis.zosma.game.engine.Deducer;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Value;
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
@Value
public class RobotMeasurementsDeducer implements Deducer<DatagramPacket, RobotMeasurements> {
  private final TeamColor color;

  @Override
  public Publisher<RobotMeasurements> apply(
      final Publisher<DatagramPacket> datagramPacketPublisher
  ) {
    return Flux.from(datagramPacketPublisher)
        .map(datagramPacket ->
            Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()))
        .map(LambdaExceptions.rethrowFunction(Robot.Measurements::parseFrom))
        // get rid of empty measurements
        .filter(measurementsList -> !measurementsList.getMeasurementsList().isEmpty())
        // and put the measurements in a map
        .map(measurements ->
            new RobotMeasurements(
                new PlayerIdentity(measurements.getRobotId(), this.color),
                measurements.getMeasurementsList().stream()
                    .collect(Collectors.toMap(
                        Measurements.Single::getLabel,
                        measurement ->
                            measurement.getValue()
                                * Math.pow(10, measurement.getTenFoldMultiplier())))));
  }
}