package beehive;

public class Flower {
	
		private String flowerName;
		private int nectarCollectionEachVisit;
		private int dailyNectarProduction;
		private int currentNectarAvailable;
		
		public Flower(String flowerName) {
			//System.out.println("Flower Constructor Called!");
			this.flowerName = flowerName;
			
			switch (flowerName) {
			case "Roses":
				this.nectarCollectionEachVisit = 20;
				this.dailyNectarProduction = 20000;
				this.currentNectarAvailable = 20000;
				break;
			case "Frangipani":
				this.nectarCollectionEachVisit = 50;
				this.dailyNectarProduction = 50000;
				this.currentNectarAvailable = 50000;
				break;
			case "Hibiscus":
				this.nectarCollectionEachVisit = 10;
				this.dailyNectarProduction = 10000;
				this.currentNectarAvailable = 10000;
				break;
			default:
				System.out.println("Incorrect flower name!");
				break;
			}
		}
		

		public int getCurrentNectarAvailable() {
			return this.currentNectarAvailable;
		}
		
		public int getNectarCollectionEachVisit() {
			return this.nectarCollectionEachVisit;
		}

		public void setAvailableNectarAfterVisit() {

			int nectarCollectionEachVisit = getNectarCollectionEachVisit();
			
			this.currentNectarAvailable -= nectarCollectionEachVisit;
		}
		
		public void display() {
			System.out.println("The flower " + this.flowerName + 
							   "\n has nectar each visit " + this.nectarCollectionEachVisit +
							   "\n daily nectar production of " + this.dailyNectarProduction +
							   "\n current nectar available is " + this.currentNectarAvailable);
		}
}
