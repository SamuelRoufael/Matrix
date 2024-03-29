package code;

public class Node implements Comparable<Node> {

    private Node parentNode = null;
    private String state = "";
    private String operator = "";
    private int depth = 0;
    private int stepsCost = 0;
    private int heuristicCost = 0;

    public Node(Node parentNode, String state) {
        this.parentNode = parentNode;
        this.state = state;
    }

    /**
     * @return Array of Strings that represent the X and Y dimensions of the grid.
     */
    public String[] extractGridSize() {
        String[] array = state.split(";", 13);
        String sizeGrid = array[0];
        return sizeGrid.split(",", 2);
    }

    /**
     * @return returns a string that represent the maximum carry.
     */
    public String extractMaxNoOfCarry() {
        String[] array = state.split(";", 13);
        return array[1];
    }

    /**
     * @return array of Strings that represent the X,Y,and neo's damage at the node's state.
     */
    public String[] extractNeoPos() {
        String[] array = state.split(";", 13);
        String posNeo = array[2];
        return posNeo.split(",");
    }

    /**
     * @return Array of Strings that represent the X and Y coordinates of the telephone booth.
     */
    public String[] extractTelBoothPos() {
        String[] array = state.split(";", 13);
        String telPos = array[3];
        return telPos.split(",");
    }

    /**
     * @return Array of Strings that represent the position of agents on the grid in the following form :
     * [X1,Y1,...,Xn,Yn], where n the total number of agents at the node's state.
     */
    public String[] extractAgentsPos() {
        String[] array = state.split(";", 13);
        String posAgents = array[4];
        return posAgents.split(",");
    }

    /**
     * @return Array of Strings that represent the position of pills on the grid in the following form :
     * [X1,Y1,...,Xn,Yn], where n the total number of pills at the node's state.
     */
    public String[] extractPillPos() {
        String[] array = state.split(";", 13);
        String posPills = array[5];
        String[] pillPos = posPills.split(",");
        return (pillPos[0].isEmpty()) ? new String[0] : pillPos;
    }

    /**
     * @return Array of Strings that represent the position of all pad pairs on the grid in the following form :
     * [X11,Y11,X12,Y12,...,Xn1,Yn1,Xn2,Yn2], where n the total number of pad pairs in the grid.
     */
    public String[] extractPadPos() {
        String[] array = state.split(";", 13);
        String posPad = array[6];
        return posPad.split(",");
    }

    /**
     * @return Array of Strings that represent hostages' position and damage on the grid (not carried by neo)
     * in the following form : [X1,Y1,D1,...,Xn,Yn,Dn], where n the total number of hostages not carried by neo at the node's state.
     */
    public String[] extractHostages() {
        String[] array = state.split(";", 13);
        String posHos = array[7];
        String[] hosPos = posHos.split(",");
        return hosPos[0].isEmpty() ? new String[0] : hosPos;
    }

    /**
     * @return Array of Strings that represent carried hostages' damage, presented in the following form :
     * [D1,...,Dn], where n the total number of hostages carried by neo at the node's state.
     */
    public String[] extractCarriedHostagesHP() {
        String[] array = state.split(";", 13);
        String hpHos = array[8];
        String[] carriedHosHP = hpHos.split(",");
        return carriedHosHP[0].isEmpty() ? new String[0] : carriedHosHP;
    }

    /**
     * @return Array of Strings that represent the position of mutated hostages on the grid in the following form :
     * [X1,Y1,...,Xn,Yn], where n the total number of mutated hostages at the node's state.
     */
    public String[] extractMutatedHostagesPos() {
        String[] array = state.split(";", 13);
        String posMutatedHos = array[9];
        String[] mutatedHosPos = posMutatedHos.split(",");
        return mutatedHosPos[0].isEmpty() ? new String[0] : mutatedHosPos;
    }

    /**
     * @return the total number deaths at the node's state.
     */
    public int extractDeathsCount() {
        String[] array = state.split(";", 13);
        return Integer.parseInt(array[11]);
    }

    /**
     * @return the total number of kills at the node's state.
     */
    public int extractKilledCount() {
        String[] array = state.split(";", 13);
        return Integer.parseInt(array[12]) - this.extractDeathsCount();
    }

    /**
     * @return the whole State string of a Node.
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the performed operation on the node.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the current depth of the node in the tree.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return the Node which the operation was applied to generate the current Node.
     */
    public Node getParentNode() {
        return parentNode;
    }

    /**
     * @return the cost of the path from the root to the current node.
     */
    public int getStepsCost() {
        return stepsCost;
    }

    /**
     * sets the path cost to the node.
     * @param stepsCost value of the path cost.
     */
    public void setStepsCost(int stepsCost) {
        this.stepsCost = stepsCost;
    }

    /**
     * @return heuristic cost of the node.
     */
    public int getHeuristicCost() {
        return heuristicCost;
    }

    /**
     * assign a heuristic cost to the node.
     * @param heuristicCost value of the heuristic cost.
     */
    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    /**
     * Compares the current node to another node, to check which node has higher priority (will take effect in enqueuing
     * nodes in the priority queue).
     * @param node : which we want to compare to this node instance.
     * @return -1 if this node instance has higher priority than the other node, 0 if they have equal priorities, or 1
     * if the other node has higher priority.
     */
    @Override
    public int compareTo(Node node) {
        String strategy = Matrix.getGlobalQueuingFunction();
        switch (strategy) {
            case "GR1" :
            case "GR2" :
                return GreedyCompareTo(node);
            default: return OptimalCompareTo(node);
        }
    }

    /**
     * A helper function to the compareTo method, which compares the two nodes optimally and considers the heuristic cost
     * if required (in A* algorithms).
     *
     * @param node : which we want to compare to this node instance.
     * @return -1 if this node instance has higher priority than the other node, 0 if they have equal priorities, or 1
     * if the other node has higher priority.
     */
    private int OptimalCompareTo(Node node) {
        if (this.extractDeathsCount() < node.extractDeathsCount())
            return -1;
        else if (this.extractDeathsCount() > node.extractDeathsCount())
            return 1;
        else return Integer.compare(this.getStepsCost() + this.getHeuristicCost(), node.getStepsCost() + node.getHeuristicCost());
    }

    /**
     * A helper function to the compareTo method, which compares the two nodes considering the heuristic cost only.
     *
     * @param node : which we want to compare to this node instance.
     * @return -1 if this node instance has higher priority than the other node, 0 if they have equal priorities, or 1
     * if the other node has higher priority.
     */
    private int GreedyCompareTo(Node node) {
        if (this.extractDeathsCount() < node.extractDeathsCount())
            return -1;
        else if (this.extractDeathsCount() > node.extractDeathsCount())
            return 1;
        return Integer.compare(this.getHeuristicCost(), node.getHeuristicCost());
    }

}