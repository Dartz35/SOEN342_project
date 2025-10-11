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

3.
