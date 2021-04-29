package prototype.core;

import java.util.Random;
import main.Game;
import misc.Tools;
import prototype.being.Alloy;
import prototype.being.Ray;
import prototype.being.Bug;
import prototype.items.Inventory;
import prototype.room.*;

public class PrototypeGame extends Game {
	
	//end game condition
	public static boolean endGame = false;
	
    public void startGame() {
		//Create Rooms
        EntryRoom entryRoom = new EntryRoom();
        CockpitRoom cockpit = new CockpitRoom();
		EngineRoom engineRoom = new EngineRoom();
		CargoRoom cargoBay = new CargoRoom();

		//Create Layout
		entryRoom.doors.add(cockpit);
		entryRoom.doors.add(engineRoom);
		entryRoom.doors.add(cargoBay);
		cockpit.doors.add(entryRoom);
		engineRoom.doors.add(entryRoom);
		cargoBay.doors.add(entryRoom);
		cargoBay.doors.add(engineRoom);
		engineRoom.doors.add(cargoBay);

		//Create and Place Beings
		Ray ray = new Ray(entryRoom);
		Alloy alloy = new Alloy(cockpit);
        Bug bug = new Bug(cargoBay);

		//Update Room Beings List
		entryRoom.beings.add(ray);
		cockpit.beings.add(alloy);	
        cargoBay.getBeings().add(bug);



		//Instantiating Ray's Inventory:
		Inventory rayInventory = new Inventory();
		rayInventory.BasicInventory();
		
		//Introductory Text
        System.out.println("------------------------------------------------------------");
        System.out.println("You find yourself waking up on a vacant-looking space ship:");
        System.out.println("You are full of questions, what do you want to do?");
             

		makeChoice(ray, alloy, bug, rayInventory);

    }
    
	public static void endGame(BasicBeing being) {
		if(being instanceof Ray) {
			System.out.println("How sad, Ray's space adventures have come to an end!");			
		}
		else if(being instanceof Bug) {
			System.out.println("Our hero has saved the day, hurrah!");
			System.out.println("Now work can begin on all those ship feature"
					+ " requests you've been receiving from Alloy...");
		}
		endGame = true;
	}


	public void makeChoice(Ray ray, Alloy alloy, Bug bug, Inventory rayInventory) {
		
		while(!endGame) {
			
            //If the bug is present, there is a 50% chance the bug will attack either
            // Ray or Alloy each time makeChoice is called
            if (bug.getLocation() == ray.getLocation()) {
                Random rand = new Random();
                //decide if the bug will attack
                if (rand.nextInt(2) == 0) {
                	//decide if it will attack Ray or Alloy
                	if (alloy.getLocation()== ray.getLocation()){
                		if(rand.nextInt(2) == 0) {
                    		System.out.println("The bug just bit you! Ouch!");
                            ray.changeHealth(-10);
                    	}
                    	else {
                    		System.out.println("The bug just bit Alloy! Ouch!");
                            alloy.changeHealth(-10);
                    	}
                		
                	}
                	else {
                		System.out.println("The bug just bit you! Ouch!");
                		ray.changeHealth(-10);
                	}
                    
                }
            }
            
            if (ray.getHealth() > 0 && bug.getHealth() > 0) {
				
				System.out.println("------------------------------------------------------------");
				System.out.println("What do you want to do?");
				System.out.println("1 - Ask - 'Who am I'");
				System.out.println("2 - Ask - 'Where am I?'");
				System.out.println("3 - Inspect the room");
				System.out.println("4 - Inspect the items you are carrying");
				System.out.println("5 - Go to another room.");
	
	
	      		// this will print out interaction options if there is another being in the room
		        int choiceNumber = 6;
		        
		        for (int i = 0; i < ray.getLocation().getBeings().size(); i++) {
		        	// make sure we only try to interact with a being that isn't Ray
		        	if(!(ray.getLocation().getBeings().get(i) instanceof Ray)) {
		        		System.out.println(choiceNumber + " - Interact with " + 
		        				ray.getLocation().getBeings().get(i).getName());
		        		choiceNumber++;
		        	}
		        System.out.println();
		     
		        		
		        }
				System.out.println();
				int choice = Tools.getWholeNumberInput();
				System.out.println();
				
				if(choice ==1) {
					System.out.println("Your name is " + ray.getName() + "\n");
				}
				
				else if(choice==2) {
					System.out.println("You see a plaque near the door that says this room is called ");
					ray.getLocation().printName();
					ray.getLocation().printDescription();
					System.out.println();
				}
	
				//Displays the contents of the room's inventory
				//This needs to somehow determine both Ray's location and call the array for that location
				else if(choice==3) {
					System.out.println("This room contains:");
					for (int i = 0; i < ray.getLocation().items.size(); i++) {
						System.out.print("    ");
	
						//This is a debugging print and won't be needed later on if we can work out
						//how to safely implement an inventory management system that takes
						//into account arrays starting with 0
						System.out.print(i + ": ");
	
						//This prints out the item at the i location
						System.out.println(ray.getLocation().items.get(i));
					}
	
					//rayInventory.InventoryList();
					System.out.println();
				}
				
				// Displays the contents of Ray's inventory.
				else if(choice==4) {
					rayInventory.InventoryList();
					System.out.println();
				}
				
				else if(choice==5) {
					
					if(ray.getLocation().getDoors().size() == 1) {
						System.out.println("You see only 1 door from this room.");
					}
					else {
						System.out.println("You see " + ray.getLocation().getDoors().size() + " doors.");
					}
					System.out.println("Please make a selection.");
					System.out.println("1: Stay in this room.");
					for(int i=0; i < ray.getLocation().getDoors().size(); i++) {
						System.out.print((i+2) + ": ");
						ray.getLocation().getDoors().get(i).printName();
					}
					int temp = 100;
					do {
						System.out.println();
						temp = Tools.getWholeNumberInput();
					}
					while (temp > (ray.getLocation().getDoors().size() + 1));
					if(temp == 1) {
						continue;
					}
					else {
						BasicRoom tempRoom = ray.getLocation();
						ray.changeLocation(ray.getLocation().getDoors().get(temp-2));
						tempRoom.removeBeing(ray);
						ray.getLocation().addBeing(ray);
						
	                    // move Alloy with Ray if Alloy is in the same room and if stayPut is false
						if (alloy.getLocation().equals(tempRoom)) {
							if(!alloy.getStayPut()) {
								alloy.changeLocation(ray.getLocation());
								tempRoom.removeBeing(alloy);
								ray.getLocation().addBeing(alloy);
							}
						}
						
	                    // move the bug with Ray if the bug is in the same room
	                    if (bug.getLocation().equals(tempRoom)) {
	                        bug.changeLocation(ray.getLocation());
	                        tempRoom.removeBeing(bug);
	                        ray.getLocation().addBeing(bug);
	                        System.out.println("How frightening! The bug followed you through the door!");
	                        
	                    }
					}
				}
				
				// check if the choice is to interact with the other being
				else if(choice <= choiceNumber) {
					// don't interact with Ray in the room's being list
					if(ray.getLocation().getBeings().get(choice-6) instanceof Ray) {
						ray.getLocation().getBeings().get(choice-5).interact();
					}
					else {
						ray.getLocation().getBeings().get(choice-6).interact();
					}
					
				}
				
				
				else {
					System.out.println("You must be confused, that isn't an option.");
				}
			}
		}

	}
}
