# Assignment 4 Activity 1
## Description


## Protocol

### Requests
request: { "selected": <int: 1=add, 2=pop, 3=display, 4=count, 5=switch,
0=quit>, "data": <thing to send>}

  data <string> add
  data <> pop
  data <> display
  data <> count
  data <int> <int> switch return a string

### Responses

sucess response: {"type": <"add",
"pop", "display", "count", "switch", "quit"> "data": <thing to return> }

type <String>: echoes original selected from request
data <string>: add = new list, pop = new list, display = current list, count = num elements, switch = current list


error response: {"type": "error", "message"": <error string> }
Should give good error message if something goes wrong


## How to run the program
### Terminal
Please use the following commands:
```
    For Server Task 1, run "gradle runServerTask1 -Pport=9099 -q --console=plain"
```
```
    For Threaded Server Task 2, run "gradle runServerTask2 -Pport=9099 -q --console=plain"
```
```   
    For ThreadPoolServer, run "gradle runServerTask3 -Pport=9099 -Pthread=10 -q --console=plain"
```   


For Client, run "gradle runClient -Phost=localhost -Pport=9099 -q --console=plain"




A) Implemented task 1, task 2 and task 3 in a gradle file. 
B) run client - gradle runClient -Phost=localhost -Pport=9099 -q --console=plain
   Run server for task 1 - gradle runServerTask1 -Pport=9099 -q --console=plain
   Run server for task 2 - gradle runServerTask2 -Pport=9099 -q --console=plain
   Run server for task 3 - gradle runServerTask3 -Pport=9099 -Pthread=10 -q --console=plain
C) The program converts from a single threaded server to a multi threaded server
D) [short video](https://youtu.be/T_ch09BfCh8)
E) The design and user interaction is easy to use
F) Task 1 - the server adds string to an array
   Task 2 - Made the server multi threaded
   Task 3 - Made Multi threaded server bounded
