# ⚡ Real-Time Bidding & Auction Platform

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![WebSockets](https://img.shields.io/badge/WebSockets-STOMP-yellow.svg)
![JWT Security](https://img.shields.io/badge/Security-JWT-blueviolet.svg)

A high-performance, real-time auction engine built with **Spring Boot**. This platform allows users to create auctions and place live bids, broadcasting updates instantly to all connected clients using **WebSockets (STOMP)**. It is engineered to handle high-concurrency bidding environments with robust race-condition protections.

## 🚀 Key Features

* **Real-Time Broadcasting:** Live bid updates pushed to clients in milliseconds via STOMP WebSockets, eliminating the need for HTTP polling.
* **Concurrency Control:** Implements **Optimistic Locking** at the database layer to prevent race conditions when multiple users bid on the same item at the exact same millisecond.
* **Stateless Security:** Secured via Spring Security and custom JWT (JSON Web Token) authentication.
* **Global Error Handling:** Clean, sanitized API responses managed by a central `@RestControllerAdvice` layer.
* **CORS Configured:** Fully integrated Cross-Origin Resource Sharing for seamless frontend-to-backend WebSocket connections.

## 🛠️ Tech Stack

* **Backend Framework:** Java 17, Spring Boot 3.x
* **Database & ORM:** MySQL 8, Spring Data JPA, Hibernate
* **Real-Time Engine:** Spring WebSockets, STOMP Messaging Protocol
* **Security:** Spring Security, JWT (io.jsonwebtoken)
* **Build Tool:** Maven

## 🏗️ Architecture & Data Flow

When a user places a bid, the system executes the following real-time flow:
1. **HTTP POST Request:** The client submits a JWT-authenticated bid.
2. **Database Transaction:** The Service layer verifies the bid amount and attempts an Optimistic Lock save to MySQL.
3. **Message Broker:** Upon successful database commit, the `BidBroadcaster` intercepts the result.
4. **WebSocket Push:** The system routes the new bid through the STOMP Message Broker and pushes it directly to all users subscribed to that specific auction's frequency.

## 📡 API Reference

### Authentication
* `POST /api/auth/register` - Register a new user
* `POST /api/auth/login` - Authenticate and receive a JWT

### Auctions
* `POST /api/auctions` - Create a new scheduled auction (Requires JWT)

### Bids (Real-Time)
* `POST /api/bids` - Place a bid (Requires JWT)
  * *Payload:* `{"auctionId": 1, "amount": 5000.00}`

### WebSocket Endpoints (STOMP)
* **Connection URL:** `ws://localhost:8080/ws-raw` (No auth required for read-only viewing)
* **Subscribe Topic:** `/topic/auctions/{auctionId}/bids` (Listen for live bids)

## 💻 Running the Project Locally

### Prerequisites
* Java 17+ installed
* Docker & Docker Compose installed
* Maven installed

### Setup Steps
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/YOUR_USERNAME/real-time-auction-platform.git](https://github.com/YOUR_USERNAME/real-time-auction-platform.git)
   cd real-time-auction-platform
   Start the MySQL Database:

Bash
docker-compose up -d
Run the Spring Boot Application:

Bash
mvn spring-boot:run
Test the WebSocket Connection:
Open the included websocket-test.html file in any modern web browser to see the live broadcasting in action.

🔮 Roadmap / Future Enhancements
Auction Lifecycle Scheduler: Implement @Scheduled cron jobs to automatically transition auctions from SCHEDULED -> LIVE -> CLOSED.

Redis Leaderboard: Cache the top-10 active auctions using Spring Data Redis ZSET for sub-millisecond query performance.

Anti-Sniping Logic: Automatically extend auction timers by 2 minutes if a bid is placed in the final 30 seconds.
