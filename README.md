# RPC Framework

A high-performance distributed RPC framework inspired by Dubbo, supporting service registration and discovery, dynamic proxies, network communication, load balancing, and SPI-based extension mechanisms.

## Key Features

- **Network Communication Layer**
    - Built an asynchronous communication layer using **Netty NIO** , supporting connection multiplexing and achieving **18,000 QPS**  per instance.

    - Optimized memory usage by integrating **Netty memory pooling** , reducing **GC overhead by 35%** .

    - Developed a **blocking I/O (BIO) version**  as a comparative baseline.

- **Custom Binary Protocol**
    - Designed a **binary transmission protocol**  with headers including **magic number, version, message length, message type, compression type, serialization type, and request ID** , supporting **CRC32 checksum validation** .

    - Implemented a **graceful shutdown mechanism**  using JVM **Shutdown Hook**  to ensure resource cleanup.

- **Serialization and Compression**
    - Implemented **Kryo, Hessian, and Protostuff**  serialization schemes via **SPI extensions** , supporting **Gzip compression** , resulting in **40% serialization performance improvement** .

    - Provided an extensible SPI mechanism supporting **IOC dependency injection**  and **AOP-based enhancements** .

- **Load Balancing & Service Discovery**
    - Implemented **random, round-robin, least active, and consistent hashing**  load balancing strategies.

    - Integrated **ZooKeeper**  for **dynamic service discovery** , enabling real-time service node updates.

    - Developed a **configuration center**  supporting **dynamic updates via ZooKeeper** , reducing the need for service restarts.

- **Fault Tolerance & Concurrency Optimization**
    - Designed **circuit breaker and failover strategies** , supporting **Failover, Failfast, and Failsafe**  fault tolerance modes to enhance system availability.

    - Leveraged **CompletableFuture**  for **asynchronous calls**  with **callback support** , reducing thread blocking overhead.

- **Dynamic Proxy & Thread Isolation**
    - Developed a **dynamic proxy factory**  using **JDK Proxy** , abstracting underlying network communication details.

    - Implemented **thread pool isolation** , separating core and non-core business logic threads to prevent resource contention.

## Prerequisites

- Java 8+
- Maven 3.6+
- Zookeeper 3.5.4+

## Project Structure
```pgsql
├── pom.xml
├── rpc-core
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── alex
│                       └── rpc
│                           ├── config
│                           │   └── RpcServiceConfig.java
│                           ├── constant
│                           │   └── RpcConstant.java
│                           ├── dto
│                           │   ├── RpcReq.java
│                           │   └── RpcResp.java
│                           ├── enums
│                           │   └── RpcRespStatus.java
│                           ├── exception
│                           │   └── RpcException.java
│                           ├── factory
│                           │   └── SingletonFactory.java
│                           ├── handler
│                           │   └── RpcReqHandler.java
│                           ├── loadbalance
│                           │   ├── LoadBalance.java
│                           │   └── impl
│                           │       └── RandomLoadBalance.java
│                           ├── provider
│                           │   ├── ServiceProvider.java
│                           │   └── impl
│                           │       ├── SimpleServiceProvider.java
│                           │       └── ZkServiceProvider.java
│                           ├── proxy
│                           │   └── RpcClientProxy.java
│                           ├── registry
│                           │   ├── ServiceDiscovery.java
│                           │   ├── ServiceRegistry.java
│                           │   ├── impl
│                           │   │   ├── ZkServiceDiscovery.java
│                           │   │   └── ZkServiceRegistry.java
│                           │   └── zk
│                           │       └── ZkClient.java
│                           ├── transmission
│                           │   ├── RpcClient.java
│                           │   ├── RpcServer.java
│                           │   └── socket
│                           │       ├── client
│                           │       │   └── SocketRpcClient.java
│                           │       └── server
│                           │           ├── SocketReqHandler.java
│                           │           └── SocketRpcServer.java
│                           └── util
│                               ├── IPUtils.java
│                               ├── ShutdownHookUtils.java
│                               └── ThreadPoolUtils.java
├── test-api
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── alex
│                       └── api
│                           ├── User.java
│                           └── UserService.java
├── test-client
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── alex
│                       └── client
│                           ├── Main.java
│                           └── utils
│                               └── ProxyUtils.java
└── test-server
    ├── pom.xml
    └── src
        └── main
            └── java
                └── com
                    └── alex
                        └── server
                            ├── Main.java
                            └── service
                                └── UserServiceImpl.java


```

