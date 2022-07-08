package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;


// Implement the car leasing service. It has four sevices listCars, lease, giveBack, bill
class CarLeasingImpl extends CarLeasingGrpc.CarLeasingImplBase {
	
	// inner class for car instance
	class CarInstance {
		private Integer id;
		private String name;
		private String brand;
		private Double leasingPrice;
		private Integer status;
		
		public CarInstance(Integer id, String name, String brand, Double leasingPrice, Integer status) {
			this.id = id;
			this.name = name;
			this.brand = brand;
			this.leasingPrice = leasingPrice;
			this.status = status;
		}
			
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getBrand() {
      return brand;
    }
    public void setBrand(String brand) {
      this.brand = brand;
    }
    public Double getLeasingPrice() {
      return leasingPrice;
    }
    public void setStatus(Integer status) {
      this.status = status;
    }
    public Integer getStatus() {
      return status;
    }
	}
    
  // global map for cars
  Map<Integer, CarInstance> carMap = new HashMap<>();
  Map<String, Set<CarInstance>> userLeasingCarSummary = new HashMap<>();
  Map<String, List<CarInstance>> userBillCarSummary = new HashMap<>();
  
  public CarLeasingImpl(){
      super();
      
      // creating some car datas
      carMap.put(1, new CarInstance(1, "car1", "Benz", 100.0, 0));
      carMap.put(2, new CarInstance(2, "car2", "Tesla", 120.0, 0));
      carMap.put(3, new CarInstance(3, "car3", "BMW", 150.0, 0));
      carMap.put(4, new CarInstance(4, "car4", "Buick", 90.0, 0));
      carMap.put(5, new CarInstance(5, "car5", "Toyota", 80.0, 0));
  }
    
  // list all the cars
  @Override
  public void listCars(com.google.protobuf.Empty req, StreamObserver<CarsResponse> responseObserver) {
    System.out.println("Received from client. Start to get car list.");
    CarsResponse.Builder response = CarsResponse.newBuilder();
    if (!carMap.isEmpty()) {
      response.setIsSuccess(true);
      response.setError("");
      List<Car> carList = new ArrayList<>();
      for (Map.Entry<Integer, CarInstance> entry : carMap.entrySet()) {
        CarInstance carInstance = (CarInstance) entry.getValue();
        Car.Builder car = Car.newBuilder();
        car.setId(carInstance.getId())
           .setName(carInstance.getName())
           .setBrand(carInstance.getBrand())
           .setLeasingPrice(carInstance.getLeasingPrice())
           .setStatus(carInstance.getStatus());
        carList.add(car.build());
      }
      response.addAllCars(carList);
    } else {
      response.setIsSuccess(false);
      response.setError("there are no car datas now.");
    }
    CarsResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
  
  //car lease
  @Override
  public void lease(CarLeasingRequest req, StreamObserver<CarLeasingResponse> responseObserver) {
    System.out.println("Received from client. Start to lease a car.");
    CarLeasingResponse.Builder response = CarLeasingResponse.newBuilder();
    Integer carId = req.getCarId();
    String userName = req.getUserName();
    if (carMap.containsKey(carId)) {
      CarInstance carInstance = (CarInstance) carMap.get(carId);
      if (carInstance.getStatus().equals(0)) {
        // releasing a car
        carInstance.setStatus(1);
        // update user leasing summary
        Set<CarInstance> userLeasingCars;
        if (!userLeasingCarSummary.containsKey(userName)) {
          userLeasingCars = new HashSet<CarInstance>();
          userLeasingCarSummary.put(userName, userLeasingCars);
        } else {
          userLeasingCars = (Set<CarInstance>) userLeasingCarSummary.get(userName);
        }
        userLeasingCars.add(carInstance);
        
        // update user bill summary
        List<CarInstance> userBillCars;
        if (!userBillCarSummary.containsKey(userName)) {
          userBillCars = new ArrayList<CarInstance>();
          userBillCarSummary.put(userName, userBillCars);
        } else {
          userBillCars = (List<CarInstance>) userBillCarSummary.get(userName);
        }
        userBillCars.add(carInstance);
        
        // response a success info
        response.setIsSuccess(true);
        Car.Builder car = Car.newBuilder();
        car.setId(carInstance.getId())
           .setName(carInstance.getName())
           .setBrand(carInstance.getBrand())
           .setLeasingPrice(carInstance.getLeasingPrice())
           .setStatus(carInstance.getStatus());
        response.setCar(car.build());
      } else {
        response.setIsSuccess(false);
        response.setError("this car has alreasy leased to other person.");
      }
    } else {
      response.setIsSuccess(false);
      response.setError("no this car data, car leasing fail.");
    }
    CarLeasingResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
  
  // car give back
  @Override
  public void giveBack(CarGiveBackRequest req, StreamObserver<CarGiveBackResponse> responseObserver) {
    System.out.println("Received from client. Start to give back a car.");
    CarGiveBackResponse.Builder response = CarGiveBackResponse.newBuilder();
    Integer carId = req.getCarId();
    String userName = req.getUserName();
    if (carMap.containsKey(carId)) {
      CarInstance carInstance = (CarInstance) carMap.get(carId);
      Set<CarInstance> userLeasingCars = userLeasingCarSummary.get(userName);
      if (null == userLeasingCars || userLeasingCars.isEmpty() || !userLeasingCars.contains(carInstance)) {
        response.setIsSuccess(false);
        response.setError("don't lease this car, car give back fail.");
      } else {
        // give back a car
        carInstance.setStatus(0);
        // update user leasing summary
        userLeasingCars.remove(carInstance);
        
        // response a success info
        response.setIsSuccess(true);
        Car.Builder car = Car.newBuilder();
        car.setId(carInstance.getId())
           .setName(carInstance.getName())
           .setBrand(carInstance.getBrand())
           .setLeasingPrice(carInstance.getLeasingPrice())
           .setStatus(carInstance.getStatus());
        response.setCar(car.build());
      }
    } else {
      response.setIsSuccess(false);
      response.setError("don't lease this car, car give back fail.");
    }
    CarGiveBackResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
  
  //car bill
  @Override
  public void bill(BillRequest req, StreamObserver<BillResponse> responseObserver) {
    System.out.println("Received from client. Start to calculate your bill.");
    BillResponse.Builder response = BillResponse.newBuilder();
    String userName = req.getUserName();
    List<CarInstance> userBillCars = userBillCarSummary.get(userName);
    if (null == userBillCars || userBillCars.isEmpty()) {
      response.setIsSuccess(false);
      response.setError("don't have bill data.");
    } else {
      Double totalPrice = 0.0;
      List<Car> carList = new ArrayList<>();
      for (int i = 0; i < userBillCars.size(); i++) {
        CarInstance carInstance = userBillCars.get(i);
        totalPrice += carInstance.getLeasingPrice();
        Car.Builder car = Car.newBuilder();
        car.setId(carInstance.getId())
        .setName(carInstance.getName())
        .setBrand(carInstance.getBrand())
        .setLeasingPrice(carInstance.getLeasingPrice())
        .setStatus(carInstance.getStatus());
        carList.add(car.build());
      }
      response.setIsSuccess(true);
      response.setTotalPrice(totalPrice);
      response.addAllEverLeasingCars(carList);
    }
    BillResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
 }
}