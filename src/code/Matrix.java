package code;

import java.util.*;

public class Matrix extends GeneralSearch {

    private static final String[] operators = {"carry", "drop", "takePill", "up", "down", "right", "left", "fly", "kill"};
    private static String globalQueuingFunction = "";
    private final String grid;
    private final Node initialNode;

    public Matrix(String grid) {
        this.grid = grid;
        this.initialNode = this.createInitialNode();
    }

    /**
     * Generates a grid with neo, telephone booth locations randomized and number of agents, hostages, pads, and pills
     * randomized as well as their locations
     * @return A grid
     */
    public static String genGrid() {
        Random rand = new Random();

        String grid = ""; // output

        // Initialize Dimensions of the Grid
        int M = rand.nextInt(11) + 5;
        int N = rand.nextInt(11) + 5;
        grid += M + "," + N + ";";

        // Variables used to avoid clashes and overlapping
        boolean[][] gridArray = new boolean[M][N]; // Used to keep track of the used cell to avoid spawning different object in the same cell
        int numberOfEmptyCells = (M * N); // a variable to keep track of the number of the available cells

        // Choose a random position to spawn Neo given the grid boundaries and Neo's variables.
        int neoX = rand.nextInt(M);
        int neoY = rand.nextInt(N);
        int maxCarry = rand.nextInt(4) + 1; // Max number Neo can Carry
        grid += maxCarry + ";" + neoX + "," + neoY + ";";
        gridArray[neoX][neoY] = true;
        numberOfEmptyCells -= 1;

        // Choose a random position to spawn the Telephone booth given the grid boundaries.
        int telephoneX, telephoneY;

        while (true) {
            telephoneX = rand.nextInt(M);
            telephoneY = rand.nextInt(N);
            if (!gridArray[telephoneX][telephoneY]) {
                gridArray[telephoneX][telephoneY] = true;
                numberOfEmptyCells -= 1;
                grid += telephoneX + "," + telephoneY + ";";
                break;
            }
        }

        // Generate a Random number of Hostages
        int numberOfHostages = rand.nextInt(6) + 4;
        numberOfEmptyCells -= numberOfHostages;

        // Generate a Random number of Pills
        int numberOfPills = rand.nextInt(numberOfHostages) + 1;
        numberOfEmptyCells -= numberOfPills;

        // Generate a Random number of Agents
        int numberOfAgents = (numberOfEmptyCells <= numberOfHostages * 2) ? (rand.nextInt(numberOfEmptyCells) + 1) : (rand.nextInt(numberOfHostages) + numberOfHostages);
        numberOfEmptyCells -= numberOfAgents;

        // Generate a Random number of Pads
        int numberOfPadPairs = (numberOfEmptyCells >= 2) ? (Math.max(Math.min(rand.nextInt(numberOfEmptyCells) / 2, 6), 1)) : 0;

        int agentX, agentY;
        for (int i = 0; i < numberOfAgents; i++) {
            while (true) {
                agentX = rand.nextInt(M);
                agentY = rand.nextInt(N);
                if (!gridArray[agentX][agentY]) {
                    gridArray[agentX][agentY] = true;
                    grid += agentX + "," + agentY;
                    break;
                }
            }
            grid += (numberOfAgents == i + 1) ? ";" : ",";
        }

        int pillX, pillY;
        for (int i = 0; i < numberOfPills; i++) {
            while (true) {
                pillX = rand.nextInt(M);
                pillY = rand.nextInt(N);
                if (!gridArray[pillX][pillY]) {
                    gridArray[pillX][pillY] = true;
                    grid += pillX + "," + pillY;
                    break;
                }
            }
            grid += (numberOfPills == i + 1) ? ";" : ",";
        }

        int padX1, padY1, padX2, padY2;
        boolean pairMatched;
        for (int i = 0; i < numberOfPadPairs; i++) {
            pairMatched = false;
            while (!pairMatched) {
                padX1 = rand.nextInt(M);
                padY1 = rand.nextInt(N);
                if (!gridArray[padX1][padY1]) {
                    gridArray[padX1][padY1] = true;
                    while (true) {
                        padX2 = rand.nextInt(M);
                        padY2 = rand.nextInt(N);
                        if (!gridArray[padX2][padY2]) {
                            gridArray[padX2][padY2] = true;
                            pairMatched = true;
                            grid += padX1 + "," + padY1 + "," + padX2 + "," + padY2;
                            grid += "," + padX2 + "," + padY2 + "," + padX1 + "," + padY1;
                            break;
                        }
                    }
                }
            }
            grid += (numberOfPadPairs == i + 1) ? ";" : ",";
        }

        int hostageX, hostageY, hostageDamage;
        for (int i = 0; i < numberOfHostages; i++) {
            while (true) {
                hostageX = rand.nextInt(M);
                hostageY = rand.nextInt(N);
                if (!gridArray[hostageX][hostageY]) {
                    hostageDamage = rand.nextInt(99) + 1;
                    gridArray[hostageX][hostageY] = true;
                    grid += hostageX + "," + hostageY + "," + hostageDamage;
                    break;
                }
            }
            grid += (numberOfHostages == i + 1) ? ";" : ",";
        }
        return grid;
    }

