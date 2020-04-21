package beehive;
import beehive.Bee;
import beehive.Flower;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Scanner;
import java.util.Random;

/*Throughout the source code, I have made use of both enhanced for loops and Iterator
 *to access the ArrayList: beesArray
 *This is because when I was trying to remove a particular object(through its index)
 *Java was throwing a ConcurrentModificationException error
 *and output that this class is possibly making multiple concurrent modifications to beesArray
 *while it was still iterating. After searching on StackOverflow, it was pointed that
 *sometimes Java throws this exception even when a single thread is modifying the ArrayList.
 *Therefore, I had to opt for an Iterator*/



public class Beehive {
	
	//Global declaration of variables
	//Array Lists
	public static ArrayList<Bee> beesArray = new ArrayList<Bee>(1000);
	public static ArrayList<Flower> flowersArray = new ArrayList<Flower>(3);	
	//To be accessed on every new local scope.
	public static Iterator<Bee> iterator;
	
	public static int DayOfSimulation; 
	//Read from inputConfigValues()
	public static int[] configValues = new int[3];
	public static int configWorkers = 0;
	public static int totalEggsLaid, dailyEggsLaid = 0;
	public static int totalNectarObtained = 0;
	public static double honeyStock = 0;
	public static int numberOfWorkerBees = 0;
	public static int numberOfLarva = 0;
	public static int numberOfPupa = 0;
	public static int numberOfDrones = 0;
	public static int numberOfBirths = 0;
	public static int deathCount = 0;
	
 	public static void initBeesArray() {
		//Read the value given by simconfig.txt
		configWorkers = configValues[1];
		
		//If value is given, only then create instances of Bee class and assign to beesArray
		if (configWorkers > 0) {
			
			for (int i = 0; i < configWorkers; i++) {
				//Call the second constructor of Bee class with the BeeType passed as argument
				beesArray.add(new Bee("Worker"));
			}
			
		}
	}
	
	public static void resetFlowerArray() {
		//Remove all elements from previous day to avoid carry forward
		flowersArray.clear();
		//clear method is faster than removeAll as it does not deal with extra method calls
//		flowersArray.removeAll(flowersArray);
		
		//Instantiating flowersArray
		flowersArray.add(new Flower("Roses"));
		flowersArray.add(new Flower("Frangipani"));
		flowersArray.add(new Flower("Hibiscus"));
		
	}

	public static void emptyStomachOfAllBees() {
		
		for(Bee bee: beesArray) {
			//Reset attributes to 0 and false respectively
			bee.resetNectarCollectionSorties();
			bee.resetEaten();
		}
	}
	
	public static int layDailyEggs() {
		Random randomEggs = new Random();
		dailyEggsLaid = 0;
		final int MAX_EGGS = 50;
		final int MIN_EGGS = 10;
		
		dailyEggsLaid = randomEggs.nextInt((MAX_EGGS - MIN_EGGS) + 1) + MIN_EGGS;
		return dailyEggsLaid;//To pass as argument to addEggToHive
		
}

	public static void addEggtoHive(int eggs) {
		
		while (eggs > 0) {
			
			//Check if there is place in the hive	
			if(beesArray.size() >= 1000) {
			//Do nothing, ie discard surplus eggs and exit because
			//if eggs have not been put: infinite loop!(eggCount would still be > 0 )
			break;
			
			} else {
			//Add egg to hive by calling empty Bee constructor which sets attributes of an Egg
			beesArray.add(new Bee());
			eggs--;
		}
	}		
}
	
	public static void incrementAge() {
		
		//Increment age of all existing bees
		for(Bee bee: beesArray) {
			//Method found in Bee class
			bee.incrementAge();
		}	
	}

