package skywolf46.twilightsatellite.common.data.collections;

@FunctionalInterface
public interface TrioConsumer<T1, T2, T3> {
	void accept(T1 t1, T2 t2, T3 t3);
}
