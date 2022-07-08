package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;

import com.google.protobuf.Empty; // needed to use Empty

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class Client {
  
  private ManagedChannel channel; // channel to connect
  
  private String serviceName; // service name
  
	private String serviceHost; // service host

	private Integer servicePort; // service port
	
	private Integer auto; // whether to auto run service
	
	/** Construct client for accessing server using the existing channel. */
  public Client(String serviceName, String serviceHost, Integer servicePort, Integer auto) {
    System.out.println("start to init client...");
    this.serviceName = serviceName;
    this.serviceHost = serviceHost;
    this.servicePort = servicePort;
    this.auto = auto;
  }
  
  /**
   * connect grpc server to get resp
   */
  public void connectForResp() throws Exception {
    try {
      String target = serviceHost + ":" + servicePort;
      this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
      System.out.println(this.serviceName);
      switch (this.serviceName) {
        case "services.Weather/atCoordinates":
          if (this.auto == 0) {
            this.askWeatherAtCoordinates(0.0, 0.0);
          } else {
            this.doTestAskWeatherAtCoordinates();
          }
          break;
        case "services.Weather/inCity":
          if (this.auto == 0) {
            this.askWeatherInCity("");
          } else {
            doTestAskWeatherInCity();
          }
          break;
        case "services.Weather/listCities":
          if (this.auto == 0) {
            this.askWeatherListCities();
          } else {
            this.doTestAskWeatherListCities();  
          }
          break;
        case "services.Timer/start":
          if (this.auto == 0) {
            this.askTimerStart("");
          } else {
            this.doTestAskTimerStart();
          }
          break;
        case "services.Timer/check":
          if (this.auto == 0) {
            this.askTimerCheck("");
          } else {
            this.doTestAskTimerCheck();
          }
          break;
        case "services.Timer/close":
          if (this.auto == 0) {
            this.askTimerClose("");
          } else {
            this.doTestAskTimerClose();
          }
          break;
        case "services.Timer/list":
          if (this.auto == 0) {
            this.askTimerList();
          } else {
            this.doTestAskTimerList();
          }
          break;
        case "services.CarLeasing/listCars":
          if (this.auto == 0) {
            this.askCarLeasingListCars();
          } else {
            this.doTestCarLeasingListCars();
          }
          break;
        case "services.CarLeasing/lease":
          if (this.auto == 0) {
            this.askCarLeasingLease("", 0);
          } else {
            this.doTestAskCarLeasingLease();
          }
          break;
        case "services.CarLeasing/giveBack":
          if (this.auto == 0) {
            this.askCarLeasingGiveBack("", 0);
          } else {
            this.doTestAskCarLeasingGiveBack();
          }
          break;
        case "services.CarLeasing/bill":
          if (this.auto == 0) {
            this.askCarLeasingBill("");
          } else {
            this.doTestAskCarLeasingBill();
          }
          break;
        default:
          break;
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      this.channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
  
  /**
   * service: service.Weather\atCoordinates
   * @throws Exception
   */
  public void askWeatherAtCoordinates(Double latitude, Double longitude) throws Exception {
    System.out.println("now will run service: service.Weather\\atCoordinates ..........");
    if (this.auto == 0) {
      System.out.println("please input latitude and longitude: ");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String latitudeStr = reader.readLine();
      String longitudeStr = reader.readLine();
      try {
        latitude = Double.valueOf(latitudeStr);
        longitude = Double.valueOf(longitudeStr);
      } catch (Exception e) {
        System.out.println("please input valid coordinate data.");
        return;
      }
    } else {
      System.out.println("now will call the service with default parameter: [latitude]: " + latitude + " [longitude]: " + longitude);
    }
      
    WeatherCoordinateRequest request = WeatherCoordinateRequest.newBuilder()
        .setLatitude(latitude)
        .setLongitude(longitude)
        .build();
        
    WeatherResponse response;

    try {
      WeatherGrpc.WeatherBlockingStub weatherBlockingStub = WeatherGrpc.newBlockingStub(this.channel);
      response = weatherBlockingStub.atCoordinates(request);
      // deal response
      Boolean isSuccess = response.getIsSuccess();
      if (isSuccess) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[currentTemp]: " + response.getCurrentTemp());
        System.out.println("[currentConditions]: " + response.getCurrentConditions());
        System.out.println("[dailyHighs]: " + response.getDailyHighsList());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskWeatherAtCoordinates() throws Exception {
    this.askWeatherAtCoordinates(40.0, -74.0);
  }
  
  /**
   * service: service.Weather\inCity
   * @throws Exception
   */
  public void askWeatherInCity(String cityName) throws Exception {
    System.out.println("now will run service: service.Weather\\inCity ..........");
    if (this.auto == 0) {
      System.out.println("please input city: ");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      cityName = reader.readLine();
    } else {
      System.out.println("now will call the service with default parameter: [cityName]: " + cityName);
    }
      
    WeatherCityRequest request = WeatherCityRequest.newBuilder()
        .setCityName(cityName)
        .build();
        
    WeatherResponse response;

    try {
      WeatherGrpc.WeatherBlockingStub weatherBlockingStub = WeatherGrpc.newBlockingStub(this.channel);
      response = weatherBlockingStub.inCity(request);
      // deal response
      Boolean isSuccess = response.getIsSuccess();
      if (isSuccess) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[currentTemp]: " + response.getCurrentTemp());
        System.out.println("[currentConditions]: " + response.getCurrentConditions());
        System.out.println("[dailyHighs]: " + response.getDailyHighsList());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskWeatherInCity() throws Exception {
    this.askWeatherInCity("London");
  }
  
  /**
   * service: service.Weather\listCities
   * @throws Exception
   */
  public void askWeatherListCities() throws Exception {
    System.out.println("now will run service: service.Weather\\listCities ..........");
    System.out.println("get all city datas as below: ");
      
    Empty request = null;
    CitiesResponse response;

    try {
      WeatherGrpc.WeatherBlockingStub weatherBlockingStub = WeatherGrpc.newBlockingStub(this.channel);
      response = weatherBlockingStub.listCities(request);
      // deal response
      Boolean isSuccess = response.getIsSuccess();
      if (isSuccess) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[cityNameList]: " + response.getCityNameList());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskWeatherListCities() throws Exception {
    this.askWeatherListCities();
  }
  
  /**
   * service: service.Timer\start
   * @throws Exception
   */
  public void askTimerStart(String timerName) throws Exception {
    System.out.println("now will run service: service.Timer\\start ..........");
    if (this.auto == 0) {
      System.out.println("please input timer name: ");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      timerName = reader.readLine();
    } else {
      System.out.println("now will call the service with default parameter: [timerName]: " + timerName);
    }
      
    TimerRequest request = TimerRequest.newBuilder()
        .setName(timerName)
        .build();
    TimerResponse response;

    try {
      TimerGrpc.TimerBlockingStub timerBlockingStub = TimerGrpc.newBlockingStub(this.channel);
      response = timerBlockingStub.start(request);
      // deal response
      Boolean isSuccess = response.getIsSuccess();
      if (isSuccess) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[timerName]: " + response.getTimer().getName());
        System.out.println("[timerSecondsPassed]: " + response.getTimer().getSecondsPassed());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskTimerStart() throws Exception {
    this.askTimerStart("testTimer1");
    this.askTimerStart("testTimer2");
  }
  
  /**
   * service: service.Timer\check
   * @throws Exception
   */
  public void askTimerCheck(String timerName) throws Exception {
    System.out.println("now will run service: service.Timer\\check ..........");
    if (auto == 0) {
      System.out.println("please input timer name: ");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      timerName = reader.readLine();
    } else {
      System.out.println("now will call the service with default parameter: [timerName]: " + timerName);
    }
      
    TimerRequest request = TimerRequest.newBuilder()
        .setName(timerName)
        .build();
    TimerResponse response;

    try {
      TimerGrpc.TimerBlockingStub timerBlockingStub = TimerGrpc.newBlockingStub(this.channel);
      response = timerBlockingStub.check(request);
      // deal response
      Boolean isSuccess = response.getIsSuccess();
      if (isSuccess) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[timerName]: " + response.getTimer().getName());
        System.out.println("[timerSecondsPassed]: " + response.getTimer().getSecondsPassed());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskTimerCheck() throws Exception {
    this.askTimerCheck("testTimer1");
    this.askTimerCheck("testTimer3");
  }
  
  /**
   * service: service.Timer\close
   * @throws Exception
   */
  public void askTimerClose(String timerName) throws Exception {
    System.out.println("now will run service: service.Timer\\close ..........");
    if (auto == 0) {
      System.out.println("please input timer name: ");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      timerName = reader.readLine();
    } else {
      System.out.println("now will call the service with default parameter: [timerName]: " + timerName);
    }
      
    TimerRequest request = TimerRequest.newBuilder()
        .setName(timerName)
        .build();
    TimerResponse response;

    try {
      TimerGrpc.TimerBlockingStub timerBlockingStub = TimerGrpc.newBlockingStub(this.channel);
      response = timerBlockingStub.close(request);
      // deal response
      Boolean isSuccess = response.getIsSuccess();
      if (isSuccess) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[timerName]: " + response.getTimer().getName());
        System.out.println("[timerSecondsPassed]: " + response.getTimer().getSecondsPassed());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskTimerClose() throws Exception {
    this.askTimerClose("testTimer1");
    this.askTimerClose("testTimer3");
  }
  
  /**
   * service: service.Timer\list
   * @throws Exception
   */
  public void askTimerList() throws Exception {
    System.out.println("now will run service: service.Timer\\list ..........");
    System.out.println("get all timer list as below: ");
      
    Empty request = null;
    TimerList response;

    try {
      TimerGrpc.TimerBlockingStub timerBlockingStub = TimerGrpc.newBlockingStub(this.channel);
      response = timerBlockingStub.list(request);
      // deal response
      List<Time> timerList = response.getTimersList();
      if (timerList.isEmpty()) {
        System.out.println("[isSuccess]: false");
        System.out.println("[error]: there is no timer list.");
      } else {
        System.out.println("[isSuccess]: true");
        System.out.println("[timerList]: " + response.getTimersList());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskTimerList() throws Exception {
    this.askTimerList();
    this.askTimerClose("testTimer2");
    this.askTimerList();
  }
  
  /**
   * service: service.CarLeasing\listCars
   * @throws Exception
   */
  public void askCarLeasingListCars() throws Exception {
    System.out.println("now will run service: service.CarLeasing\\listCars ..........");
    System.out.println("get all car list as below: ");
      
    Empty request = null;
    CarsResponse response;

    try {
      CarLeasingGrpc.CarLeasingBlockingStub carLeasingBlockingStub = CarLeasingGrpc.newBlockingStub(this.channel);
      response = carLeasingBlockingStub.listCars(request);
      // deal response
      List<Car> carList = response.getCarsList();
      if (carList.isEmpty()) {
        System.out.println("[isSuccess]: false");
        System.out.println("[error]: there is no car list.");
      } else {
        System.out.println("[isSuccess]: true");
        System.out.println("[carList]: " + carList);
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestCarLeasingListCars() throws Exception {
    this.askCarLeasingListCars();
  }
  
  /**
   * service: service.CarLeasing\lease
   * @throws Exception
   */
  public void askCarLeasingLease(String userName, Integer carId) throws Exception {
    System.out.println("now will run service: service.CarLeasing\\lease ..........");
    
    if (this.auto == 0) {
      System.out.println("welcome to lease a car, please input your name and carId you want to lease.");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("please input your name:");
      userName = reader.readLine();
      System.out.println("please input car id:");
      String carIdStr = reader.readLine();
      carId = Integer.valueOf(carIdStr);
    } else {
      System.out.println("now will call the service with default parameter: [userName]: " + userName + " [carId]: " + carId);
    }
    
    CarLeasingRequest request = CarLeasingRequest.newBuilder()
        .setUserName(userName)
        .setCarId(carId)
        .build();
    CarLeasingResponse response;

    try {
      CarLeasingGrpc.CarLeasingBlockingStub carLeasingBlockingStub = CarLeasingGrpc.newBlockingStub(this.channel);
      response = carLeasingBlockingStub.lease(request);
      // deal response
      if (response.getIsSuccess()) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[car]: " + response.getCar());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskCarLeasingLease() throws Exception {
    this.askCarLeasingLease("tom", 1);
    this.askCarLeasingLease("sam", 1);
  }
  
  
  /**
   * service: service.CarLeasing\giveBack
   * @throws Exception
   */
  public void askCarLeasingGiveBack(String userName, Integer carId) throws Exception {
    System.out.println("now will run service: service.CarLeasing\\giveBack ..........");
    
    if (this.auto == 0) {
      System.out.println("welcome to give back a car, please input your name and carId you want to give back.");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("please input your name:");
      userName = reader.readLine();
      System.out.println("please input car id:");
      String carIdStr = reader.readLine();
      carId = Integer.valueOf(carIdStr);
    } else {
      System.out.println("now will call the service with default parameter: [userName]: " + userName + " [carId]: " + carId);
    }
    
    CarGiveBackRequest request = CarGiveBackRequest.newBuilder()
        .setUserName(userName)
        .setCarId(carId)
        .build();
    CarGiveBackResponse response;

    try {
      CarLeasingGrpc.CarLeasingBlockingStub carLeasingBlockingStub = CarLeasingGrpc.newBlockingStub(this.channel);
      response = carLeasingBlockingStub.giveBack(request);
      // deal response
      if (response.getIsSuccess()) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[car]: " + response.getCar());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskCarLeasingGiveBack() throws Exception {
    this.askCarLeasingGiveBack("tom", 1);
    this.askCarLeasingGiveBack("sam", 1);
  }
  
  /**
   * service: service.CarLeasing\bill
   * @throws Exception
   */
  public void askCarLeasingBill(String userName) throws Exception {
    System.out.println("now will run service: service.CarLeasing\\bill ..........");
    
    if (this.auto == 0) {
      System.out.println("will show your bill, please input your name");
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      userName = reader.readLine();
    } else {
      System.out.println("now will call the service with default parameter: [userName]: " + userName);
    }
    
    BillRequest request = BillRequest.newBuilder()
        .setUserName(userName)
        .build();
    BillResponse response;

    try {
      CarLeasingGrpc.CarLeasingBlockingStub carLeasingBlockingStub = CarLeasingGrpc.newBlockingStub(this.channel);
      response = carLeasingBlockingStub.bill(request);
      // deal response
      if (response.getIsSuccess()) {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[totalPrice]: " + response.getTotalPrice());
        System.out.println("[everLeasingCars]: " + response.getEverLeasingCarsList());
      } else {
        System.out.println("[isSuccess]: " + response.getIsSuccess());
        System.out.println("[error]: " + response.getError());
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }
  
  /**
   * do auto testcase
   * @throws Exception
   */
  public void doTestAskCarLeasingBill() throws Exception {
    this.askCarLeasingBill("tom");
    this.askCarLeasingBill("sam");
  }
  
  /**
   * connect to register to find services
   * @throws Exception
   */
  public void connectForRegisterResp() throws Exception {
    String target = this.serviceHost + ":" + this.servicePort;
    System.out.println("connect for register resp...");
    this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      RegistryGrpc.RegistryBlockingStub registryBlockingStub = RegistryGrpc.newBlockingStub(this.channel);
      response = registryBlockingStub.getServices(request);
      System.out.println("please choose a service: ");
      for (int i = 0; i < response.getServicesCount(); i++) {
        System.out.println((i+1) + ", " + response.getServices(i));
      }
      
      // let user choose a service
      String selectedService = "";
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String chooseNumStr = reader.readLine();
      Integer chooseNum = Integer.valueOf(chooseNumStr);
      selectedService = response.getServices(chooseNum-1);
      System.out.println("you have choose service: " + selectedService);
      
      // find all servers
      FindServersReq findServersReq = FindServersReq.newBuilder().setServiceName(selectedService).build();
      ServerListRes serverListResponse;
      try {
        serverListResponse = registryBlockingStub.findServers(findServersReq);
        
        // find service config
        System.out.println("please choose connections: ");
        for (int j = 0; j < serverListResponse.getConnectionsCount(); j++) {
          System.out.println((j+1) + ", " + serverListResponse.getConnections(j));
        }
        
        String chooseConnNumStr = reader.readLine();
        Integer chooseConnNum = Integer.valueOf(chooseConnNumStr);
        System.out.println("you have choose connection: " + chooseConnNum);
        Connection selectedConnection = serverListResponse.getConnections(chooseConnNum - 1);
        
        // connect to service
        Client serviceClient = new Client(response.getServices(chooseNum-1), selectedConnection.getUri(), selectedConnection.getDiscoveryPort(), 0);
        serviceClient.connectForResp();
        
        // then return to this loop, until user exit
        Thread.sleep(3000);
        this.connectForRegisterResp();
      } catch (Exception e) {
        System.err.println("RPC failed: " + e);
        return;
      }
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }
}