	public static void metamorphose() {
		
		//To use to calculate the probability of Drone/Worker emerging from Pupa
		Random random = new Random();
		double probability = 0;
		
	
		//Using Iterator
		iterator = beesArray.iterator();
		
		while(iterator.hasNext()){
			
           Bee bee = iterator.next();
           int age = bee.getBeeAge();
           
			if (age == 4) {
			//Type = larva
			bee.setBeeType("Larva");
			
			}else if(age == 10) {
			//Type = Pupa
			bee.setBeeType("Pupa");
			
			}else if(age == 20) {
			
			//Probability for a drone to emerge is 10%(As per area under curve, should be less than or equal to 10%)
			probability = random.nextDouble();
			
				if(probability <= 0.1) {
					//Drone Bee
					bee.setBeeType("Drone");
				} else {
					bee.setBeeType("Worker");
				}
		}
		
		//Call a funeral if the bee is of the following age
	     if( (age == 35 || age == 56) ) {
	       	   funeral(bee);      	   
	       }
      }
}

	public static void undertakerCheck() {
		
		//If larva or bees have not been fed, they die
		iterator = beesArray.iterator();	
		while(iterator.hasNext()){
			
            Bee bee = iterator.next();
			String beeType = bee.getBeeType();
			boolean hasEaten = bee.getEaten();
           
			if ( (beeType.equalsIgnoreCase("Larva")  && hasEaten == false) ||
				 (beeType.equalsIgnoreCase("Drone")  && hasEaten == false)||
				 (beeType.equalsIgnoreCase("Worker") && hasEaten == false)
		       )  {
				funeral(bee);
		      }
       }
}
	
	public static void funeral(Bee bee) {
		//Every time funeral is called, increment deathCount and remove the object of that bee in beesArray
		//Will remove the current object that iterator is pointing to
 		iterator.remove();
		deathCount++;		
}

 	public static void aDayPasses() {
 		
		emptyStomachOfAllBees();
		resetFlowerArray();
		addEggtoHive(layDailyEggs());
		incrementAge();
		metamorphose();
		AllWorkerBeesGardenSorties();
		feedingTime();
		undertakerCheck();
		printBeehiveStatus();
		logDailyStatusToFile(DayOfSimulation);
	}

 	public static void AllWorkerBeesGardenSorties() {
 		
 		//To execute 5 times
 		//Each time, all worker bees are made to visit flowers
 		//Remove all WORKER BEES and push them onto Stack workersLaunchChamber

 		//Pop each element from the Stack(while(!empty)) and then call the method visitFlower
 		//After visiting the flower, add that worker bee BACK to the workerBees array
 		
 		//Stack declaration of type Bee
 		Stack<Bee> workersLaunchChamber = new Stack<Bee>();
 		totalNectarObtained = 0;//For a particular day
 		int count = 0;
 		

 		for(int i = 0;i < 5; i++) {
 			
 			//Will report very low honey stock: Beehive will doom:investigate specification sheet
// 			totalNectarObtained = 0;//For each time
 			//Traverse through beesArray, extract all worker bees and push them onto workersLaunchChamber
 			iterator = beesArray.iterator();
 			while(iterator.hasNext()){
 	           Bee bee = iterator.next();
 	           String beeType = bee.getBeeType();
 	           
 	           if(beeType.equalsIgnoreCase("Worker")) {
 	        	   iterator.remove();
 	        	   workersLaunchChamber.push(bee);
 	           }

 	       }
// 			System.out.println("Size of beesArray after moving to stack " + beesArray.size());
 			//Stack full with worker bees objects
 			while(!workersLaunchChamber.empty()) {
 				
 				//Pop element, visit flower, add back to beesArray
 				Bee aWorkerBee = workersLaunchChamber.pop();//To restore back in beesArray
 				//Can now access the objects' properties through aWorkerBee
 				
 				//Visit flower
 				int nectarObtained = visitFlower();
 				
 				if(nectarObtained == 0) {
 					
 					//Retry a maximum number of 5 times
 					while(nectarObtained == 0 && count <= 5) {
						//Allow another flower type to be visited 5 times
						nectarObtained = visitFlower();
						count++;
					}
 				}
 				//Finally obtained nectar, update total counter
 				totalNectarObtained += nectarObtained;
 				
 				//Update aWorkerBee with nectarObtained then push
 				aWorkerBee.setNectarCollectionSorties(nectarObtained);
 				
 				//Add back to beesArray, will find its index by itself(contiguous)
 				beesArray.add(aWorkerBee);
 				//The size of the beesArray should be the same BEFORE removing, and after ADDING back

 			}
 			
// 			System.out.println("Size of beesArray after adding back " + beesArray.size());
 			
 		}
 		
 	}
 	
