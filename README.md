# RPC Framework

A lightweight, modular RPC (Remote Procedure Call) framework in Java. This project demonstrates service registration, dynamic discovery, load balancing, and client-side proxying using both socket-based communication and ZooKeeper for distributed service management.

## Features

- **Service Registration & Discovery**: Integrated with Zookeeper for service management
- **Load Balancing**: Random load balancing strategy (extensible interface)
- **Thread Pool Management**: Custom thread pool utilities ensure efficient handling of concurrent requests
- **Socket-based Communication**: A custom socket server handles incoming requests and dispatches them to the appropriate service handlers
- **Dynamic Proxy**: Client-side proxies transparently convert method calls into RPC requests
- **Graceful Shutdown**: Built-in shutdown hooks for resource cleanup
- **Configuration Management**: Centralized service configuration
- **Exception Handling**: Custom RPC exception system
- **Singleton Management**: Thread-safe singleton factory pattern
- **Service Grouping/Versioning**: Support for service version and group management

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
