package ksqldb.extensions;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

@UdfDescription(name = "modulo",
                author = "alr",
                version = "0.0.0",
                description = "Modulo operator for scalar columns and/or constants")
public class Modulo {

    @Udf(description = "Modulo for integers input.")
    public int modulo(@UdfParameter int v1, @UdfParameter int v2) {
        return (v1 % v2);
    }
    
    @Udf(description = "Modulo for doubles input.")
    public double modulo(@UdfParameter double v1, @UdfParameter double v2) {
        return (v1 % v2);
    }

    @Udf(description = "Modulo for longs input.")
    public long modulo(@UdfParameter long v1, @UdfParameter long v2) {
        return (v1 % v2);
    }
    
    @Udf(description = "Modulo for double & integer input.")
    public double modulo(@UdfParameter double v1, @UdfParameter int v2) {
        return (v1 % v2);
    }
    
    @Udf(description = "Modulo for double & long input.")
    public double modulo(@UdfParameter double v1, @UdfParameter long v2) {
        return (v1 % v2);
    }
    
    @Udf(description = "Modulo for long & integer input.")
    public long modulo(@UdfParameter long v1, @UdfParameter int v2) {
        return (v1 % v2);
    }
}