 	public static int visitFlower() {
 		//To return nectar obtained
 		int nectarObtained = 0;
 		
 		String[] flowerTypes = {"Roses", "Frangipani", "Hibiscus"};
		//Randomly choose depending on the length and so, each one has an equal chance of being selected
		String choice = flowerTypes[new Random().nextInt(flowerTypes.length)];
		
		if (choice.equalsIgnoreCase("Roses")) {
			//Modify flowersArray
			flowersArray.get(0).setAvailableNectarAfterVisit();
			nectarObtained = flowersArray.get(0).getNectarCollectionEachVisit();
			
		} 
		if(choice.equalsIgnoreCase("Frangipani")) {
			flowersArray.get(1).setAvailableNectarAfterVisit();
			nectarObtained = flowersArray.get(1).getNectarCollectionEachVisit();
		}
		if (choice.equalsIgnoreCase("Hibiscus")) {
			flowersArray.get(2).setAvailableNectarAfterVisit();
			nectarObtained = flowersArray.get(2).getNectarCollectionEachVisit();
		}

 		return nectarObtained;
 	}
 
 	public static void feedingTime() {
 		
 		//Get total nectar collected and convert to honey
 		double nectarToHoney = 0;
 		nectarToHoney = (totalNectarObtained / 40);
 		
 		//To load initial honey stock from config values!
		if(DayOfSimulation == 0) {
			honeyStock = configValues[2];
		}
		//honeyStock represents the total accumulated honey till date(or for 1 day?)
 		honeyStock += nectarToHoney;
 		
 		if(honeyStock < 0) {
 			System.out.print("Queen Bee did not have her quota of honey or there is not enough honey for the colony");
			System.exit(-1);
 		}else {
 			honeyStock -= 2;//For the Queen
 		}
 		
 		//Follow hierarchical order: LARVA, WORKERS, DRONES
 		for(Bee bee: beesArray) {
 			
 			String beeType = bee.getBeeType();
 			
 			//LARVA
 			if (beeType.equalsIgnoreCase("Larva")) {
				honeyStock -= 0.5;
				//Update hasEaten attribute: true
				bee.setHasEaten();
			}
 			if ( beeType.equalsIgnoreCase("Worker") || beeType.equalsIgnoreCase("Drone") ) {
				honeyStock -= 1;
				bee.setHasEaten();
			}
 		}
 }
 	
	public static void inputConfigValues() {
		
		String line = "";
		PrintWriter fileStream = null;
		Scanner stdInputScanner = new Scanner(System.in);
		
		do {
			System.out.println("simulationDays: ");
			configValues[0] = stdInputScanner.nextInt();
			
			if (configValues[0] <= 0 || configValues[0] > 500) {
				System.out.println("Cannot input 0 or a negative value for days. Maximum simulation days: 500");
			}
		}while(configValues[0] <= 0 || configValues[0] > 500);
		do {
			System.out.println("initWorkers: ");
			configValues[1] = stdInputScanner.nextInt();
			
			if (configValues[1] <= 0 || configValues[1] > 250) {
				System.out.println("Cannot admit too many workers or 0 or negative value. Maximum worker bees: 250");
			}
		} while (configValues[1] <= 0 ||configValues[1] > 250 );

		do {	
			System.out.println("initHoney: ");
			configValues[2] = stdInputScanner.nextInt();
			
			if (configValues[2] <= 0 || configValues[2] > 20000) {
				System.out.println("Cannot input 0 or a negative value for honey. Maximum honey: 20000");
			}
			
		} while (configValues[2] <= 0 || configValues[2] > 20000);

		stdInputScanner.close();
		//The reason behind 3 while loops is to ensure that the values are correctly entered 1 after the other.
		
		//Write to file
		try {
			fileStream = new PrintWriter(new FileOutputStream("simconfig.txt"));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("File not found!");
		}
		line =  "simulationDays " + configValues[0] + 
				"\ninitWorkers " + configValues[1] +
				"\ninitHoney " + configValues[2];
		fileStream.println(line);
		fileStream.close();
	}
	
