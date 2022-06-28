# Purpose:
## Very basic peer-2-peer for a chat. All peers can communicate with each other.

Each node is independent. Has own port to listen to new connection. and can connect to as many pear as needed.

## Running a peer with no initial connection
This will start the peer on a default port and use localhost
```s
gradle runPeer -q --console=plain -PpeerName=name1 -Pport=9001

localhost:9002 localhost:9003
```
If you want to change the peer port
```s
gradle runPeer -q --console=plain -PpeerName=name1 -Pport=9000
```

## Running a peer with a initial connection
This will start the peer on a default port and use localhost
```s
gradle runPeer -q --console=plain -PpeerName=name2 -Pport=9002 -PinitConnection=localhost:9001

gradle runPeer -q --console=plain -PpeerName=name2-Pport=9003 -PinitConnection=localhost:9002
```
If you want to change the peer port 
```s
gradle runPeer -q --console=plain -PpeerName=name2 -Pport=9001 -PinitConnection=localhost:9000
```

Screencast: https://youtu.be/qwPjUFSiaTA