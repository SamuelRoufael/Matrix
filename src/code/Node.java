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
	
	
}
