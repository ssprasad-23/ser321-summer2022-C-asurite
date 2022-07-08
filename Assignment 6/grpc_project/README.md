# GRPC Services and Registry

The following folder contains a Registry.jar which includes a Registering service where Nodes can register to allow clients to find them and use their implemented GRPC services. 

Some more detailed explanations will follow and please also check the build.gradle file

Before starting do a "gradle generateProto".

### gradle runRegistryServer
Will run the Registry node on localhost (arguments are possible see gradle). This node will run and allows nodes to register themselves. 

The Server allows Protobuf, JSON and gRPC. We will only be using gRPC

### gradle runNode
Will run a node with an Echo and Joke service. The node registers itself on the Registry. You can change the host and port the node runs on and this will register accordingly with the Registry

### gradle runClientJava
Will run a client which will call the services from the node, it talks to the node directly not through the registry. At the end the client does some calls to the Registry to pull the services, this will be needed later.

### gradle runDiscovery
Will create a couple of threads with each running a node with services in JSON and Protobuf. This is just an example and not needed for assignment 6. 

### gradle testProtobufRegistration
Registers the protobuf nodes from runDiscovery and do some calls. 

### gradle testJSONRegistration
Registers the json nodes from runDiscovery and do some calls. 


Running
Task 1
run: gradle generateProto - this will help generate java files according to proto files
run: gradle runNode - this will start a grpc server node by default config(which run locally, without the register center)
run: gradle runClient - this will start a grpc client by default config(which run locally, without the register center)

Task 2
run command as task 1
There are four services implemented. 
-listCars, show all the cars that can be leasing; 
-lease, input username and carid, that can lease a car; 
-giveBack, input username and carid, then can give back a car leasing before; 
-bill, input username, then can show user bill detail.

Task 3
run: gradle runRegistry - then we start the register center
run: gradle runNode –PregOn=true –PservicePort=7000 - then we start node1 as grpc server.
run: gradle runNode –PregOn=true –PservicePort=7001 - then we start node2 as grpc server.
run: gradle runClient –PregOn=true - then we start client as grpc client.

//for whatever reason if the commands don't work, try typing it in



a)A distribute system that provide whether query in some citys, car leasing service for people, and a simple timer
b)Task 1
  run: gradle generateProto
  run: gradle runNode
  run: gradle runClient

  Task 2 is same as task 1

  Task 3
  run: gradle runRegistry
  run: gradle runNode –PregOn=true –PservicePort=7000
  run: gradle runNode –PregOn=true –PservicePort=7001
  run: gradle runClient –PregOn=true

c) The inputs are task 1 to 11, and navigate through the menu
d) User interaction is easy to navigate
f) https://youtu.be/AnAs9v9godY