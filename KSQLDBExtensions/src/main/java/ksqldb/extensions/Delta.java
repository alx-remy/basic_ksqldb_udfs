package ksqldb.extensions;

import java.util.LinkedList;
import java.util.List;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafFactory;
import io.confluent.ksql.function.udaf.UdafDescription;

@UdafDescription(name = "delta",
                author = "alr",
                version = "0.0.0",
                description = "Difference between two successive values")
public class Delta {

	private Delta() {}

    @UdafFactory(description = "Returns the difference between two successive column values of a stream")
    public static Udaf<Double, List<Double>, Double> createUdaf() {
        return new DeltaImpl();
    }

	private static class DeltaImpl implements Udaf<Double, List<Double>, Double> {
		
		@Override
		public List<Double> initialize() {
			LinkedList<Double> bufferList = new LinkedList<Double>();
			bufferList.add(0.0);
			bufferList.add(0.0);
			
			return bufferList;
		}
	
		@Override
		public List<Double> aggregate(Double newValue, List<Double> bufferList) {
			bufferList.remove(0);
			bufferList.add(newValue);
			return bufferList;
		}
	
		@Override
		public Double map(List<Double> bufferList) {
			return bufferList.get(1)-bufferList.get(0);
		}
	
		@Override
		public List<Double> merge(List<Double> aggOne, List<Double> aggTwo) {
			return aggTwo;
		}
	}
}