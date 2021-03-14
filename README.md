# flyer-analytics

Platform to collect metrics needed for flyer analytics.

## Design

This is the proposed design.

![Design](flyer_analytics.jpg)

NOTE: `flyer-analytics-service` was renamed to `flyer-analytics-beacon`

For the sake of simplicity, I've decided to exclude the usage of Avro in the demo. The benefits are tangible when 
running production workloads, but is definitely overkill for this usecase, so I've opted for the simpler solution. 
Kafka will process JSON strings, and the end result will be the same. 

## Requirements
The only dependency needed to run this project is `docker-compose`.

## How to run
```bash
./docker-compose up -d
```

To test that the service works, you can run 
```bash
curl -X POST localhost:8082/beacon/collect -d '{"user_id": "ABCD", "event_type": "flyer_open", "merchant_id": "1", "flyer_id": "2"}'
```

You can verify that the new event was inserted in Cassandra
```bash
docker exec -it cassandra cqlsh
select * from flyer.events where date='$CURRENT_DATE';
```


