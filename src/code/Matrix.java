package code;

import java.util.Random;

public class Matrix extends GeneralSearch {

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
		grid += maxCarry + ";" + neoX + "," + neoY + "," + 0 + ";";
		gridArray[neoX][neoY] = true;
		numberOfEmptyCells-=1;

		// Choose a random position to spawn the Telephone booth given the grid boundaries.
		int telephoneX ,telehoneY = 0;

		while (true) {
			telephoneX = rand.nextInt(M);
			telehoneY = rand.nextInt(N);
			if (!gridArray[telephoneX][telehoneY]) {
				gridArray[telephoneX][telehoneY] = true;
				numberOfEmptyCells-=1;
				grid += telephoneX + "," + telehoneY + ";";
				break;
			}
		}

		// Generate Hostages
		int numberOfHostages = rand.nextInt(6) + 4;
		numberOfEmptyCells -= numberOfHostages;

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

		// Generate Pills
		int numberOfPills = rand.nextInt(numberOfHostages) + 1;
		numberOfEmptyCells -= numberOfPills;

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

		int numberOfAgents = (numberOfEmptyCells <= numberOfHostages*2) ? (rand.nextInt(numberOfEmptyCells) + 1) : (rand.nextInt(numberOfHostages) + numberOfHostages);
		numberOfEmptyCells -= numberOfAgents;

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

		int numberOfPadPairs = (numberOfEmptyCells >= 2) ? (Math.max(Math.min(rand.nextInt(numberOfEmptyCells)/ 2, 6), 1)) : 0;

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
							break;
						}
					}
				}
			}
			grid += (numberOfPadPairs == i+1) ? ";" : ",";
		}
		grid += ";"; // space to add carried Hostages and Hostages mutated to Agents.

		return grid;
	}

	public static void solve(String grid, String strategy, boolean visualize) {
		Matrix matrix = new Matrix();
		matrix.Search("", "");
	}

	@Override
	public Node Search(String grid, String str) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100 ; i++)
			System.out.println(Matrix.genGrid());
	}
}
