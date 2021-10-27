package code;

public class Node {
	private static int numberOfNodes;
	private Node parentNode;
	private String state;
	private short cost;
	
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
	
	
}
