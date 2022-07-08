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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;


// Implement the weather service. It has three sevices atCoordinates, inCity, listCities
class WeatherImpl extends WeatherGrpc.WeatherImplBase {
	
	// inner class for cityCondition
	class CityCondition {
		private String cityName;
		private Double lattitude;
		private Double longitude;
		private Double currentTemp;
		private String currentConditions;
		private Double[] dailyHighs;
		
		public CityCondition(String cityName, Double lattitude, Double longitude, Double currentTemp, String currentConditions, Double[] dailyHighs) {
			this.cityName = cityName;
			this.lattitude = lattitude;
			this.longitude = longitude;
			this.currentTemp = currentTemp;
			this.currentConditions = currentConditions;
			this.dailyHighs = dailyHighs;
		}
			
		public String getCityName() {
			return cityName;
		}
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}
		public Double getLattitude() {
			return lattitude;
		}
		public void setLattitude(Double lattitude) {
			this.lattitude = lattitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Double getCurrentTemp() {
			return currentTemp;
		}
		public void setCurrentTemp(Double currentTemp) {
			this.currentTemp = currentTemp;
		}
		public String getCurrentConditions() {
			return currentConditions;
		}
		public void setCurrentConditions(String currentConditions) {
			this.currentConditions = currentConditions;
		}
		public Double[] getDailyHighs() {
			return dailyHighs;
		}
		public void setDailyHighs(Double[] dailyHighs) {
			this.dailyHighs = dailyHighs;
		}
	}
    
  // global map city weather condition datas
  Map<String, CityCondition> cityConditions = new HashMap<>();
  
  public WeatherImpl(){
      super();
      
      // creating some fake city condition datas
      cityConditions.put("New York", new CityCondition("New York", 40.0, -74.0, 59.0, "the weather is cool, please have a good rest.", new Double[] {62.0, 64.0, 57.0, 66.0, 60.0, 58.0, 62.0}));
      cityConditions.put("London", new CityCondition("London", 51.0, 0.0, 55.4, "the weather is cool, please have a good rest.", new Double[] {56.0, 58.0, 57.0, 50.0, 52.0, 56.0, 60.0}));
      cityConditions.put("Tokyo", new CityCondition("Tokyo", 35.0, 140.0, 89.0, "the weather is hot, please avoid heatstroke.", new Double[] {89.0, 92.4, 98.0, 100.7, 103.0, 95.2, 92.0}));
      cityConditions.put("Paris", new CityCondition("Paris", 49.0, 2.0, 60.8, "the weather is cool, please have a good rest.", new Double[] {62.0, 65.7, 57.8, 66.0, 62.0, 58.0, 62.0}));
      cityConditions.put("Sydney", new CityCondition("Sydney", -34.0, 150.0, 32.0, "the weather is cold, please avoid catching cold.", new Double[] {32.0, 30.0, 28.4, 26.3, 30.0, 32.0, 34.0}));
  }
    
  // For a certain coordinate, check the city weather condition
  @Override
  public void atCoordinates(WeatherCoordinateRequest req, StreamObserver<WeatherResponse> responseObserver) {
    System.out.println("Received from client. Start to get weather condition.");
    System.out.println("lattitude: " + req.getLatitude());
    System.out.println("longitude: " + req.getLongitude());
    CityCondition neededCityCondition = null;
    WeatherResponse.Builder response = WeatherResponse.newBuilder();
    if (!cityConditions.isEmpty()) {
    	for (Map.Entry<String, CityCondition> entry : cityConditions.entrySet()) {
    		CityCondition cityCondition = (CityCondition) entry.getValue();
    		if (cityCondition.getLattitude().equals(req.getLatitude())
    		&& cityCondition.getLongitude().equals(req.getLongitude())) {
    			neededCityCondition = cityCondition;
    			break;
    		}
    	}
    }
    
    if (null != neededCityCondition) {
    	response.setIsSuccess(true);
    	response.setError("");
    	response.setCurrentTemp(neededCityCondition.getCurrentTemp());
    	response.setCurrentConditions(neededCityCondition.getCurrentConditions());
    	response.addAllDailyHighs(Arrays.asList(neededCityCondition.getDailyHighs()));
    } else {
    	response.setIsSuccess(false);
    	response.setError("can not find city condition.");
    }
    WeatherResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }

  // get certain city condition by city name
  @Override
  public void inCity(WeatherCityRequest req, StreamObserver<WeatherResponse> responseObserver) {
  	System.out.println("Received from client. City Name is: " + req.getCityName());
  	WeatherResponse.Builder response = WeatherResponse.newBuilder();
  	CityCondition neededCityCondition = null;
  	if (!cityConditions.isEmpty()) {
  		neededCityCondition = cityConditions.get(req.getCityName());
  	}
  	
  	if (null != neededCityCondition) {
  		response.setIsSuccess(true);
    	response.setError("");
    	response.setCurrentTemp(neededCityCondition.getCurrentTemp());
    	response.setCurrentConditions(neededCityCondition.getCurrentConditions());
    	response.addAllDailyHighs(Arrays.asList(neededCityCondition.getDailyHighs()));
  	} else {
  		response.setIsSuccess(false);
  		response.setError("there is this city data now.");
  	}
	
  	WeatherResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }

  // check all the city names in city condition datas
  @Override
  public void listCities(com.google.protobuf.Empty req, StreamObserver<CitiesResponse> responseObserver) {
  	System.out.println("Received from client. Show all city list...");
  	CitiesResponse.Builder response = CitiesResponse.newBuilder();
  	if (!cityConditions.isEmpty()) {
  		response.setIsSuccess(true);
  		response.setError("");
  		response.addAllCityName(cityConditions.keySet());
  	} else {
  		response.setIsSuccess(false);
  		response.setError("there are no city names datas now.");
  	}
  	CitiesResponse resp = response.build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}