package code;

public class Node {

	private Node parentNode = null;
	private String state = "";
	private String operator = null;
	private int pathCost = 0;
	private int depth = 0;
	private int heuristicCost = 0;
	private int priority = 0;

	public Node(Node parentNode, String state) {
		this.parentNode = parentNode;
		this.state = state;
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

	public void setPathCost(int pathCost) {
		this.pathCost = pathCost;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setHeuristicCost(int heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
