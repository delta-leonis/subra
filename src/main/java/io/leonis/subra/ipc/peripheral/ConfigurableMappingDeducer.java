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
import lombok.experimental.Delegate;
import org.reactivestreams.Publisher;
import reactor.core.publisher.*;

/**
 * @author jeroen.dejong.
 */
@Value
public class ConfigurableMappingDeducer<
    I extends Identity,
    C extends Controller,
    S extends Controller.SetSupplier<C>>
implements Deducer<S, Controller.MapSupplier<I>> {

  private final BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner;
  private final TopicProcessor<MapSupplier<I>> topic;

  public ConfigurableMappingDeducer(
    final Publisher<Controller.MapSupplier<I>> mapPublisher,
    final BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner
  ) {
    this.combiner = combiner;
    this.topic = TopicProcessor.create();
    // publish latest map to topic
    Flux.from(mapPublisher).subscribe(topic::onNext);
  }

  @Override
  public Publisher<MapSupplier<I>> apply(final Publisher<S> setPublisher) {
    return Flux.combineLatest(setPublisher, topic.distinctUntilChanged(MapSupplier::getControllerMapping), KnoepTroep::new)
          .map(c -> c.getControllerSet().stream()
              .reduce(c.getControllerMapping(),
                  combiner,
                  (a, b) -> Stream.of(a.entrySet(), b.entrySet())
                      .flatMap(Collection::stream)
                      .flatMap(entry -> entry.getValue().stream()
                          .map(controller -> new SimpleImmutableEntry<>(entry.getKey(), controller)))
                      .collect(groupingBy(Entry::getKey,
                          mapping(Entry::getValue, toSet())))))
          .<MapSupplier<I>>map(map -> () -> map)
          .doOnNext(topic::onNext);
  }

  @Value
  private static class KnoepTroep<I extends Identity, C extends Controller>
      implements Controller.MapSupplier<I>, Controller.SetSupplier<C> {
    @Delegate
    private final SetSupplier<C> setSupplier;
    @Delegate
    private final MapSupplier<I> mapSupplier;
  }

}
