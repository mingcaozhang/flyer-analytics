CREATE KEYSPACE flyer WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE flyer;
CREATE TABLE events(id UUID, date DATE, user_id VARCHAR, timestamp BIGINT, event_type VARCHAR, flyer_id VARCHAR, merchant_id VARCHAR, PRIMARY KEY ((date), user_id, timestamp, id));