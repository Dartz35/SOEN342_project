CREATE DATABASE IF NOT EXISTS trip_booking_db;
USE trip_booking_db;

SET default_storage_engine=InnoDB;




CREATE TABLE Trip (
  trip_id VARCHAR(20) PRIMARY KEY,
  from_city VARCHAR(50) NOT NULL,
  to_city VARCHAR(50) NOT NULL,
  departure_time TIME NOT NULL,
  arrival_time TIME NOT NULL,
  total_duration VARCHAR(20),
  time_to_change_connection VARCHAR(20),
  total_first_rate DECIMAL(8,2),
  total_second_rate DECIMAL(8,2),
  days_of_op VARCHAR(100),
  train_types VARCHAR(100),
  policy_id INT
) ENGINE=InnoDB;

CREATE TABLE Client (
  client_id VARCHAR(20) PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  age INT
) ENGINE=InnoDB;

CREATE TABLE Ticket (
  ticket_number INT PRIMARY KEY
) ENGINE=InnoDB;

CREATE TABLE Reservation (
  reservation_id VARCHAR(20) PRIMARY KEY,
  client_id VARCHAR(20) NOT NULL,
  ticket_number INT NOT NULL,          -- CHANGED from BIGINT → INT
  FOREIGN KEY (client_id) REFERENCES Client(client_id),
  FOREIGN KEY (ticket_number) REFERENCES Ticket(ticket_number)
) ENGINE=InnoDB;

CREATE TABLE BookedTrip (
  booked_trip_id VARCHAR(20) PRIMARY KEY,
  trip_id VARCHAR(20) NOT NULL,
  client_id VARCHAR(20) NOT NULL,
  FOREIGN KEY (trip_id) REFERENCES Trip(trip_id),
  FOREIGN KEY (client_id) REFERENCES Client(client_id)
  )ENGINE=InnoDB;



