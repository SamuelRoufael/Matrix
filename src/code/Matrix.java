package code;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Matrix extends GeneralSearch {

	private static final String[] operators = {"Up","Down","Right","Left","Kill","TakePill","Carry","Drop","Fly"};
	private static int deaths = 0;

	public static String genGrid() {
		Random rand = new Random();

		String grid = ""; // output

		// Initialize Dimensions of the Grid
		int M = rand.nextInt(11) + 5;
		int N = rand.nextInt(11) + 5;
		grid += M + "," + N + ";";

		// Variables used to avoid clashes and overlapping
		boolean [][] gridArray = new boolean [M][N]; // Used to keep track of the used cell to avoid spawning different object in the same cell
		int numberOfEmptyCells = (M*N); // a variable to keep track of the number of the available cells

		// Choose a random position to spawn Neo given the grid boundaries and Neo's variables.
		int neoX = rand.nextInt(M);
		int neoY = rand.nextInt(N);
		int maxCarry = rand.nextInt(4) + 1; // Max number Neo can Carry
		grid += maxCarry + ";" + neoX + "," + neoY + ";";
		gridArray[neoX][neoY] = true;
		numberOfEmptyCells-=1;

		// Choose a random position to spawn the Telephone booth given the grid boundaries.
		int telephoneX ,telephoneY = 0;

		while (true) {
			telephoneX = rand.nextInt(M);
			telephoneY = rand.nextInt(N);
			if (!gridArray[telephoneX][telephoneY]) {
				gridArray[telephoneX][telephoneY] = true;
				numberOfEmptyCells-=1;
				grid += telephoneX + "," + telephoneY + ";";
				break;
			}
		}

		// Generate a Random number of Hostages
		int numberOfHostages = rand.nextInt(6) + 4;
		numberOfEmptyCells -= numberOfHostages;

		// Generate a Random number of Pills
		int numberOfPills = rand.nextInt(numberOfHostages) + 1;
		numberOfEmptyCells -= numberOfPills;

		// Generate a Random number of Agents
		int numberOfAgents = (numberOfEmptyCells <= numberOfHostages*2) ? (rand.nextInt(numberOfEmptyCells) + 1) : (rand.nextInt(numberOfHostages) + numberOfHostages);
		numberOfEmptyCells -= numberOfAgents;

		// Generate a Random number of Pads
		int numberOfPadPairs = (numberOfEmptyCells >= 2) ? (Math.max(Math.min(rand.nextInt(numberOfEmptyCells)/ 2, 6), 1)) : 0;

		int agentX, agentY;
		for (int i = 0 ; i < numberOfAgents ; i++) {
			while (true) {
				agentX = rand.nextInt(M);
				agentY = rand.nextInt(N);
				if (!gridArray[agentX][agentY]) {
					gridArray[agentX][agentY] = true;
					grid += agentX + "," + agentY;
					break;
				}
			}
			grid += (numberOfAgents == i+1) ? ";" : ",";
		}

		int pillX, pillY;
		for (int i = 0 ; i < numberOfPills ; i++) {
			while (true) {
				pillX = rand.nextInt(M);
				pillY = rand.nextInt(N);
				if (!gridArray[pillX][pillY]) {
					gridArray[pillX][pillY] = true;
					grid += pillX + "," + pillY;
					break;
				}
			}
			grid += (numberOfPills == i+1) ? ";": ",";
		}

		int padX1, padY1, padX2, padY2;
		boolean pairMatched = false;
		for (int i = 0 ; i < numberOfPadPairs ; i++) {
			pairMatched = false;
			while (!pairMatched) {
				padX1 = rand.nextInt(M);
				padY1 = rand.nextInt(N);
				if (!gridArray[padX1][padY1]) {
					gridArray[padX1][padY1] = true;
					while (true) {
						padX2 = rand.nextInt(M);
						padY2 = rand.nextInt(N);
						if (!gridArray[padX2][padY2]) {
							gridArray[padX2][padY2] = true;
							pairMatched = true;
							grid += padX1 + "," + padY1 + "," + padX2 + "," + padY2;
							grid += "," + padX2 + "," + padY2 + "," + padX1 + "," + padY1;
							break;
						}
					}
				}
			}
			grid += (numberOfPadPairs == i+1) ? ";" : ",";
		}

		int hostageX, hostageY, hostageDamage;
		for (int i = 0 ; i < numberOfHostages ; i++) {
			while (true) {
				hostageX = rand.nextInt(M);
				hostageY = rand.nextInt(N);
				if (!gridArray[hostageX][hostageY]) {
					hostageDamage = rand.nextInt(99) + 1;
					gridArray[hostageX][hostageY] = true;
					grid += hostageX + "," + hostageY + "," + hostageDamage;
					break;
				}
			}
			grid += (numberOfHostages == i+1) ? ";": ",";
		}
		return grid;
	}

	public static void solve(String grid, String strategy, boolean visualize) {
		Matrix matrix = new Matrix();
		Node initialNode = createInitialNode(grid);
		matrix.Search(initialNode,"");
	}

	@Override
	public Node Search(Node node, String operation) {
		return null;
	}

	public static Node createInitialNode(String grid) {
		String [] gridArray = grid.split(";",10);
		gridArray[2] = gridArray[2] + ",0";
		String state = String.join(";", gridArray) + ";";
		return new Node(null, state);
	}

	public static void printArray(String [] array) {
		for (String s: array) {
			System.out.println(s);
		}
	}

	public static String Carry(Node node){
		String state = node.getState();
		String [] arrayState = state.split(";", 10);
		String [] hostages = node.extractHostages();
		String [] neoPosition = node.extractNeoPos();
		String newHostages = "";
		for(int i=0;i<hostages.length-2; i+=3) {
			String xHostage = hostages[i];
			String yHostage = hostages[i+1];
			String hostageDamage = hostages[i+2];

			if(xHostage.equals(neoPosition[0]) && yHostage.equals(neoPosition[1])) {
				if (!arrayState[8].isEmpty())
					arrayState[8] += ",";
				arrayState[8] += hostageDamage;
			}
			else {
				newHostages += xHostage + "," + yHostage + "," + hostageDamage + ",";
			}
		}
		newHostages = newHostages.substring(0,newHostages.length() - 1);
		arrayState[7] = newHostages;

		return String.join(";", arrayState);
	}

	public static String Drop(Node node){
		String state = node.getState();
		String[] stateArray = state.split(";", 10);
		String[] neoPosition = node.extractNeoPos();
		String[] teleBoothPosition = node.extractTelBoothPos();
		String[] carriedHostages = node.extractCarriedHostagesHP();
		boolean neoAtTeleBooth = false;
		for(int i=0;i<2;i++){
			if(Integer.parseInt(neoPosition[i]) == Integer.parseInt(teleBoothPosition[i]))
				neoAtTeleBooth = true;
		}
		if(neoAtTeleBooth){
			if(carriedHostages != null) {
				for (String carriedHostage : carriedHostages)
					if (Integer.parseInt(carriedHostage) == 100)
						deaths += 1;
				stateArray[8] = "";
			}
		}
		return String.join(";", stateArray);
	}

	public static String Kill(Node node) {
		String [] stateArray = node.getState().split(";",10);
		ArrayList<String> agents = new ArrayList<String>(Arrays.asList(node.extractAgentsPos()));
		ArrayList<String> mutatedHostages = new ArrayList<String>(Arrays.asList(node.extractMutatedHostagesPos()));
		String [] neo = node.extractNeoPos();
		boolean killedSomeOne = false;

		for (int i = 0 ; i < mutatedHostages.size() - 1 ; i+=2) {
			if (neo[0].equals(mutatedHostages.get(i)) && neo[1].equals(mutatedHostages.get(i+1))) {
				mutatedHostages.remove(i);
				mutatedHostages.remove(i+1);
				i-=2;
				killedSomeOne = true;
			}
		}

		for (int i = 0 ; i < agents.size() - 1 ; i+=2) {
			if (neo[0].equals(agents.get(i)) && neo[1].equals(agents.get(i+1))) {
				agents.remove(i);
				agents.remove(i+1);
				i-=2;
				killedSomeOne = true;
			}
		}

		// Update Neo's and all living hostages (Either carried by Neo or somewhere in the gird) if neo killed someone.
		if (killedSomeOne) {
			int neoDamage = Integer.parseInt(neo[2]);
			neoDamage += 20;
			neo[2] = neoDamage + "";
			stateArray[2] =  String.join(",", neo);

			ArrayList<String> hostages = new ArrayList<String>(Arrays.asList(node.extractHostages()));
			for (int i = 0 ; i < hostages.size() - 2 ; i+=3) {
				int damage = Integer.parseInt(hostages.get(i+2));
				damage += 20;
				if (damage < 100) {
					hostages.set(i+2,damage+"");
				}
				else {
					mutatedHostages.add(hostages.remove(i));
					mutatedHostages.add(hostages.remove(i+1));
					hostages.remove(i+2);
					i-=3;
				}
			}
			String newHostages = String.join(",", hostages);
			stateArray[7] = newHostages;

			ArrayList<String> carriedHostages = new ArrayList<String>(Arrays.asList(node.extractCarriedHostagesHP()));
			for (int i = 0 ; i < carriedHostages.size() ; i++) {
				int damage = Integer.parseInt(carriedHostages.get(i));
				damage = Math.min(100, damage + 20);
				carriedHostages.set(i, damage+"");
			}
			stateArray[8] = String.join(";", carriedHostages);
		}

		String newAgentsString = String.join(",", agents);
		String newMutatedHostagesString = String.join(",", mutatedHostages);
		stateArray[4] = newAgentsString;
		stateArray[9] = newMutatedHostagesString;

		return String.join(";", stateArray);
	}

	public static String TakePill(Node node){
		String state = node.getState();
		String[] pillPosition = node.extractPillPos();
		String[] neoPosition = node.extractNeoPos();
		String[] hostages = node.extractHostages();
		String[] carriedHostages = node.extractCarriedHostagesHP();
		String[] arrayState = state.split(";", 10);
		String newPills = "";
		for(int i=0;i<pillPosition.length-1;i+=2){
			String pillX = pillPosition[i];
			String pillY = pillPosition[i+1];
			if(pillX.equals(neoPosition[0]) && pillY.equals(neoPosition[1])){
				for(int j=0; j<carriedHostages.length;j++){
					if(Integer.parseInt(carriedHostages[j]) > 20)
						carriedHostages[j] = (Integer.parseInt(carriedHostages[j]) - 20) + "";
					else
						carriedHostages[j] = "0";
				}
				for(int j=0;j<hostages.length-2;j+=3){
					System.out.println(hostages[j] + " " + hostages[j+1] + " " + hostages[j+2]);
					if(Integer.parseInt(hostages[j+2]) > 20)
						hostages[j+2] = (Integer.parseInt(hostages[j+2]) - 20) + "";
					else
						hostages[j+2] = "0";
				}
				if(Integer.parseInt(neoPosition[2]) > 20)
					neoPosition[2] = (Integer.parseInt(neoPosition[2]) - 20) + "";
				else
					neoPosition[2] = "0";

			}
			else
				newPills += pillX + ',' + pillY + ',';
			arrayState[2] = String.join(",", neoPosition);
			arrayState[7] = String.join(",", hostages);
			arrayState[8] = String.join(",", carriedHostages);
			arrayState[5] = newPills;

		}
		return String.join(";", arrayState);
	}

	public static String Fly(Node node){
		String state = node.getState();
		String[] neoPosition = node.extractNeoPos();
		String[] padsPosition = node.extractPadPos();
		String[] arrayState = state.split(";", 10);
		for(int i=0; i<padsPosition.length-1;i+=4){
			String padX1 = padsPosition[i];
			String padY1 = padsPosition[i+1];
			String padX2 = padsPosition[i+2];
			String padY2 = padsPosition[i+3];
			if(neoPosition[0].equals(padX1) && neoPosition[1].equals(padY1)){
				neoPosition[0] = padX2;
				neoPosition[1] = padY2;
				break;
			}
			else if(neoPosition[0].equals(padX2) && neoPosition[1].equals(padY2)){
				neoPosition[0] = padX1;
				neoPosition[1] = padY1;
				break;
			}
		}
		arrayState[2] = String.join(",", neoPosition);
		return String.join(";", arrayState);
	}

	public static Node Expand(Node node){
		for (String operator : operators) {
			if (operator.equals("Carry")) {
				//remove the hostage from the state and add the damage to the end of the state
				String state = node.getState();
				//check if neo reached maximum carry capacity
				String[] carriedHostages = node.extractCarriedHostagesHP();
				String maxCarry = node.extractMaxNoOfCarry();
				if(carriedHostages.length == Integer.parseInt(maxCarry))
					break;
				String newState = Carry(node);
				if(!state.equals(newState))
					return new Node(node, newState);
			}
			if(operator.equals("Drop")){
				String state = node.getState();
				String newState = Drop(node);
				if(!state.equals(newState))
					return new Node(node, newState);
			}
			if(operator.equals("Kill")){
				Kill(node);
			}
			if(operator.equals("TakePill")){
				String state = node.getState();
				String newState = TakePill(node);
				if(!state.equals(newState))
					return new Node(node, newState, (short) 0);
			}
			if(operator.equals("Fly")){
				String state = node.getState();
				String newState = Fly(node);
				if(!state.equals(newState))
					return new Node(node, newState, (short) 0);
			}
		}
		return node;
	}

	public static boolean goalTest(Node node){
		String [] neoPosDam = node.extractNeoPos();
		String [] neoPos = new String[2];
		neoPos[0] = neoPosDam[0];
		neoPos[1] = neoPosDam[1];
		String [] telBoothPos = node.extractTelBoothPos();
		String [] hostages = node.extractHostages();
		String [] mutatedHostages = node.extractMutatedHostagesPos();

		// TODO : fix logical error here.
		if(neoPos.equals(telBoothPos) && hostages == null && mutatedHostages == null){
			return true;
		}
		else{
			return false;
		}
	}

	public static void main(String[] args) {
		String grid = genGrid();
		Node initialNode = createInitialNode(grid);
		String testString = "8,9;1;2,2,0;2,2;7,3,1,0,7,2,4,5,1,7,5,3,5,4,3,8,6,4,3,1;6,8,3,5,2,8,7,5;2,2,20,8,0,8,4,7,1,8,6,1,6,1,1,8,2,6,1,5,1,5,2,6,7,4,6,0,6,0,7,4,6,5,7,8,7,8,6,5,4,1,5,8,5,8,4,1;5,0,69,2,5,94,1,4,8,3,7,37,1,1,54;95;";
//		System.out.println(Carry(node));
//		System.out.println(Drop(node));
//		System.out.println(initialNode.getState());
//		System.out.println(Kill(initialNode));
	}
}
