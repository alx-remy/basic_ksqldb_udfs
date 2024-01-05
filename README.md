# Overview

Here are some java functions I developped for KSQLDB following this guide https://docs.ksqldb.io/en/0.29.0-ksqldb/how-to-guides/create-a-user-defined-function/.

KSQLDB currently lacks many basic SQL functions and I haven't found many community projects actively working on it. I'll try to update this repo with new functions, any contribution is appreciated.

# Content

Project :

```fs
  .
  ├── kafka_dataset_generator.py    - test environment tool (cf. hands-on)
  └── KSQLDBExtensions              - source eclipse project
```

Functions :

```
Name        : DELTA
Overview    : Difference between two successive values
Type        : AGGREGATE
Variations  :
  Variation   : DELTA(val1 DOUBLE)
  Returns     : DOUBLE
```

```
Name        : MODULO
Overview    : Modulo operator for scalar columns and constants
Type        : SCALAR
Variations  :
  Variation   : MODULO(v1 DOUBLE, v2 DOUBLE)
  Returns     : DOUBLE
  Variation   : MODULO(v1 DOUBLE, v2 BIGINT)
  Returns     : DOUBLE
  Variation   : MODULO(v1 DOUBLE, v2 INT)
  Returns     : DOUBLE
  Variation   : MODULO(v1 BIGINT, v2 BIGINT)
  Returns     : BIGINT
  Variation   : MODULO(v1 BIGINT, v2 INT)
  Returns     : BIGINT
  Variation   : MODULO(v1 INT, v2 INT)
  Returns     : INT
```

# Hands-on

You can follow these steps to try these functions (assuming you've got a ksqldb server/cluster and the underlying kafka cluster set-up) :
  1. <details>

      <summary>Compile the project into an uber-jar (using eclipse)</summary>

      1. Enable parameters reflection (gives ksqldb access to our udfs methods parameters which it uses to generate the doc at runtime) :

      ```eclipse_ui
      (SUBWINDOW) Project Explorer > (RCLIC) [Project] > (CLIC) Properties >
        (WINDOW) Properties for [Project] > (SUBWINDOW) Java Compiler >
          (SUBWINDOW) Classfile Generation > (TICK) Store information about method parameters (usable via reflection)
      ```

      2. Generate the .jar :

      ```eclipse_ui
      (SUBWINDOW) Project Explorer > (RCLIC) [Project] > (CLIC) Export >
        (WINDOW) Export > (SELECT) Java > (SELECT) JAR file ; (CLIC) Next
        (WINDOW) JAR Export >
          (SUBWINDOW) Select the resources to export: >
            (SELECT) src/main/java
            (SELECT) lib
          (TICK) Export generated class files and resources
          (TICK) Compress the contents of the JAR file
          (CLIC) Finish
      ```

  </details>

  2. <details>

      <summary>Implement the functions on your ksqldb server (on the device hosting the ksqldb server)</summary>

      1. Create a folder for your extensions and add them to it

      ```shell
      mkdir [path to your ksqldb extensions folder] && cp [path to your previously generated extensions uber-jar] [path to your ksqldb extensions folder]
      ```

      2. Specify the path to your extensions folder in your ksqldb config.

      ```fs
      (EDIT) [path to your ksqldb]/etc/ksqldb/ksqldb-server.properties
      "
      [...]
      ksql.extension.dir=[path to your ksqldb extensions folder]
      [...]
      "
      ```

      3. Restart your ksqldb server

      ```shell
      [path to ksqldb]/usr/bin/ksql-server-stop && \
      [path to ksqldb]/usr/bin/ksql-server-start [path to your ksqldb]/etc/ksqldb/ksqldb-server.properties
      ```

  </details>

  3. <details>

      <summary>Check that your ksqldb server implements the new functions</summary>

      1. List ksqldb functions

        ```sql
        show functions;
        ```

      2. Describe newly added ksqldb functions

        ```sql
        describe function [function];
        ```

  </details>

  4. <details>

      <summary>Test the new functions</summary>

      1. Set-up a test environment

          1. Create a kafka topic

          ```shell
          [path to kafka]/bin/kafka-topics.sh --create --bootstrap-server 10.0.72.10:9092 --topic TEST_TOPIC
          ```

          2. Extract a stream from it

          ```sql
          CREATE OR REPLACE STREAM TEST_STREAM (NAME STRING, VALUE DOUBLE, TS STRING) WITH (KAFKA_TOPIC='TEST_TOPIC', KEY_FORMAT='KAFKA', TIMESTAMP='TS', TIMESTAMP_FORMAT='yyyy-MM-dd''T''HH:mm:ss.SSSX', VALUE_FORMAT='JSON');
          ```

          3. Write random data to your topic (alternative : https://docs.ksqldb.io/en/0.10.2-ksqldb/developer-guide/test-and-debug/generate-custom-test-data/ (less flexible))

          ```shell
          ./kafka_dataset_generator.py 10.0.72.10:9092
          ```

      2. Try the new functions (from ksqldb cli)

          1. modulo (UDF) :

          ```sql
          SELECT *, modulo(value, 12) AS modulo_12 FROM TEST_STREAM EMIT CHANGES;
          ```

          2. delta (UDAF) :

          ```sql
          SELECT name, delta(value) AS delta, latest_by_offset(value,2) last_values, LATEST_BY_OFFSET(ts, 2) as last_tss FROM TEST_STREAM GROUP BY name EMIT CHANGES;
          ```
            
      3. Shut down the test environment

          1. Stop the generator

          2. Delete the test topic

          ```shell
          [path to kafka]/bin/kafka-topics.sh --delete --topic TEST_TOPIC --bootstrap-server 10.0.72.10:9092
          ```

          3. Delete the test stream

          ```sql
          drop stream TEST_STREAM;
          ```

  </details>

# Additional infos

Notable other repos on the subject (cf. https://github.com/search?q=ksqldb-udf&type=repositories) :
  - https://github.com/hpgrahsl/ksqldb-datetime-functions : date manipulation oriented functions & types built on java 11's time library
  - https://github.com/entechlog/ksqlDB-udf : specific operations (strings/array manipulation)
  - https://github.com/nbuesing/ksqldb-udf-geospatial : geospatial operations

Contact : alx.remy@gmail.com
