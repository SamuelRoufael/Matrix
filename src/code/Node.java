package code;

public class Node {

	private Node parentNode;
	private String state;
	private String operator;
	private int pathCost;
	private int depth;
	private int heuristicCost;
	private int priority;

	public Node(Node parentNode, String state, int pathCost) {
		this.parentNode = parentNode;
		this.state = state;
		this.pathCost = pathCost;
	}

	public String getState() {
		return state;
	}

	public String getOperator() {
		return operator;
	}

	public int getPathCost() {
		return pathCost;
	}

	public int getDepth() {
		return depth;
	}

	public int getHeuristicCost() {
		return heuristicCost;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public int getPriority() {
		return priority;
	}

	public String[] extractGridSize(){
		String [] array  = state.split(";",10);
		String sizeGrid = array[0];
		return sizeGrid.split(",",1);
	}

	public String extractMaxNoOfCarry(){
		String [] array = state.split(";", 10);
		return array[1];
	}

	public String[] extractNeoPos(){
		String [] array = state.split(";", 10);
		String posNeo = array[2];
		return posNeo.split(",");
	}

	public String[] extractTelBoothPos(){
		String [] array = state.split(";", 10);
		String telPos = array[3];
		return telPos.split(",");
	}

	public String[] extractAgentsPos(){
		String [] array = state.split(";", 10);
		String posAgents = array[4];
		return posAgents.split(",");
	}

	public String[] extractPillPos(){
		String [] array = state.split(";", 10);
		String posPills = array[5];
		String [] pillPos = posPills.split(",");
		return (pillPos[0].isEmpty()) ? null : pillPos ;
	}

	public String[] extractPadPos(){
		String [] array = state.split(";", 10);
		String posPad = array[6];
		return posPad.split(",");
	}

	public String[] extractHostages(){
		String [] array = state.split(";", 10);
		String posHos = array[7];
		String [] hosPos = posHos.split(",");
		return hosPos[0].isEmpty() ? null : hosPos;
	}

	public String[] extractCarriedHostagesHP(){
		String [] array = state.split(";", 10);
		String hpHos = array[8];
		String [] carriedHosHP = hpHos.split(",");
		return carriedHosHP[0].isEmpty() ? null : carriedHosHP;
	}

	public String[] extractMutatedHostagesPos(){
		String [] array = state.split(";", 10);
		String posMutatedHos = array[9];
		String [] mutatedHosPos = posMutatedHos.split(",");
		return mutatedHosPos[0].isEmpty() ? null : mutatedHosPos;
	}
}
