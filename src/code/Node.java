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

    public String[] extractGridSize() {
        String[] array = state.split(";", 13);
        String sizeGrid = array[0];
        return sizeGrid.split(",", 2);
    }

    public String extractMaxNoOfCarry() {
        String[] array = state.split(";", 13);
        return array[1];
    }

    public String[] extractNeoPos() {
        String[] array = state.split(";", 13);
        String posNeo = array[2];
        return posNeo.split(",");
    }

    public String[] extractTelBoothPos() {
        String[] array = state.split(";", 13);
        String telPos = array[3];
        return telPos.split(",");
    }

    public String[] extractAgentsPos() {
        String[] array = state.split(";", 13);
        String posAgents = array[4];
        return posAgents.split(",");
    }

    public String[] extractPillPos() {
        String[] array = state.split(";", 13);
        String posPills = array[5];
        String[] pillPos = posPills.split(",");
        return (pillPos[0].isEmpty()) ? new String[0] : pillPos;
    }

    public String[] extractPadPos() {
        String[] array = state.split(";", 13);
        String posPad = array[6];
        return posPad.split(",");
    }

    public String[] extractHostages() {
        String[] array = state.split(";", 13);
        String posHos = array[7];
        String[] hosPos = posHos.split(",");
        return hosPos[0].isEmpty() ? new String[0] : hosPos;
    }

    public String[] extractCarriedHostagesHP() {
        String[] array = state.split(";", 13);
        String hpHos = array[8];
        String[] carriedHosHP = hpHos.split(",");
        return carriedHosHP[0].isEmpty() ? new String[0] : carriedHosHP;
    }

    public String[] extractMutatedHostagesPos() {
        String[] array = state.split(";", 13);
        String posMutatedHos = array[9];
        String[] mutatedHosPos = posMutatedHos.split(",");
        return mutatedHosPos[0].isEmpty() ? new String[0] : mutatedHosPos;
    }

    public int extractSavedCount() {
        String[] array = state.split(";", 13);
        return Integer.parseInt(array[10]);
    }

    public int extractDeathsCount() {
        String[] array = state.split(";", 13);
        return Integer.parseInt(array[11]);
    }

    public int extractKilledCount() {
        String[] array = state.split(";", 13);
        return Integer.parseInt(array[12]) - this.extractDeathsCount();
    }

    public int calculateDMG() {
        String[] hostages = this.extractHostages();
        String[] carriedHostages = this.extractCarriedHostagesHP();
        int totalDMG = 0;

        for (int i = 0; i < hostages.length - 2; i += 3) {
            totalDMG += Integer.parseInt(hostages[i + 2]);
        }

        for (String carriedHostage : carriedHostages) {
            totalDMG += Integer.parseInt(carriedHostage);
        }
        return totalDMG;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public int getStepsCost() {
        return stepsCost;
    }

    public void setStepsCost(int stepsCost) {
        this.stepsCost = stepsCost;
    }

    public int getHeuristicCost() {
        return heuristicCost;
    }

    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

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

    private int OptimalCompareTo(Node node) {
        if (this.extractDeathsCount() < node.extractDeathsCount())
            return -1;
        else if (this.extractDeathsCount() > node.extractDeathsCount())
            return 1;
        else return Integer.compare(this.getStepsCost() + this.getHeuristicCost(), node.getStepsCost() + node.getHeuristicCost());
    }

    private int GreedyCompareTo(Node node) {
        if (this.extractDeathsCount() < node.extractDeathsCount())
            return -1;
        else if (this.extractDeathsCount() > node.extractDeathsCount())
            return 1;
        return Integer.compare(this.getHeuristicCost(), node.getHeuristicCost());
    }

}