	public static void printFlowerGarden() {
		
		System.out.print("Flower 1:Roses pollen stock: " + flowersArray.get(0).getCurrentNectarAvailable() + 
						 "\nFlower 2:Frangipani pollen stock: " + flowersArray.get(1).getCurrentNectarAvailable() + 
						 "\nFlower 3:Hibiscus pollen stock: " + flowersArray.get(1).getCurrentNectarAvailable() + "\n");
		
	}
	
	public static void countTypesOfBees() {
		//Initialize for each day
		numberOfBirths = 0;
		totalEggsLaid = 0;
		numberOfLarva = 0;
		numberOfPupa = 0;
		numberOfWorkerBees = 0;
		numberOfDrones = 0;
		
		iterator = beesArray.iterator();
		
		while(iterator.hasNext()){
			
	    Bee bee = iterator.next();
	    String beeType = bee.getBeeType();
	           
	    if(beeType.equalsIgnoreCase("Egg")) {
			totalEggsLaid++;
	     }
	    if (beeType.equalsIgnoreCase("Larva")) {
			numberOfLarva++;
		}
	    if (beeType.equalsIgnoreCase("Pupa")) {
			numberOfPupa++;
		}
	    if (beeType.equalsIgnoreCase("Worker")) {
	    	numberOfWorkerBees++;
		}
	    if (beeType.equalsIgnoreCase("Drone")) {
			numberOfDrones++;
		}
	    
	}
	//As observed by screenshot provided on coursework!
	numberOfBirths = numberOfLarva + numberOfPupa;
		
}

	public static void printBeehiveStatus() {
		//Called here to account for possible funerals before doing the count
		countTypesOfBees();
		System.out.print("Queen laid " + dailyEggsLaid + " eggs!" + 
						 "\nBeehive status\nEgg Count: "+ totalEggsLaid + "\nLarva Count: " + numberOfLarva + "\nPupa Count: " + numberOfPupa +
						 "\nWorker Count: "+ numberOfWorkerBees + "\nDrone Count: " + numberOfDrones + 
						 "\nDeath Count: " + deathCount + "\nBirth Count: "+ numberOfBirths +
						 "\nHoney Stock: " + honeyStock +"\n");
		printFlowerGarden();
}

	public static void logHeadingToFile() {
		//To be called once
		String heading = "";
		PrintWriter fileStream = null;
		try {
			fileStream = new PrintWriter(new FileOutputStream("simLog.csv"));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("File not found!");
		}
		heading = "Day;eggsLaid;eggInHive;Larva;Pupa;Worker;Drone;Death;Birth;HoneyStock;Flower1Nectar;Flower2Nectar;Flower3Nectar";
		fileStream.println(heading);
		fileStream.close();
	}
	
	public static void logDailyStatusToFile(int day) {
		String line = "";
		PrintWriter fileStream = null;
		try {
			fileStream = new PrintWriter(new FileOutputStream("simLog.csv", true));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("File not found!");
		}
		line = (day+1)+";"+dailyEggsLaid+";"+totalEggsLaid+";"+numberOfLarva+";"+numberOfPupa+";"+numberOfWorkerBees+";"
				+numberOfDrones+";"+deathCount+";"+numberOfBirths+";"+honeyStock+";"
				+flowersArray.get(0).getCurrentNectarAvailable()+";"
				+flowersArray.get(1).getCurrentNectarAvailable()+";"
				+flowersArray.get(2).getCurrentNectarAvailable();
		fileStream.println(line);
		fileStream.close();
		
}
	
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
			
		//Read from command line
		inputConfigValues();
		initBeesArray();
		logHeadingToFile();
		
		for (DayOfSimulation = 0; DayOfSimulation < configValues[0]; DayOfSimulation++) {
			System.out.println("*** This is day "+ (DayOfSimulation + 1) +" ***");
			aDayPasses();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			System.out.println();
		}
		System.out.println();

	}

}
