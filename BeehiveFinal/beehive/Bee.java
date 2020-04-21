package beehive;

public class Bee {
		
	public static int beeCount = 0;//Shared by all instances: all objects will report same number
	private int beeId;
	private int age;
	private String type;
	private int nectarCollectionSorties;
	private boolean eaten;
	private boolean alive;
	
	//Constructor method: no argument => egg
	public Bee() {
		//System.out.println("Empty Bee constructor called!");
		beeCount++;//Increment by 1 everytime an instance is created
		this.beeId = beeCount;
		//If age is set as -1, then it resulting .csv file will be same as Specification
		this.age = 0;
		this.type = "Egg";
		this.nectarCollectionSorties = 0;
		this.eaten = false;
		this.alive = true;
	}
	
	public Bee(String type) {
//		System.out.println("Worker Bee constructor called!");
		beeCount++;//Increment by 1 everytime an instance is created
		this.beeId = beeCount;
		this.age = 20;//Worker age
		this.type = type;
		this.nectarCollectionSorties = 0;
		this.eaten = false;
		this.alive = true;
		
	}
	
	public void incrementAge() {
		//Will increment age of a particular bee
		this.age++;
	}

	public void resetNectarCollectionSorties() {
		this.nectarCollectionSorties = 0;
	}
	
	public void resetEaten() {
		this.eaten = false;
	}
	
	public boolean getEaten() {
		return this.eaten;
	}
	
	public void setHasEaten() {
		this.eaten = true;
	}

	public int getBeeAge() {
		return this.age;
	}
	
	public int getBeeId() {
		return this.beeId;
	}
		
	public String getBeeType() {
		return this.type;
	}
	public void setBeeType(String type) {
		this.type = type;
	}

	public void setNectarCollectionSorties(int nectarObtained) {
		this.nectarCollectionSorties = nectarObtained;
	}
	
	public void display() {
		System.out.println("Bee ID: " + this.beeId +
						   "\nAge: " + this.age +
						   "\nType: " + this.type +
						   "\nNectar Collection Sorties: " + this.nectarCollectionSorties +
						   "\nHas eaten: " + this.eaten +
						   "\nIs alive: " + this.alive);
	}
}