# Purpose:
## Very basic peer-2-peer for a chat. All peers can communicate with each other.

Each node is one of those at a time
- leader
- bank
- client
When started type of should be provided.

You want to first start the leader who is the one in charge of the network

## Running the leader
This will start the leader on a default port and use localhost
```s
gradle runNode -q --console=plain -Ptype=leader
```
If you want to change the leader settings 
```s
gradle runNode -q --console=plain -Ptype=leader -Pport=9000
```

## Running a bank node
This will start the bank node on a default port and use localhost
```s
gradle runNode -q --console=plain -Ptype=bank -Pid=bank1 -Pamount=2000
```
If you want to change the leader settings 
```s
gradle runNode -q --console=plain -Ptype=bank -Pid=bank1 -Pamount=2000 -Phost=localhost -Pport=9000
```

## Running a client node
This will start the bank node on a default port and use localhost
```s
gradle runNode -q --console=plain -Ptype=client -Pid=client1
```
If you want to change the leader settings 
```s
gradle runNode -q --console=plain -Ptype=client -Pid=client1 -Phost=localhost -Pport=9000
```

Screencast: https://youtu.be/5B733Ka5rpY