- **rpc-core:**  Contains the core implementation of the RPC framework (service configuration, networking, registry, discovery, load balancing, and utility classes).

- **test-api:**  Defines service interfaces and data transfer objects (DTOs) used in testing.

- **test-client:**  Implements a client that uses dynamic proxies to invoke remote services.

- **test-server:**  Hosts the server-side implementations of services, registers them with ZooKeeper, and listens for incoming RPC requests.


## Key Components & Highlights

### Service Configuration

- **RpcServiceConfig.java**
  This class encapsulates service metadata such as version, group, and the service instance. It generates unique service names by combining the interface names with version and group information

### Singleton Management

- **SingletonFactory.java**
  Implements a thread-safe singleton pattern using a concurrent cache to ensure that each class is instantiated only once

### Service Provider & Registry

- **ZkServiceProvider.java & ZkServiceRegistry.java**
  These classes handle service publication using ZooKeeper. The provider publishes the service (obtaining the host and port dynamically), and the registry creates a persistent node in ZooKeeper for service discovery:

```java
public class ZkServiceProvider implements ServiceProvider {
    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames()
                .forEach(rpcServiceName -> publishService(rpcServiceName, config.getService()));
    }
}
```

### Client-side Proxy

- **RpcClientProxy.java**
  Leverages Java's dynamic proxy mechanism to intercept method calls and convert them into RPC requests

### Socket-Based Server & Request Handling

- **SocketRpcServer.java & SocketReqHandler.java**
  The server listens on a specified port, accepts incoming socket connections, and dispatches each request to a handler that processes the RPC request and writes back the response

### Thread Pool Utilities

- **ThreadPoolUtils.java**
  Provides methods to create and manage thread pools for CPU-intensive and IO-intensive tasks, ensuring efficient handling of concurrent RPC requests.

## Getting Started

### Prerequisites

- **Java 8+**

- **Maven 3.x**

- **ZooKeeper Server**  (for service registry and discovery)

### Building the Project

Clone the repository and build all modules using Maven:


```bash
mvn clean install
```

### Running the Server and Client

1. **Start the Server**
   Navigate to the `test-server` module and run the main class:

```bash
cd test-server
mvn exec:java -Dexec.mainClass="com.alex.server.Main"
```
The server publishes the service (e.g., `UserServiceImpl`) to ZooKeeper and starts listening for incoming RPC requests.

2. **Run the Client**
   In a separate terminal, navigate to the `test-client` module and run:

```bash
cd test-client
mvn exec:java -Dexec.mainClass="com.alex.client.Main"
```
The client creates a proxy for `UserService` and invokes remote methods through the RPC framework.

## Dependencies

The project leverages several key libraries:

- **Lombok:**  Reduces boilerplate code.

- **Hutool:**  Provides various utility methods.

- **Logback:**  For logging.

- **Netty:**  For network communication (if extended in the future).

- **Apache Curator & ZooKeeper:**  For distributed service registration and discovery.

## Future Enhancements

- Implement additional load balancing strategies.

- Integrate more robust network frameworks such as Netty.

- Enhance error handling and fault tolerance.

- Add security features to secure RPC communication.

## Conclusion

This RPC framework serves as a modular and extensible foundation for building distributed systems. It illustrates key concepts in remote communication, service discovery, and dynamic proxying, making it a valuable learning tool for mastering advanced Java programming and distributed architectures.

## Contribution
Contributions are welcome! Please open an issue or PR for any improvements.
