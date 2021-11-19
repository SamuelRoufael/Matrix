package code;

public abstract class GeneralSearch {

	protected static int numberOfNodes;

	public abstract Node Search(Node node, QueuingFunction strategy);
}
