package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;


// Implement the timer service. It has four sevices start, check, close, list
class TimerImpl extends TimerGrpc.TimerImplBase {
  
  class TimerInstance {
    private Timer timer;
    private Long timePassed;
    
    public TimerInstance(Timer timer, Long timePassed) {
      this.timer = timer;
      this.timePassed = timePassed;
    }
    
    public Timer getTimer() {
      return this.timer;
    }
    public void setTimer(Timer timer) {
      this.timer = timer;
    }
    public Long getTimePassed() {
      return this.timePassed;
    }
    public void setTimePassed(Long timePassed) {
      this.timePassed = timePassed;
    }
    public void tick() {
      this.timePassed++;
    }
  }
    
  // global map timer datas
  Map<String, TimerInstance> timerMap = new HashMap<>();
  
  public TimerImpl(){
      super();
  }
  
  @Override
  public void start(TimerRequest req, StreamObserver<TimerResponse> responseObserver) {
    System.out.println("Received from client. Try to start a timer.");
    System.out.println("[TimerName]: " + req.getName());
    TimerResponse.Builder response = TimerResponse.newBuilder();
    if (!timerMap.containsKey(req.getName())) {
      Timer timer = new Timer();
      Long timePassed = 0L;
      final String timerName = req.getName();
      timerMap.put(req.getName(), new TimerInstance(timer, timePassed));
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          TimerInstance timerInstance = (TimerInstance) timerMap.get(timerName);
          timerInstance.tick();
        }
      };
      timer.schedule(timerTask, 0, 1000);
      response.setIsSuccess(true);
      response.setTimer(Time.newBuilder().setName(req.getName()).setSecondsPassed(0L).build());
    } else {
      response.setIsSuccess(false);
      response.setError("can not start the timer, because name has exist for: " + req.getName());
    }
    
    TimerResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
  
  @Override
  public void check(TimerRequest req, StreamObserver<TimerResponse> responseObserver) {
    System.out.println("Received from client. Try to check timer.");
    System.out.println("[TimerName]: " + req.getName());
    TimerResponse.Builder response = TimerResponse.newBuilder();
    if (timerMap.containsKey(req.getName())) {
      TimerInstance timerInstance = (TimerInstance) timerMap.get(req.getName());
      response.setIsSuccess(true);
      response.setTimer(Time.newBuilder().setName(req.getName()).setSecondsPassed(timerInstance.getTimePassed()).build());
    } else {
      response.setIsSuccess(false);
      response.setError("don't exist timer for name: " + req.getName());
    }
    
    TimerResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
  
  @Override
  public void close(TimerRequest req, StreamObserver<TimerResponse> responseObserver) {
    System.out.println("Received from client. Try to close timer.");
    System.out.println("[TimerName]: " + req.getName());
    TimerResponse.Builder response = TimerResponse.newBuilder();
    if (timerMap.containsKey(req.getName())) {
      TimerInstance timerInstance = (TimerInstance) timerMap.get(req.getName());
      timerInstance.getTimer().cancel();
      timerMap.remove(req.getName());
      response.setIsSuccess(true);
      response.setTimer(Time.newBuilder().setName(req.getName()).setSecondsPassed(timerInstance.getTimePassed()).build());
    } else {
      response.setIsSuccess(false);
      response.setError("don't exist timer for name: " + req.getName());
    }
    
    TimerResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
  
  @Override
  public void list(com.google.protobuf.Empty req, StreamObserver<TimerList> responseObserver) {
    System.out.println("Received from client. Try to get all timer list.");
    TimerList.Builder response = TimerList.newBuilder();
    if (!timerMap.isEmpty()) {
      List<Time> timerList = new ArrayList<>();
      for (Map.Entry<String, TimerInstance> entry : timerMap.entrySet()) {
        String timerName = (String) entry.getKey();
        TimerInstance instance = (TimerInstance) entry.getValue();
        Time oneTime = Time.newBuilder().setName(timerName).setSecondsPassed(instance.getTimePassed()).build();
        timerList.add(oneTime);
      }
      response.addAllTimers(timerList);
    }
    
    TimerList resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}