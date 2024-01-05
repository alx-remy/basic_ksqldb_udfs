#!/usr/bin/python3

import sys
import random
import json
import datetime
from time import sleep
from kafka import KafkaProducer

if len(sys.argv)>1:
    kafka_server=sys.argv[1]
else:
    kafka_server='127.0.0.1:9092'


producer = KafkaProducer(bootstrap_servers=kafka_server)
topic = 'TestTopic'
names = ['a', 'b', 'c', 'd', 'e']

print('Writing timeseries '+str(names)+' to '+kafka_server+' > '+topic)

i=0
while True:
    data = {
        'name': random.choice(names),
        'value': random.uniform(0.0, 5000.0),
        'ts':datetime.datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%fZ")
    }
    message = json.dumps(data).encode('utf-8')

    producer.send(topic, value=message)
    print(str(i) +' - '+str(data))

    sleep(random.uniform(0.5, 2.0))
    i+=1