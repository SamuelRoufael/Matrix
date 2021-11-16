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

	public String[] extractGridSize(){
		String [] array  = state.split(";");
		String sizeGrid = array[0];
		return sizeGrid.split(",",1);
	}

	public String[] extractMaxNoOfCarry(){
		String [] array = state.split(";");
		String [] MaxCarryNo = new String[1];
		MaxCarryNo[0] = array[1];
		return MaxCarryNo;
	}

	public String[] extractNeoPos(){
		String [] array = state.split(";");
		String posNeo = array[2];
		return posNeo.split(",");
	}

	public String[] extractTelBoothPos(){
		String [] array = state.split(";");
		String telPos = array[3];
		String [] telBoothPos = telPos.split(",");
		return telBoothPos;
	}

	public String[] extractAgentsPos(){
		String [] array = state.split(";");
		String posAgents = array[4];
		String [] agentsPos = posAgents.split(",");
		return agentsPos;
	}

	public String[] extractPillPos(){
		String [] array = state.split(";");
		String posPills = array[5];
		String [] pillPos = posPills.split(",");
		return pillPos;
	}

	public String[] extractPadPos(){
		String [] array = state.split(";");
		String posPad = array[6];
		String [] padPos = posPad.split(",");
		return padPos;
	}

	public String[] extractHostages(){
		String [] array = state.split(";");
		String posHos = array[7];
		String [] hosPos = posHos.split(",");
		return hosPos;
	}

	public String[] extractCarriedHostagesHP(){
		String [] array = state.split(";");
		String hpHos = array[8];
		String [] carriedHosHP = hpHos.split(",");
		return carriedHosHP;
	}

	public String[] extractMutatedHostagesPos(){
		String [] array = state.split(";");
		String posMutatedHos = array[9];
		String [] mutatedHosPos = posMutatedHos.split(",");
		return mutatedHosPos;
	}
}
