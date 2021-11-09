package code;

public class Node {
	private static int numberOfNodes;
	private Node parentNode;
	private String state;
	private short cost;
	
	public Node(Node parentNode, String state, short cost) {
		this.parentNode = parentNode;
		this.state = state;
		this.cost = cost;
	}
	
	public static int getNumberOfNodes() {
		return numberOfNodes;
	}
	public Node getParentNode() {
		return parentNode;
	}
	public String getState() {
		return state;
	}
	public short getCost() {
		return cost;
	}

	public String removeObject(short x, short y) {
		// TODO remove Agent, Hostage, Pill, Mutated hostage
		return "";
	}

	public static String[] extractGridSize(String state){
		String [] array  = state.split(";");
		String sizeGrid = array[0];
		String [] gridSize = sizeGrid.split(",",1);
		return gridSize;
	}

	public static String[] extractMaxNoOfCarry(String state){
		String [] array = state.split(";");
		String [] MaxCarryNo = new String[1];
		MaxCarryNo[0] = array[1];
		return MaxCarryNo;
	}

	public static String[] extractNeoPos(String state){
		String [] array = state.split(";");
		String posNeo = array[2];
		String [] neoPos = posNeo.split(",");
		return neoPos;
	}

	public static String[] extractTelBoothPos(String state){
		String [] array = state.split(";");
		String telPos = array[3];
		String [] telBoothPos = telPos.split(",");
		return telBoothPos;
	}

	public static String[] extractAgentsPos(String state){
		String [] array = state.split(";");
		String posAgents = array[4];
		String [] agentsPos = posAgents.split(",");
		return agentsPos;
	}

	public static String[] extractPillPos(String state){
		String [] array = state.split(";");
		String posPills = array[5];
		String [] pillPos = posPills.split(",");
		return pillPos;
	}

	public static String[] extractPadPos(String state){
		String [] array = state.split(";");
		String posPad = array[6];
		String [] padPos = posPad.split(",");
		return padPos;
	}

	public static String[] extractHostagesPos(String state){
		String [] array = state.split(";");
		String posHos = array[7];
		String [] hosPos = posHos.split(",");
		return hosPos;
	}

	public static String[] extractCarriedHostagesHP(String state){
		String [] array = state.split(";");
		String hpHos = array[8];
		String [] carriedHosHP = hpHos.split(",");
		return carriedHosHP;
	}

	public static String[] extractMutatedHostagesPos(String state){
		String [] array = state.split(";");
		String posMutatedHos = array[9];
		String [] mutatedHosPos = posMutatedHos.split(",");
		return mutatedHosPos;
	}

	public static boolean goalTest(String state){
		String [] neoPosDam = extractNeoPos(state);
		String [] neoPos = new String[2];
		neoPos[0] = neoPosDam[0];
		neoPos[1] = neoPosDam[1];
		String [] telBoothPos = extractTelBoothPos(state);
		String [] hostages = extractHostagesPos(state);
		String [] mutatedHostages = extractMutatedHostagesPos(state);
		if(neoPos.equals(telBoothPos) && hostages == null && mutatedHostages == null){
			return true;
		}
		else{
			return false;
		}
	}
}
