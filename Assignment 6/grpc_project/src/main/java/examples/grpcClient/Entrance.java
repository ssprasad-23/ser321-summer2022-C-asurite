package example.grpcclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * entrance class
 */
public class Entrance {

  /**
   * service target, just like service.Echo/parrot
   */
  private String target;
  
  /**
   * service name index
   */
  private Integer serviceIndex;
  
  private String serviceHost = "localhost";
  private Integer servicePort = 8000;
  private String registryHost = "localhost";
  private Integer grpcPort = 9002;
  private Boolean regOn = false;
  private Integer auto = 0;
  
  private Client serviceClient;
  private Client registerClient;
  
  /**
   * service list
   */
  private String[] serviceList = new String[] {
    "services.Weather/atCoordinates", "services.Weather/inCity", "services.Weather/listCities",
    "services.Timer/start", "services.Timer/check", "services.Timer/close", "services.Timer/list",
    "services.CarLeasing/listCars", "services.CarLeasing/lease", "services.CarLeasing/giveBack", "services.CarLeasing/bill"
  };
  
  /**
   * ui for user to choose service
   */
  public void chooseService() throws Exception {
    // Reading data using readLine
    System.out.println("Here are services we provide, please choose: ");
    for (int i = 0; i < this.serviceList.length; i++) {
      System.out.println((i+1) + ", " + this.serviceList[i]);
    }
    
    // ask the user for what he wants
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String num = reader.readLine();
    try {
      Integer serviceNum = Integer.valueOf(num);
      switch (serviceNum) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
          this.serviceIndex = serviceNum - 1;
          System.out.println("you have choose service: " + this.serviceList[this.serviceIndex]);
          this.startGrpcClient();
          
          Thread.sleep(3000);
          System.out.println("service response ended, return to beginning.......");
          this.chooseService();
          break;
        default:
          System.out.println("start to exit, goodbye.");
          System.exit(0);
          break;
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("start to exit, goodbye.");
      System.exit(0);
    }
  }
  
  /**
   * run all services auto with hardcode test data
   */
  public void runAllServicesAuto() throws Exception {
	  for (int i = 0; i < this.serviceList.length; i++) {
	    Thread.sleep(3000);
	    System.out.println("----------------------------------------------------------------");
      this.serviceIndex = i;
      this.startGrpcClient();
    }
  }
  
  /**
   * start the grpc client, depend on args and user choice
   */
  public void startGrpcClient() {
    if (!this.regOn) {
      // no need to find register, use local service
      System.out.println("start grpc client...");
      this.serviceClient = new Client(this.serviceList[this.serviceIndex], this.serviceHost, this.servicePort, this.auto);
      try {
        this.serviceClient.connectForResp();
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
    } else {
      // find service through register
      System.out.println("welcome to connect to register...");
      this.registerClient = new Client("register", this.registryHost, this.grpcPort, this.auto);
      try {
        this.registerClient.connectForRegisterResp();
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
 
  /**
   * main for program entrance
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 6) {
      System.out.println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <regOn(bool)> <auto(int)>");
      System.exit(1);
    }
    
    Entrance entrance = new Entrance();
    try {
      entrance.serviceHost = args[0];
      entrance.servicePort = Integer.parseInt(args[1]);
      entrance.registryHost = args[2];
      entrance.grpcPort = Integer.parseInt(args[3]);
      entrance.regOn = Boolean.valueOf(args[4]);
      entrance.auto = Integer.valueOf(args[5]);
    } catch (NumberFormatException nfe) {
      System.out.println("please check args format.");
      System.exit(2);
    }
    
    // if auto set to 1, then the problem will run itself with hardcode
    if (!entrance.regOn) {
      if (entrance.auto == 0) {
        System.out.println("welcom using grpc user client");
          entrance.chooseService();
      } else {
        System.out.println("now will run all services with default parameters...");
        entrance.runAllServicesAuto();
      }
    } else {
      entrance.startGrpcClient();
    }
  }
}
