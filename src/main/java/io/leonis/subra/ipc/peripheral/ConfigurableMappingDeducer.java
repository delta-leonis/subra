package io.leonis.subra.ipc.peripheral;

import static java.util.stream.Collectors.*;

import io.leonis.zosma.game.Identity;
import io.leonis.zosma.game.engine.Deducer;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.Controller.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * @author jeroen.dejong.
 */
@Value
public class ConfigurableMappingDeducer<
    I extends Identity,
    C extends Controller,
    S extends Controller.SetSupplier<C>>
implements Deducer<S, Controller.MapSupplier<I>> {

  private final AtomicReference<MapSupplier<I>> currentMapping =
      new AtomicReference<>(Collections::emptyMap);
  private final BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner;

  public ConfigurableMappingDeducer(
      final Publisher<Controller.MapSupplier<I>> mapSource,
      final BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner
  ) {
    // Override current mapping with whatever comes through the publisher.
    Flux.from(mapSource).subscribe(currentMapping::set);
    this.combiner = combiner;
  }

  @Override
  public Publisher<MapSupplier<I>> apply(final Publisher<S> setPublisher) {
    return Flux.from(setPublisher)
          .map(c -> c.getControllerSet().stream()
              .reduce(currentMapping.get().getControllerMapping(),
                  combiner,
                  (a, b) -> Stream.of(a.entrySet(), b.entrySet())
                      .flatMap(Collection::stream)
                      .flatMap(entry -> entry.getValue().stream().map(controller -> new SimpleImmutableEntry<>(entry.getKey(), controller)))
                      .collect(groupingBy(Entry::getKey,
                          mapping(Entry::getValue, toSet())))))
          .<MapSupplier<I>>map(set -> () -> set) // wrap the result in a Controller.MapSupplier
          .doOnNext(currentMapping::set); // update currentMap to contain moved controllers;
  }
}
