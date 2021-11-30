package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public abstract class GeneralSearch {

	protected static int numberOfNodes;
	protected static final HashMap<String, Boolean> repeatedStates = new HashMap<>();

	/**
	 * @return a function that returns an instance of the class Node from the grid, where this node
	 * will be the root of the tree.
	 */
	public abstract Node createInitialNode();

	public abstract String getGrid();

	/**
	 * @return extracts the initials (head) of the tree.
	 */
	public abstract Node getInitialNode();

	/**
	 * @param node : A node object which we want to expand.
	 * @return all possible operators that can be executed from a certain node's state by eliminated all operators that
	 * can result in producing an illegal state.
	 */
	public abstract ArrayList<String> AvailableOperators(Node node);

	/**
	 * @param node : A node instance which the operation will be applied to.
	 * @param operator : the operation to be applied on the given node.
	 * @return a new node after applying the operator on the given node, and it returns null if the node's state is
	 * repeated or does not change the current state.
	 */
	public abstract Node Expand(Node node, String operator);

	/**
	 * @param node : a node object that can be a goal candidate.
	 * @return true if the given node is a goal, false otherwise.
	 */
	public abstract boolean GoalTest(Node node);

	/**
	 * @param node : a node object on a given path.
	 * @return true if Neo's Damage reaches 100, false otherwise.
	 */
	public abstract boolean GameOver(Node node);

	/**
	 * Calculates an admissible heuristic cost (estimated cost to reach the goal from the given node).
	 * Initially, we Assume that no Agents or mutated hostages exist on the grid, consequently kills won't be a factor
	 * in the heuristic cost. Afterwards, we calculate the heuristic cost based on the distance (taking into
	 * consideration 4 directions and pads) between neo and the Hostage with the highest damage that neo can save.
	 *
	 * @param node : a node which we want to calculate its heuristic cost.
	 * @return an integer value representing the heuristic cost.
	 */
	public abstract int CalculateHeuristicOne(Node node);

	/**
	 * Calculates an admissible heuristic cost (estimated cost to reach the goal from the given node).
	 * Initially, we Assume that no Agents or mutated hostages exist on the grid, consequently kills won't be a factor
	 * in the heuristic cost. Afterwards, we calculate the heuristic cost based on the shortest distance (taking into
	 * consideration 4 directions and pads) between neo and the hostage that neo can save.
	 *
	 * @param node : a node which we want to calculate its heuristic cost.
	 * @return an integer value representing the heuristic cost.
	 */
	public abstract int CalculateHeuristicTwo(Node node);

	public static Node Search(GeneralSearch problem, QueuingFunction queuingFunction) {
		if (queuingFunction == QueuingFunction.ENQUEUE_END || queuingFunction == QueuingFunction.ENQUEUE_FRONT || queuingFunction == QueuingFunction.ENQUEUE_END_IDS)
			return QueueSearch(problem, queuingFunction);
		else
			return PriorityQueueSearch(problem, queuingFunction);
	}

	private static Node QueueSearch(GeneralSearch problem, QueuingFunction queuingFunction) {
		LinkedList<Node> nodesQueue = new LinkedList<>();

		if (queuingFunction == QueuingFunction.ENQUEUE_END_IDS) {

			int currentMaxDepth = 0;

			while (true) {
				nodesQueue.addFirst(problem.getInitialNode());

				while (!nodesQueue.isEmpty()) {
					Node currNode = nodesQueue.poll();

					if (problem.GoalTest(currNode)) {
						repeatedStates.clear();
						return currNode;
					} else if (problem.GameOver(currNode) || currNode.getDepth() >= currentMaxDepth)
						continue;

					ArrayList<String> availableOperators = problem.AvailableOperators(currNode);

					for (String operator : availableOperators) {
						Node node = problem.Expand(currNode, operator);

						if (node == null)
							continue;

						nodesQueue.addFirst(node);
					}
				}
				currentMaxDepth += 10;
				repeatedStates.clear();
			}
		} else {
			nodesQueue.add(problem.getInitialNode());
			while (!nodesQueue.isEmpty()) {
				Node currNode = nodesQueue.poll();

				if (problem.GameOver(currNode))
					continue;
				else if (problem.GoalTest(currNode)) {
					repeatedStates.clear();
					return currNode;
				}
				ArrayList<String> availableOperations = problem.AvailableOperators(currNode);
				for (String operation : availableOperations) {
					Node node = problem.Expand(currNode, operation);

					if (node == null)
						continue;

					if (queuingFunction == QueuingFunction.ENQUEUE_END)
						nodesQueue.addLast(node);
					else
						nodesQueue.addFirst(node);
				}
			}
			return null;
		}
	}

	private static Node PriorityQueueSearch(GeneralSearch problem, QueuingFunction queuingFunction) {
		PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
		priorityQueue.add(problem.getInitialNode());

		while (!priorityQueue.isEmpty()) {
			Node currNode = priorityQueue.poll();

			if (problem.GameOver(currNode))
				continue;
			else if (problem.GoalTest(currNode)) {
				repeatedStates.clear();
				return currNode;
			}

			ArrayList<String> availableOperations = problem.AvailableOperators(currNode);
			for (String operation : availableOperations) {
				Node node = problem.Expand(currNode, operation);

				if (node == null)
					continue;

				switch (queuingFunction) {
					case ENQUEUE_ORDERED_A1:
					case ENQUEUE_ORDERED_G1:
						node.setHeuristicCost(problem.CalculateHeuristicOne(node));break;
					case ENQUEUE_ORDERED_A2:
					case ENQUEUE_ORDERED_G2:
						node.setHeuristicCost(problem.CalculateHeuristicTwo(node));break;
					default:
						node.setHeuristicCost(0);
				}

				priorityQueue.add(node);
			}
		}
		return null;
	}

	public static String GenerateOutput(Node node) {

		if (node == null) {
			return "No Solution";
		}

		LinkedList<String> path = new LinkedList<>();
		String[] outputArray = new String[4];
		Node currentNode = node;

		while (currentNode.getParentNode() != null) {
			path.addFirst(currentNode.getOperator());
			currentNode = currentNode.getParentNode();
		}
		outputArray[0] = String.join(",", path);
		outputArray[1] = node.extractDeathsCount() + "";
		outputArray[2] = (node.extractKilledCount() + node.extractDeathsCount()) + "";
		outputArray[3] = numberOfNodes + "";

		return String.join(";", outputArray);
	}
}
