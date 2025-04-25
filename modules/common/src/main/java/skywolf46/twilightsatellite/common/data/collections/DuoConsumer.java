package skywolf46.twilightsatellite.common.data.collections;

@FunctionalInterface
public interface DuoConsumer<T1, T2> {
	void accept(T1 t1, T2 t2);
}
