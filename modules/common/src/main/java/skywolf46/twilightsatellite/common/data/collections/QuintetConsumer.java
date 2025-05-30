package skywolf46.twilightsatellite.common.data.collections;

@FunctionalInterface
public interface QuintetConsumer<T1, T2, T3, T4, T5> {
	void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
}