    /**
     * Takes a grid and try to reach the goal node if there is any
     * @param grid : A grid generated by the genGrid
     * @param strategy : The search strategy which can be BFS, DFS, UC, ID, A* or GR
     * @param visualize : A boolean to call visualize or not
     * @return
     */
    public static String solve(String grid, String strategy, boolean visualize) {
        Matrix matrix = new Matrix(grid);
        globalQueuingFunction = strategy;
        numberOfNodes = 0;
        repeatedStates.clear();
        Node goalNode;

        switch (strategy) {
            case "BF":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_FRONT);
                break;
            case "ID":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_END_IDS);
                break;
            case "UC":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_ORDERED_UC);
                break;
            case "GR1":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_ORDERED_G1);
                break;
            case "GR2":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_ORDERED_G2);
                break;
            case "AS1":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_ORDERED_A1);
                break;
            case "AS2":
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_ORDERED_A2);
                break;
            default:
                goalNode = Search(matrix, QueuingFunction.ENQUEUE_END);
        }
        if(visualize){
            visualize(matrix,GenerateOutput(goalNode));
        }
        return GenerateOutput(goalNode);
    }

    @Override
    public Node getInitialNode() {
        return initialNode;
    }

    /**
     * @return The queuing function to know whether to call the optimal comparsion or heuristic comparsion
     */
    public static String getGlobalQueuingFunction() {
        return globalQueuingFunction;
    }

    @Override
    public String getGrid() {
        return grid;
    }

    @Override
    public Node createInitialNode() {
        String[] gridArray = this.grid.split(";", 10);
        gridArray[2] = gridArray[2] + ",0";
        String state = String.join(";", gridArray) + ";;;0;0;0";
        return new Node(null, state);
    }

    @Override
    public ArrayList<String> AvailableOperators(Node node) {
        ArrayList<String> availableOperators = new ArrayList<>(Arrays.asList(operators));
        String[] hostages = node.extractHostages();
        String[] neo = node.extractNeoPos();
        String[] agents = node.extractAgentsPos();
        String[] mutatedAgents = node.extractMutatedHostagesPos();
        String[] grid = node.extractGridSize();
        String maxCarry = node.extractMaxNoOfCarry();
        String[] carriedHostages = node.extractCarriedHostagesHP();
        String[] telephoneBooth = node.extractTelBoothPos();

        for (String operator : operators) {
            if (operator.equals("carry")) {
                if (carriedHostages.length == Integer.parseInt(maxCarry))
                    availableOperators.remove("carry");
            }

            if (operator.equals("drop")) {
                if (!(neo[0].equals(telephoneBooth[0]) && neo[1].equals(telephoneBooth[1])))
                    availableOperators.remove("drop");
            }

            if (operator.equals("up")) {
                if (node.getOperator().equals("down") || neo[0].equals("0")) {
                    availableOperators.remove("up");
                }
            }

            if (operator.equals("down")) {
                if (node.getOperator().equals("up") || Integer.parseInt(neo[0]) == Integer.parseInt(grid[0]) - 1)
                    availableOperators.remove("down");
            }

            if (operator.equals("right")) {
                if (node.getOperator().equals("left") || Integer.parseInt(neo[1]) == Integer.parseInt(grid[1]) - 1)
                    availableOperators.remove("right");
            }

            if (operator.equals("left")) {
                if (node.getOperator().equals("right") || neo[1].equals("0"))
                    availableOperators.remove("left");
            }

            if (operator.equals("fly") && node.getOperator().equals("fly")) {
                availableOperators.remove("fly");
            }
        }

        availableOperators.removeAll(AgentIllegalMoves(agents, neo));
        availableOperators.removeAll(HostageIllegalMoves(hostages, neo));
        availableOperators.removeAll(MutatedHostagesIllegalMoves(mutatedAgents, neo));
        return availableOperators;
    }

    /**
     * Finds all operation that if done by neo will end with a dead hostage.
     * @param hostages : Array of available hostages in the state
     * @param neo : Neo's current position
     * @return A list of the operators that neo is not allowed to do
     */
    private static ArrayList<String> HostageIllegalMoves(String[] hostages, String[] neo) {

        ArrayList<String> toBeRemovedMoves = new ArrayList<>();
        for (int i = 0; i < hostages.length - 2; i += 3) {
            if (Integer.parseInt(hostages[i + 2]) >= 98) {
                // Check if we need to remove the up move.
                if (hostages[i + 1].equals(neo[1]) && Integer.parseInt(hostages[i]) == Integer.parseInt(neo[0]) - 1) {
                    toBeRemovedMoves.add("up");
                }
                // Check if we need to remove the down move.
                if (hostages[i + 1].equals(neo[1]) && Integer.parseInt(hostages[i]) == Integer.parseInt(neo[0]) + 1) {
                    toBeRemovedMoves.add("down");
                }
                // Check if we need to remove the left move.
                if (hostages[i].equals(neo[0]) && Integer.parseInt(hostages[i + 1]) == Integer.parseInt(neo[1]) - 1) {
                    toBeRemovedMoves.add("left");
                }
                // Check if we need to remove the right move.
                if (hostages[i].equals(neo[0]) && Integer.parseInt(hostages[i + 1]) == Integer.parseInt(neo[1]) + 1) {
                    toBeRemovedMoves.add("right");
                }
                // if neo is in a cell with a Hostage who will die on the next round and performs a kill operation.
                if (neo[0].equals(hostages[i]) && neo[1].equals(hostages[i + 1]) && Integer.parseInt(hostages[i + 2]) >= 98) {
                    toBeRemovedMoves.add("kill");
                }
            }
        }
        return toBeRemovedMoves;
    }

    /**
     * Finds all operation that if done by neo will clash with an agent.
     * @param agents : Array of available agents in the state
     * @param neo : Neo's current position
     * @return A list of the operators that neo is allowed to do
     */
    private static ArrayList<String> AgentIllegalMoves(String[] agents, String[] neo) {
        ArrayList<String> toBeRemovedMoves = new ArrayList<>();
        for (int i = 0; i < agents.length - 1; i += 2) {
            // Check if we need to remove the up move.
            if (agents[i + 1].equals(neo[1]) && Integer.parseInt(agents[i]) == Integer.parseInt(neo[0]) - 1) {
                toBeRemovedMoves.add("up");
            }
            // Check if we need to remove the down move.
            if (agents[i + 1].equals(neo[1]) && Integer.parseInt(agents[i]) == Integer.parseInt(neo[0]) + 1) {
                toBeRemovedMoves.add("down");
            }
            // Check if we need to remove the left move.
            if (agents[i].equals(neo[0]) && Integer.parseInt(agents[i + 1]) == Integer.parseInt(neo[1]) - 1) {
                toBeRemovedMoves.add("left");
            }
            // Check if we need to remove the right move.
            if (agents[i].equals(neo[0]) && Integer.parseInt(agents[i + 1]) == Integer.parseInt(neo[1]) + 1) {
                toBeRemovedMoves.add("right");
            }
        }
        return toBeRemovedMoves;
    }

    /**
     * Finds all operation that if done by neo will end with a mutated hostage.
     * @param mutatedHostages : Array of available hostages in the state
     * @param neo : Neo's current position
     * @return A list of operators that neo is not allowed to do.
     */
    private static ArrayList<String> MutatedHostagesIllegalMoves(String[] mutatedHostages, String[] neo) {
        ArrayList<String> toBeRemovedMoves = new ArrayList<>();
        for (int i = 0; i < mutatedHostages.length - 1; i += 2) {
            // Check if we need to remove the up move.
            if (mutatedHostages[i + 1].equals(neo[1]) && Integer.parseInt(mutatedHostages[i]) == Integer.parseInt(neo[0]) - 1) {
                toBeRemovedMoves.add("up");
            }
            // Check if we need to remove the down move.
            if (mutatedHostages[i + 1].equals(neo[1]) && Integer.parseInt(mutatedHostages[i]) == Integer.parseInt(neo[0]) + 1) {
                toBeRemovedMoves.add("down");
            }
            // Check if we need to remove the left move.
            if (mutatedHostages[i].equals(neo[0]) && Integer.parseInt(mutatedHostages[i + 1]) == Integer.parseInt(neo[1]) - 1) {
                toBeRemovedMoves.add("left");
            }
            // Check if we need to remove the right move.
            if (mutatedHostages[i].equals(neo[0]) && Integer.parseInt(mutatedHostages[i + 1]) == Integer.parseInt(neo[1]) + 1) {
                toBeRemovedMoves.add("right");
            }
        }
        return toBeRemovedMoves;
    }

    @Override
    public Node Expand(Node node, String operator) {
        String newState;
        Node newNode;
        int stepCost = node.getStepsCost();

        switch (operator) {
            case "up": {
                newState = Move(node, operator);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("up");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost + 50);
            }
            break;
            case "down": {
                newState = Move(node, operator);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("down");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost + 50);
            }
            break;
            case "left": {
                newState = Move(node, operator);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("left");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost + 50);
            }
            break;
            case "right": {
                newState = Move(node, operator);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("right");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost + 50);
            }
            break;

            case "carry": {
                newState = Carry(node);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("carry");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost);
            }
            break;

            case "drop": {
                newState = Drop(node);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("drop");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost);
            }
            break;
            case "kill": {
                newState = Kill(node);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("kill");
                newNode.setDepth(node.getDepth() + 1);

                if (newNode.extractMutatedHostagesPos().length != node.extractMutatedHostagesPos().length &&
                        newNode.extractAgentsPos().length != node.extractAgentsPos().length)
                    newNode.setStepsCost(stepCost + 100000);
                else if (newNode.extractMutatedHostagesPos().length != node.extractMutatedHostagesPos().length)
                    newNode.setStepsCost(stepCost);
                else
                    newNode.setStepsCost(stepCost + 1000000);
            }
            break;
            case "fly": {
                newState = Fly(node);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("fly");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost + 50);
            }
            break;
            //Take Pill
            default: {
                newState = TakePill(node);
                if (newState == null || repeatedStates.get(newState) != null)
                    return null;
                repeatedStates.put(newState, true);
                newNode = new Node(node, newState);
                newNode.setOperator("takePill");
                newNode.setDepth(node.getDepth() + 1);
                newNode.setStepsCost(stepCost + 20);
            }
            break;
        }

        numberOfNodes++;
        if (!operator.equals("takePill")) {
            newNode.setState(UpdateDamage(newNode));
        }

        return newNode;
    }

    /**
     * Takes a node and the direction and applies the direction upon the node
     * @param node : A node object that we want to move up, down, right, left
     * @param direction : Specify which direction we want to move
     * @return A new state with Neo's new position
     */
    private static String Move(Node node, String direction) {
        String state = node.getState();
        String[] arrayState = state.split(";", 4);
        String[] neo = node.extractNeoPos();

        switch (direction) {
            case "up":
                neo[0] = (Integer.parseInt(neo[0]) - 1) + "";
                break;
            case "down":
                neo[0] = (Integer.parseInt(neo[0]) + 1) + "";
                break;
            case "right":
                neo[1] = (Integer.parseInt(neo[1]) + 1) + "";
                break;
            default:
                neo[1] = (Integer.parseInt(neo[1]) - 1) + "";
                break;
        }
        arrayState[2] = String.join(",", neo);
        String newState = String.join(";", arrayState);
        return newState.equals(state) ? null : newState;
    }

    /**
     * Takes a node and applies the carry operation upon and returns a updated state
     * @param node : A node object that we want to apply carry upon
     * @return A new state with the carried hostages' damage
     */
    private static String Carry(Node node) {
        String state = node.getState();
        String[] stateArray = state.split(";", 13);
        ArrayList<String> hostages = new ArrayList<>(Arrays.asList(node.extractHostages()));
        ArrayList<String> carriedHostages = new ArrayList<>(Arrays.asList(node.extractCarriedHostagesHP()));
        String[] neo = node.extractNeoPos();

        for (int i = 0; i < hostages.size() - 2; i += 3) {
            if (hostages.get(i).equals(neo[0]) && hostages.get(i + 1).equals(neo[1])) {
                hostages.remove(i);
                hostages.remove(i);
                carriedHostages.add(hostages.remove(i));
                break;
            }
        }

        stateArray[7] = String.join(",", hostages);
        stateArray[8] = String.join(",", carriedHostages);
        String newState = String.join(";", stateArray);

        return (newState.equals(state) ? null : newState);

    }

    /**
     * Takes a node and applies the drop operation upon and returns a updated state
     * @param node : A node object that we want to apply drop upon
     * @return A new state without the carried hostages' damage
     */
    private static String Drop(Node node) {
        String state = node.getState();
        String[] stateArray = state.split(";", 13);
        String[] carriedHostages = node.extractCarriedHostagesHP();
        short saved = 0;
        for (String hostageDamage : carriedHostages) {
            if (Integer.parseInt(hostageDamage) < 100)
                saved++;
        }
        stateArray[8] = "";
        stateArray[10] = Integer.parseInt(stateArray[10]) + saved + "";

        return String.join(";", stateArray).equals(state) ? null : String.join(";", stateArray);
    }

    /**
     * Checks if there are any agents in the cells adjacent to neo
     * A helper function to the AgentIllegalMoves
     * @param neoX : The X-coordinate of neo
     * @param neoY : The Y-coordinate of neo
     * @param agentX : The X-coordinate of agent
     * @param agentY : The Y-coordinate of agent
     * @return A boolean that states whether there is an agent in the adjacent cell of neo, whether in the x-axis or the y-axis
     */
    private static boolean IsAdjacent(String neoX, String neoY, String agentX, String agentY) {
        int neoXInt = Integer.parseInt(neoX);
        int agentXInt = Integer.parseInt(agentX);
        int neoYInt = Integer.parseInt(neoY);
        int agentYInt = Integer.parseInt(agentY);

        if (neoXInt == agentXInt && (neoYInt == agentYInt + 1 || neoYInt == agentYInt - 1))
            return true;
        else return neoYInt == agentYInt && (neoXInt == agentXInt + 1 || neoXInt == agentXInt - 1);
    }

    /**
     * Takes a node and applies the kill operation upon it and returns a updated state
     * @param node : A node object that we want to apply kill upon
     * @return A new state with all agents/mutated hostages in the adjacent cells around neo are killed
     */
    private static String Kill(Node node) {
        String state = node.getState();
        String[] stateArray = state.split(";", 13);
        ArrayList<String> agents = new ArrayList<>(Arrays.asList(node.extractAgentsPos()));
        ArrayList<String> mutatedHostages = new ArrayList<>(Arrays.asList(node.extractMutatedHostagesPos()));
        String[] neo = node.extractNeoPos();
        short countKills = 0;

        for (int i = 0; i < mutatedHostages.size() - 1; i += 2) {
            if (IsAdjacent(neo[0], neo[1], mutatedHostages.get(i), mutatedHostages.get(i + 1))) {
                mutatedHostages.remove(i);
                mutatedHostages.remove(i);
                i -= 2;
                countKills++;
            }
        }

        for (int i = 0; i < agents.size() - 1; i += 2) {
            if (IsAdjacent(neo[0], neo[1], agents.get(i), agents.get(i + 1))) {
                agents.remove(i);
                agents.remove(i);
                i -= 2;
                countKills++;
            }
        }

        // return null of neo did not kill anyone.
        if (countKills == 0) {
            return null;
        }

        neo[2] = Math.min(100, Integer.parseInt(neo[2]) + 20) + "";
        stateArray[2] = String.join(",", neo);
        stateArray[4] = String.join(",", agents);
        stateArray[9] = String.join(",", mutatedHostages);
        stateArray[12] = (Integer.parseInt(stateArray[12]) + countKills) + "";

        return String.join(";", stateArray);
    }

    /**
     * Takes node and increments the hostages' and carried hostages' damage with 2
     * and checks if we have any new mutated agents
     * @param node : A node object that we want to update its state
     * @return A new state with the updated hostages' and carried hostages' damage and with new mutated hostages if any
     */
    private static String UpdateDamage(Node node) {
        String[] stateArray = node.getState().split(";", 13);
        ArrayList<String> hostages = new ArrayList<>(Arrays.asList(node.extractHostages()));
        ArrayList<String> carriedHostages = new ArrayList<>(Arrays.asList(node.extractCarriedHostagesHP()));
        ArrayList<String> mutatedHostages = new ArrayList<>(Arrays.asList(node.extractMutatedHostagesPos()));

        for (int i = 0; i < hostages.size() - 2; i += 3) {
            int damage = Integer.parseInt(hostages.get(i + 2));
            damage = Math.min(100, damage + 2);
            if (damage < 100) {
                hostages.set(i + 2, damage + "");
            } else {
                mutatedHostages.add(hostages.remove(i));
                mutatedHostages.add(hostages.remove(i));
                hostages.remove(i);
                stateArray[11] = (Integer.parseInt(stateArray[11]) + 1) + "";
                i -= 3;
            }
        }
        stateArray[7] = String.join(",", hostages);
        stateArray[9] = String.join(",", mutatedHostages);

        for (int i = 0; i < carriedHostages.size(); i++) {
            int damage = Integer.parseInt(carriedHostages.get(i));
            if (damage < 100 && damage + 2 >= 100) {
                stateArray[11] = (Integer.parseInt(stateArray[11]) + 1) + "";
            }
            damage = Math.min(100, damage + 2);
            carriedHostages.set(i, damage + "");
        }
        stateArray[8] = String.join(",", carriedHostages);

        return String.join(";", stateArray);
    }

    /**
     * Takes a node object to apply the takePill operation upon it and returns a updated state
     * @param node : A node object that we want to apply the takePill operation upon
     * @return A new state with the updated damage of neo, hostages and carried hostages
     */
    private static String TakePill(Node node) {
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] carriedHostages = node.extractCarriedHostagesHP();
        String[] arrayState = state.split(";", 13);
        ArrayList<String> pills = new ArrayList<>(Arrays.asList(node.extractPillPos()));

        for (int i = 0; i < pills.size() - 1; i += 2) {
            String pillX = pills.get(i);
            String pillY = pills.get(i + 1);
            if (pillX.equals(neoPosition[0]) && pillY.equals(neoPosition[1])) {
                for (int j = 0; j < carriedHostages.length; j++) {
                    carriedHostages[j] = Math.max(0, Integer.parseInt(carriedHostages[j]) - 20) + "";
                }
                for (int j = 0; j < hostages.length - 2; j += 3) {
                    hostages[j + 2] = Math.max(0, Integer.parseInt(hostages[j + 2]) - 20) + "";
                }
                neoPosition[2] = Math.max(0, Integer.parseInt(neoPosition[2]) - 20) + "";
                pills.remove(i);
                pills.remove(i);
                break;
            }
        }

        arrayState[2] = String.join(",", neoPosition);
        arrayState[7] = String.join(",", hostages);
        arrayState[8] = String.join(",", carriedHostages);
        arrayState[5] = String.join(",", pills);

        return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
    }

    /**
     * Takes a node object to apply the fly operation upon
     * @param node : A node object that we want to apply the fly operation upon
     * @return A new state with the updated neo's position
     */
    private static String Fly(Node node) {
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] padsPosition = node.extractPadPos();
        String[] arrayState = state.split(";", 13);
        for (int i = 0; i < padsPosition.length - 3; i += 4) {
            String padX1 = padsPosition[i];
            String padY1 = padsPosition[i + 1];
            String padX2 = padsPosition[i + 2];
            String padY2 = padsPosition[i + 3];
            if (neoPosition[0].equals(padX1) && neoPosition[1].equals(padY1)) {
                neoPosition[0] = padX2;
                neoPosition[1] = padY2;
                break;
            } else if (neoPosition[0].equals(padX2) && neoPosition[1].equals(padY2)) {
                neoPosition[0] = padX1;
                neoPosition[1] = padY1;
                break;
            }
        }
        arrayState[2] = String.join(",", neoPosition);
        return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
    }

    @Override
    public boolean GoalTest(Node node) {
        String[] neo = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] mutatedHostages = node.extractMutatedHostagesPos();
        String[] telephoneBooth = node.extractTelBoothPos();
        String[] carriedHostages = node.extractCarriedHostagesHP();

        return neo[0].equals(telephoneBooth[0]) && neo[1].equals(telephoneBooth[1]) && hostages.length == 0
                && mutatedHostages.length == 0 && carriedHostages.length == 0;
    }

    @Override
    public boolean GameOver(Node node) {
        String[] neo = node.extractNeoPos();
        return Integer.parseInt(neo[2]) >= 100;
    }

    @Override
    public int CalculateHeuristicOne(Node node) {
        int cost = 0;
        String[] neo = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] telephoneBooth = node.extractTelBoothPos();
        String[] pads = node.extractPadPos();
        boolean usedPads = false;
        int bestSoFar = Short.MAX_VALUE;

        for (int i = 0; i < hostages.length - 2; i += 3) {
            short xDistanceNeoToHostage = (short) Math.abs(Integer.parseInt(neo[0]) - Integer.parseInt(hostages[i]));
            short yDistanceNeoToHostage = (short) Math.abs(Integer.parseInt(neo[1]) - Integer.parseInt(hostages[i + 1]));
            short totalDistance = (short) (xDistanceNeoToHostage + yDistanceNeoToHostage);

            for (int j = 0 ; j < pads.length - 3 ; j += 4) {
                short xDistanceNeoToPad = (short) Math.abs(Integer.parseInt(neo[0]) - Integer.parseInt(pads[j]));
                short yDistanceNeoToPad = (short) Math.abs(Integer.parseInt(neo[1]) - Integer.parseInt(pads[j + 1]));
                short xDistancePadToHostage = (short) Math.abs(Integer.parseInt(pads[j + 2]) - Integer.parseInt(hostages[i]));
                short yDistancePadToHostage = (short) Math.abs(Integer.parseInt(pads[j + 3]) - Integer.parseInt(hostages[i + 1]));
                totalDistance = (short) Math.min(totalDistance, xDistanceNeoToPad + yDistanceNeoToPad + xDistancePadToHostage + yDistancePadToHostage);
            }

            int damageDifference = 100 - (Integer.parseInt(hostages[i + 2]) + (2 * totalDistance));

            if (totalDistance < xDistanceNeoToHostage + yDistanceNeoToHostage){
                usedPads = true;
                damageDifference-=2;
            }

            if (damageDifference > 0 & damageDifference < bestSoFar) {
                bestSoFar = damageDifference;
                cost = 50 * totalDistance + (50 * (Math.abs(Integer.parseInt(telephoneBooth[0]) - Integer.parseInt(hostages[i])) +
                        Math.abs(Integer.parseInt(telephoneBooth[1]) - Integer.parseInt(hostages[i + 1]))));
                if (usedPads)
                    cost += 50;
            }
        }
        return cost;
    }

    @Override
    public int CalculateHeuristicTwo(Node node) {
        int cost = 0;
        String[] neo = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] telephoneBooth = node.extractTelBoothPos();
        String[] pads = node.extractPadPos();
        boolean usedPads = false;
        short shortestDistance = Short.MAX_VALUE;

        for (int i = 0; i < hostages.length - 2; i += 3) {
            short xDistanceNeoToHostage = (short) Math.abs(Integer.parseInt(neo[0]) - Integer.parseInt(hostages[i]));
            short yDistanceNeoToHostage = (short) Math.abs(Integer.parseInt(neo[1]) - Integer.parseInt(hostages[i + 1]));
            short totalDistance = (short) (xDistanceNeoToHostage + yDistanceNeoToHostage);

            for (int j = 0 ; j < pads.length - 3 ; j += 4) {
                short xDistanceNeoToPad = (short) Math.abs(Integer.parseInt(neo[0]) - Integer.parseInt(pads[j]));
                short yDistanceNeoToPad = (short) Math.abs(Integer.parseInt(neo[1]) - Integer.parseInt(pads[j + 1]));
                short xDistancePadToHostage = (short) Math.abs(Integer.parseInt(pads[j + 2]) - Integer.parseInt(hostages[i]));
                short yDistancePadToHostage = (short) Math.abs(Integer.parseInt(pads[j + 3]) - Integer.parseInt(hostages[i + 1]));
                totalDistance = (short) Math.min(totalDistance, xDistanceNeoToPad + yDistanceNeoToPad + xDistancePadToHostage + yDistancePadToHostage);
            }

            int damageDifference = 100 - (Integer.parseInt(hostages[i + 2]) + (2 * totalDistance));

            if (totalDistance < xDistanceNeoToHostage + yDistanceNeoToHostage){
                usedPads = true;
                damageDifference-=2;
            }

            if (damageDifference > 0 & totalDistance < shortestDistance) {
                shortestDistance = totalDistance;
                cost = 50 * shortestDistance + (50 * (Math.abs(Integer.parseInt(telephoneBooth[0]) - Integer.parseInt(hostages[i])) +
                        Math.abs(Integer.parseInt(telephoneBooth[1]) - Integer.parseInt(hostages[i + 1]))));
                if (usedPads)
                    cost += 50;
            }
        }
        return cost;
    }

    /**
     * Takes an array and prints it
     * @param array : A 1D array
     */
    private static void print1DArray(String[] array){
        for (String s : array) {
            if (array.length > 1)
                System.out.print(s + ", ");
            else
                System.out.println(s);
        }
        System.out.println();
    }

    /**
     * Takes a 2D array and prints it
     * @param array : A 2D array
     */
    public static void print2DArray(String[][] array){
        for (String[] strings : array) {
            for (String string : strings) {
                System.out.print(string + " ");
            }
            System.out.println();
        }
    }

    /**
     * Takes a string state and visualizes the grid
     * @param grid : A string state representing the grid
     */
    private static void printGrid(String grid){

        String[] state = grid.split(";", 17);
        String[] gridSize = state[0].split(",",2);
        String[][] output = new String[Integer.parseInt(gridSize[0])][Integer.parseInt(gridSize[1])];
        for(int i=0;i< output.length;i++)
            for(int j=0;j<output[i].length;j++)
                if(output[i][j] == null)
                    output[i][j]= "________________|";

        String[] neo = state[2].split(",", 4);
        output[Integer.parseInt(neo[0])][Integer.parseInt(neo[1])] = "Neo("+neo[2]+")         |";

        String[] teleBooth = state[3].split(",",2);
        if(teleBooth[0].equals(neo[0]) && teleBooth[1].equals(neo[1]))
            output[Integer.parseInt(teleBooth[0])][Integer.parseInt(teleBooth[1])] = "TB,Neo("+neo[2]+")      |";
        else
            output[Integer.parseInt(teleBooth[0])][Integer.parseInt(teleBooth[1])] = "TB              |";

        String[] agents = state[4].split(",",20);
        for(int i=0;i< agents.length-1;i+=2){
            int agentX = Integer.parseInt(agents[i]);
            int agentY = Integer.parseInt(agents[i+1]);
            output[agentX][agentY] = "A               |";
        }

        String[] pills = state[5].split(",", 10);
        for(int i=0;i< pills.length-1;i+=2){
            int pillX = Integer.parseInt(pills[i]);
            int pillY = Integer.parseInt(pills[i+1]);
            if(pillX == Integer.parseInt(neo[0]) && pillY == Integer.parseInt(neo[1]))
                output[pillX][pillY] = "P,Neo("+neo[2]+")       |";
            else
                output[pillX][pillY] = "P               |";
        }

        String[] pads = state[6].split(",",20);
        for(int i=0; i<pads.length-3; i+=4){
            int padX1 = Integer.parseInt(pads[i]);
            int padY1 = Integer.parseInt(pads[i+1]);
            int padX2 = Integer.parseInt(pads[i+2]);
            int padY2 = Integer.parseInt(pads[i+3]);
            if(padX1 == Integer.parseInt(neo[0]) && padY1 == Integer.parseInt(neo[1]))
                output[padX1][padY1] = "Pad(" + padX2 + "," + padY2 + "),Neo("+neo[2]+")|";
            else
                output[padX1][padY1] = "Pad(" + padX2 + "," + padY2 + ")        |";
            if(padX2 == Integer.parseInt(neo[0]) && padY2 == Integer.parseInt(neo[1]))
                output[padX2][padY2] = "Pad(" + padX1 + "," + padY1 + "),Neo("+neo[2]+")|";
            else
                output[padX2][padY2] = "Pad(" + padX1 + "," + padY1 + ")        |";
        }

        String[] hostages = state[7].split(",",20);
        for(int i=0; i<hostages.length-2; i+=3){
            int hostageX = Integer.parseInt(hostages[i]);
            int hostageY = Integer.parseInt(hostages[i+1]);
            int damage = Integer.parseInt(hostages[i+2]);
            if(hostageX == Integer.parseInt(neo[0]) && hostageY == Integer.parseInt(neo[1]))
                output[hostageX][hostageY] = "H(" + damage + "),Neo("+neo[2]+")   |";
            else
                output[hostageX][hostageY] = "H(" + damage + ")           |";
        }
        if(state.length > 9) {
            String[] mutatedAgents = state[9].split(",", 10);
            for(int i=0;i<mutatedAgents.length-1;i+=2){
                int mutatedX = Integer.parseInt(mutatedAgents[i]);
                int mutatedY = Integer.parseInt(mutatedAgents[i+1]);
                output[mutatedX][mutatedY] = "M               |";
            }
        }
        print2DArray(output);
    }

    /**
     * It visualizes the path taken by the initial node to reach the goal by printing the operator done and
     * the new state
     * @param problem : Initial grid given
     * @param generatedOutput : The path taken to reach the goal node
     */
    public static void visualize(GeneralSearch problem, String generatedOutput){
        String[] carriedHostages = new String[0];
        String[] pathWithCommas  = generatedOutput.split(";", 5);
        String[] path = pathWithCommas[0].split(",");
        String newState = problem.createInitialNode().getState();
        printGrid(newState);
        System.out.println("------------------------------------------------------------------------");
        for(int i=0;i< path.length;i++){
            System.out.println("Operator: " + path[i]);
            newState = problem.Expand(new Node(null, newState), path[i]).getState();
            carriedHostages = new Node(null, newState).extractCarriedHostagesHP();
            System.out.print("Carried Hostages' Damage: ");
            print1DArray(carriedHostages);
            System.out.println();
            printGrid(newState);
            System.out.println("------------------------------------------------------------------------");
        }
    }
}
