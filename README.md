# SOEN342_project

- **Ali Khalifa (40215063/[@Dartz35](https://github.com/Dartz35))** –  
- **Soukayna Haitami (40280964/[@Soukay55](https://github.com/Soukay55))** –
- **Wissem Oumsalem(40291712/[@wisoums](https://github.com/wisoums))** -

# Iteration 1: Train Trip Finder

A Java-based application for searching, loading, and sorting train trips from CSV route data.  
It helps users find direct or indirect train journeys, calculate total travel times, and compare prices.

---

## Features

- **Load Route Data:** Import train routes from a CSV file containing origin, destination, time, and price.  
- **Search Trips:** Find **direct** or **indirect** trips based on travel criteria (origin, destination, and date).  
- **Trip Details:** Automatically compute total duration, transfer times, and total cost.  
- **Sorting Options:** Sort trips by  
  - Departure time  
  - Arrival time  
  - Duration  
  - Price  
  - Origin or destination city  
- **Simple Interface:** The `Main` class coordinates route loading, trip searching, and sorting.

---

## Components Overview

| Component | Description |
|------------|-------------|
| **Route** | Represents a single rail connection (one leg) with timing and price details. |
| **Trip** | Abstract base class representing a journey made up of one or more routes. |
| **DirectTrip** | Represents a one-leg journey. |
| **IndirectTrip** | Represents a multi-leg journey with connections. |
| **SearchCriteria** | Holds user filters such as origin, destination, and date. |
| **TripFinder** | Utility class that locates direct and indirect trips based on criteria. |
| **TripSort** | Utility class to sort trips using various attributes. |
| **RouteCsvLoader** | Loads route data from CSV files and returns a list of routes. |
| **Main** | Application entry point that ties all components together. |

---
## How to Run

1. Compile and run:

   java Main

2. Input one of the following attribute:
 - from       
 - to         
 - departure  
 - arrival    
 - duration   
 - First Rate      
 - Second Rate      
 - Train type
 - Day of operation

3. Let's you choose 'to'. You will then input a value. (Consult eu_rail_network.csv file)
   Example: 'Aarhus'

Results:
Trip{id='D:R01613', Malmö?Aarhus, legs=1, dep=19:35, arr=04:50, dur=555min, changeTime=0min, First Rate=246.0?, Second Rate=134.0?, TrainTypes=[Nightjet], DaysOfOp=[Fri, Mon, Wed]}
Trip{id='D:R01614', Malmö?Aarhus, legs=1, dep=15:35, arr=18:00, dur=145min, changeTime=0min, First Rate=79.0?, Second Rate=49.0?, TrainTypes=[Frecciarossa], DaysOfOp=[Daily]}

5. Once you get the results, you will be prompted to either 'Sort' or 'Quit'. If you choose 'Sort', you will have the following choices:
Sort by: 
 1) Departure time
 2) Arrival time
 3) Origin city (A?Z)
 4) Destination city (A?Z)
 5) Duration
 6) First Rate
 7) Second Rate
Choose 1-7:

6. Let's say you choose '2'. You will now be prompted to display in either ascending or descending order.
   Order [A]sc / [D]esc (default A): 

7. Let's suppose you choose 'A'.

Results:
Trip{id='D:R01613', Malmö?Aarhus, legs=1, dep=19:35, arr=04:50, dur=555min, changeTime=0min, First Rate=246.0?, Second Rate=134.0?, TrainTypes=[Nightjet], DaysOfOp=[Fri, Mon, Wed]}
Trip{id='D:R01614', Malmö?Aarhus, legs=1, dep=15:35, arr=18:00, dur=145min, changeTime=0min, First Rate=79.0?, Second Rate=49.0?, TrainTypes=[Frecciarossa], DaysOfOp=[Daily]}




# Iteration 2: Trip Booking & Management System


## Features

- **Load Route Data** – Import train routes from CSV files containing origin, destination, time, and fare information.  
- **Search Trips** – Find **direct** or **indirect** connections based on travel criteria such as origin, destination, and date.  
- **Trip Computation** – Automatically calculate total duration, connection times, and total costs.  
- **Sorting Options** – Sort trips by:
  - Departure or Arrival Time  
  - Duration  
  - Origin / Destination City  
  - First or Second-class Rate  
  - Day of Operation or Train Type  
- **Booking System** – Book trips for one or multiple clients; generate tickets and reservations automatically.  
- **Trip History Management** – Retrieve current or past trips, organized by client.  
- **Controller Logic** – `TripManager` coordinates booking, viewing, and record-keeping across system components.

---

## Components Overview

| Component | Description |
|------------|-------------|
| **Route** | Represents a single train route with origin, destination, timings, and fare details. |
| **Trip (Abstract)** | Base class representing a journey composed of one or more routes; includes validation and duration logic. |
| **DirectTrip** | A trip with a single route (no transfers). |
| **IndirectTrip** | A multi-leg journey involving transfers or connections. |
| **SearchCriteria** | Holds filters such as origin, destination, date, and sorting attributes. |
| **TripFinder** | Utility class that finds direct and indirect trips according to the given criteria. |
| **TripSort** | Utility class that sorts trips using specified `SortBy` and `SortOrder` enums. |
| **RouteCsvLoader** | Utility class for reading and parsing CSV route files into `Route` objects. |
| **Client** | Represents a traveler with identifying details like name, ID, and age. |
| **Ticket** | Represents a unique travel document for a reservation. |
| **Reservation** | Links a `Client` and their corresponding `Ticket` for a specific trip. |
| **BookedTrip** | Represents a booked trip containing one or more `Reservation` objects. |
| **TripHistory** | Stores and retrieves all booked trips; supports filtering by client or date. |
| **TripManager** | Acts as the main **controller**, managing trip booking and trip history retrieval. |

---
