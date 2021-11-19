package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

public class Matrix extends GeneralSearch {

    private static final String[] operators = {"Up", "Down", "Right", "Left", "Kill", "TakePill", "Carry", "Drop", "Fly"};
    private static int deaths = 0;
    private static int kills = 0;

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

    public static void solve(String grid, String strategy, boolean visualize) {
        Matrix matrix = new Matrix();
        Node initialNode = createInitialNode(grid);
        Node goalNode = null;

        switch (strategy) {
            case "BF":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_FRONT);
                break;
            case "ID":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_END_IDS);
                break;
            case "UC":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_ORDERED_UC);
                break;
            case "GR1":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_ORDERED_G1);
                break;
            case "GR2":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_ORDERED_G2);
                break;
            case "AS1":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_ORDERED_A1);
                break;
            case "AS2":
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_ORDERED_A2);
                break;
            default:
                goalNode = matrix.Search(initialNode, QueuingFunction.ENQUEUE_END);
        }

    }

    @Override
    public Node Search(Node initialNode, QueuingFunction queuingFunction) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(1000);
        initialNode.setPriority(0);
        priorityQueue.add(initialNode);
        switch (queuingFunction) {
            // BFS
            case ENQUEUE_FRONT: {
                while (!priorityQueue.isEmpty()) {
                    Node currNode = priorityQueue.poll();

                    if (GameOver(currNode))
                        continue;
                    else if (GoalTest(currNode))
                        return currNode;

                    ArrayList<String> availableOperations = AvailableOperators(currNode);
                    for (String operator : availableOperations) {
                        Node node = Expand(currNode, operator);
                        node.setPriority(-numberOfNodes);
                        priorityQueue.add(node);
                    }
                }
            }
            ;
            break;

            case ENQUEUE_END: {

            }
            ;
            break;

            case ENQUEUE_END_IDS: {

            }
            ;
            break;

            case ENQUEUE_ORDERED_UC: {

            }
            ;
            break;

            case ENQUEUE_ORDERED_G1: {

            }
            ;
            break;

            case ENQUEUE_ORDERED_G2: {

            }
            ;
            break;

            case ENQUEUE_ORDERED_A1: {

            }
            ;
            break;

            case ENQUEUE_ORDERED_A2: {

            }
            ;
            break;

        }
        return null;
    }

    public static Node Expand(Node node, String operator) {
        if (operator.equals("Carry")) {
            // remove the hostage from the state and add the damage to the end of the state
            String state = node.getState();
            String newState = Carry(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        if (operator.equals("Drop")) {
            String state = node.getState();
            String newState = Drop(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        if (operator.equals("Kill")) {
            Kill(node);
        }
        if (operator.equals("TakePill")) {
            String state = node.getState();
            String newState = TakePill(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        if (operator.equals("Fly")) {
            String state = node.getState();
            String newState = Fly(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        if (operator.equals("Up")) {
            String state = node.getState();
            String newState = Up(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        if (operator.equals("Down")) {
            String state = node.getState();
            String newState = Down(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        if (operator.equals("Right")) {
            String state = node.getState();
            String newState = Right(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        } if (operator.equals("Left")) {
            String state = node.getState();
            String newState = Left(node);
            if (!state.equals(newState))
                return new Node(node, newState);
        }
        return node;
    }

    public static Node createInitialNode(String grid) {
        String[] gridArray = grid.split(";", 10);
        gridArray[2] = gridArray[2] + ",0";
        String state = String.join(";", gridArray) + ";";
        return new Node(null, state);
    }

    public static void printArray(String[] array) {
        for (String s : array) {
            System.out.println(s);
        }
    }

    private ArrayList<String> AvailableOperators(Node node) {
        ArrayList<String> availableOperators = new ArrayList<>(Arrays.asList(operators));
        String[] hostages = node.extractHostages();
        String[] neo = node.extractNeoPos();
        String[] agents = node.extractAgentsPos();
        String[] grid = node.extractGridSize();
        String maxCarry = node.extractMaxNoOfCarry();
        String[] carriedHostages = node.extractCarriedHostagesHP();
        String[] telephoneBooth = node.extractTelBoothPos();

        for (String operator : operators) {
            if (operator.equals("Carry")) {
                if (carriedHostages.length == Integer.parseInt(maxCarry))
                    availableOperators.remove("Carry");
            }

            if (operator.equals("Drop")) {
                if (!(neo[0].equals(telephoneBooth[0]) && neo[1].equals(telephoneBooth[1])))
                    availableOperators.remove("Drop");
            }

            if (operator.equals("Up")) {
                if (node.getOperator().equals("Down") || neo[0].equals("0")) {
                    availableOperators.remove("Up");
                }
            }

            if (operator.equals("Down")) {
                if (node.getOperator().equals("Up") || Integer.parseInt(neo[0]) == Integer.parseInt(grid[0]) - 1)
                    availableOperators.remove("Down");
            }

            if (operator.equals("Right")) {
                if (node.getOperator().equals("Left") || Integer.parseInt(neo[1]) == Integer.parseInt(grid[1]) - 1)
                    availableOperators.remove("Right");
            }

            if (operator.equals("Left")) {
                if (node.getOperator().equals("Right") || neo[1].equals("0"))
                    availableOperators.remove("Left");
            }
        }
        availableOperators.removeAll(AgentIllegalMoves(agents, neo));
        availableOperators.removeAll(HostageIllegalMoves(hostages, neo));
        return availableOperators;
    }

    private static ArrayList<String> HostageIllegalMoves(String[] hostages, String[] neo) {
        ArrayList<String> toBeRemovedMoves = new ArrayList<>();
        for (int i = 0; i < hostages.length - 2; i += 3) {
            if (Integer.parseInt(hostages[2]) >= 98) {
                // Check if we need to remove the UP move.
                if (hostages[i + 1].equals(neo[1]) && Integer.parseInt(hostages[i]) - 1 == Integer.parseInt(neo[0])) {
                    toBeRemovedMoves.add("Up");
                }
                // Check if we need to remove the Down move.
                if (hostages[i + 1].equals(neo[1]) && Integer.parseInt(hostages[i]) + 1 == Integer.parseInt(neo[0])) {
                    toBeRemovedMoves.add("Down");
                }
                // Check if we need to remove the Left move.
                if (hostages[i].equals(neo[0]) && Integer.parseInt(hostages[i + 1]) - 1 == Integer.parseInt(neo[1])) {
                    toBeRemovedMoves.add("Left");
                }
                // Check if we need to remove the Right move.
                if (hostages[i].equals(neo[0]) && Integer.parseInt(hostages[i + 1]) + 1 == Integer.parseInt(neo[1])) {
                    toBeRemovedMoves.add("Right");
                }
                // if neo is in a cell with a Hostage who will die on the next round and performs a kill operation.
                if (neo[0].equals(hostages[i]) && neo[1].equals(hostages[i + 1]) && Integer.parseInt(hostages[i + 2]) >= 98) {
                    toBeRemovedMoves.add("Kill");
                }
            }
        }
        return toBeRemovedMoves;
    }

    private static ArrayList<String> AgentIllegalMoves(String[] agents, String[] neo) {
        ArrayList<String> toBeRemovedMoves = new ArrayList<>();
        for (int i = 0; i < agents.length - 1; i += 2) {
            // Check if we need to remove the UP move.
            if (agents[i + 1].equals(neo[1]) && Integer.parseInt(agents[i]) - 1 == Integer.parseInt(neo[0])) {
                toBeRemovedMoves.add("Up");
            }
            // Check if we need to remove the Down move.
            if (agents[i + 1].equals(neo[1]) && Integer.parseInt(agents[i]) + 1 == Integer.parseInt(neo[0])) {
                toBeRemovedMoves.add("Down");
            }
            // Check if we need to remove the Left move.
            if (agents[i].equals(neo[0]) && Integer.parseInt(agents[i + 1]) - 1 == Integer.parseInt(neo[1])) {
                toBeRemovedMoves.add("Left");
            }
            // Check if we need to remove the Right move.
            if (agents[i].equals(neo[0]) && Integer.parseInt(agents[i + 1]) + 1 == Integer.parseInt(neo[1])) {
                toBeRemovedMoves.add("Right");
            }
        }
        return toBeRemovedMoves;
    }

    public static String Carry(Node node) {
        String state = node.getState();
        String[] arrayState = state.split(";", 10);
        String[] hostages = node.extractHostages();
        String[] neoPosition = node.extractNeoPos();
        String newHostages = "";
        for (int i = 0; i < hostages.length - 2; i += 3) {
            String xHostage = hostages[i];
            String yHostage = hostages[i + 1];
            String hostageDamage = hostages[i + 2];

            if (xHostage.equals(neoPosition[0]) && yHostage.equals(neoPosition[1])) {
                if (!arrayState[8].isEmpty())
                    arrayState[8] += ",";
                arrayState[8] += hostageDamage;
            } else {
                newHostages += xHostage + "," + yHostage + "," + hostageDamage + ",";
            }
        }
        newHostages = newHostages.substring(0, newHostages.length() - 1);
        arrayState[7] = newHostages;

        return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
    }

    public static String Drop(Node node) {
        String state = node.getState();
        String[] stateArray = state.split(";", 10);
        String[] carriedHostages = node.extractCarriedHostagesHP();

        for (String carriedHostage : carriedHostages)
            if (Integer.parseInt(carriedHostage) >= 100)
                deaths += 1;
        stateArray[8] = "";

        return String.join(";", stateArray).equals(state) ? null : String.join(";", stateArray);
    }

    private static boolean IsAdjacent(String neoX, String neoY, String agentX, String agentY) {
        int neoXInt = Integer.parseInt(neoX);
        int agentXInt = Integer.parseInt(agentX);
        int neoYInt = Integer.parseInt(neoY);
        int agentYInt = Integer.parseInt(agentY);

        if (neoXInt == agentXInt && (neoYInt == agentYInt + 1 || neoYInt == agentYInt - 1))
            return true;
        else return neoYInt == agentYInt && (neoXInt == agentXInt + 1 || neoXInt == agentXInt - 1);
    }
    public static String Up(Node node){
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] arrayState = state.split(";", 10);

        ArrayList<String> availableOperations = AvailableOperators(node);

        if(availableOperations.contains("Up")) {
            for (int i = 0; i < hostages.length - 2; i += 3) {
                int hostageY = hostages[i + 1];
                if (neoPosition[0].equals(hostages[i]) && neoPosition[1].equals(hostageY - 1) ) {
                    neoPosition[1] = hostages[i + 1];
                }
            }
            arrayState[2] = String.join(",", neoPosition);
            return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
        }
    }
    public static String Down(Node node){
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] arrayState = state.split(";", 10);

        ArrayList<String> availableOperations = AvailableOperators(node);

        if(availableOperations.contains("Down")) {
            for (int i = 0; i < hostages.length - 2; i += 3) {
                int hostageY = hostages[i + 1];
                if (neoPosition[0].equals(hostages[i]) && neoPosition[1].equals(hostageY + 1)) {
                    neoPosition[1] = hostages[i + 1];
                }
            }
            arrayState[2] = String.join(",", neoPosition);
            return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
        }
    }
    public static String Left(Node node){
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] arrayState = state.split(";", 10);

        ArrayList<String> availableOperations = AvailableOperators(node);

        if(availableOperations.contains("Left")) {
            for (int i = 0; i < hostages.length - 2; i += 3) {
                int hostageX = hostages[i];
                if (neoPosition[1].equals(hostages[i + 1]) && neoPosition[0].equals(hostageX - 1)) {
                    neoPosition[0] = hostages[i];
                }
            }
            arrayState[2] = String.join(",", neoPosition);
            return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
        }
    }
    public static String Right(Node node){
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] arrayState = state.split(";", 10);

        ArrayList<String> availableOperations = AvailableOperators(node);

        if(availableOperations.contains("Right")) {
            for (int i = 0; i < hostages.length - 2; i += 3) {
                int hostageX = hostages[i];
                if (neoPosition[1].equals(hostages[i + 1]) && neoPosition[0].equals(hostageX + 1)) {
                    neoPosition[0] = hostages[i];
                }
            }
            arrayState[2] = String.join(",", neoPosition);
            return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
        }
    }


    public static String Kill(Node node) {
        String[] stateArray = node.getState().split(";", 10);
        ArrayList<String> agents = new ArrayList<>(Arrays.asList(node.extractAgentsPos()));
        ArrayList<String> mutatedHostages = new ArrayList<>(Arrays.asList(node.extractMutatedHostagesPos()));
        String[] hostages = node.extractHostages();
        String[] neo = node.extractNeoPos();
        boolean killedSomeOne = false;

        for (int i = 0; i < hostages.length - 2; i += 3) {
            if (neo[0].equals(hostages[i]) && neo[1].equals(hostages[i + 1])) {
                int hostageDamage = Integer.parseInt(hostages[i + 2]);
                if (hostageDamage >= 98)
                    return null;
            }
        }

        for (int i = 0; i < mutatedHostages.size() - 1; i += 2) {
            if (IsAdjacent(neo[0], neo[1], mutatedHostages.get(i), mutatedHostages.get(i + 1))) {
                mutatedHostages.remove(i);
                mutatedHostages.remove(i);
                i -= 2;
                kills += 1;
                killedSomeOne = true;
            }
        }

        for (int i = 0; i < agents.size() - 1; i += 2) {
            if (IsAdjacent(neo[0], neo[1], agents.get(i), agents.get(i + 1))) {
                agents.remove(i);
                agents.remove(i);
                i -= 2;
                killedSomeOne = true;
            }
        }

        // Update Neo's damage if neo killed someone.
        if (killedSomeOne) {
            int neoDamage = Integer.parseInt(neo[2]);
            neoDamage = Math.min(100, neoDamage + 20);
            neo[2] = neoDamage + "";
            stateArray[2] = String.join(",", neo);
        }

        String newAgentsString = String.join(",", agents);
        String newMutatedHostagesString = String.join(",", mutatedHostages);
        stateArray[4] = newAgentsString;
        stateArray[9] = newMutatedHostagesString;

        String newState = String.join(";", stateArray);

        return (!newState.equals(node.getState()) ? newState : null);
    }

    private Node UpdateDamage(Node node) {
        String[] stateArray = node.getState().split(";", 10);
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
                i -= 3;
            }
        }
        stateArray[7] = String.join(",", hostages);
        stateArray[9] = String.join(",", mutatedHostages);

        for (int i = 0; i < carriedHostages.size(); i++) {
            int damage = Integer.parseInt(carriedHostages.get(i));
            damage = Math.min(100, damage + 2);
            carriedHostages.set(i, damage + "");
        }
        stateArray[8] = String.join(",", carriedHostages);

        return new Node(node.getParentNode(), String.join(";", stateArray));
    }

    public static String TakePill(Node node) {
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] hostages = node.extractHostages();
        String[] carriedHostages = node.extractCarriedHostagesHP();
        String[] arrayState = state.split(";", 10);
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
            arrayState[2] = String.join(",", neoPosition);
            arrayState[7] = String.join(",", hostages);
            arrayState[8] = String.join(",", carriedHostages);
            arrayState[5] = String.join(",", pills);
        }
        return String.join(";", arrayState).equals(state) ? null : String.join(";", arrayState);
    }

    public static String Fly(Node node) {
        String state = node.getState();
        String[] neoPosition = node.extractNeoPos();
        String[] padsPosition = node.extractPadPos();
        String[] arrayState = state.split(";", 10);
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

    public static boolean GoalTest(Node node) {
        String[] neoPosDam = node.extractNeoPos();
        String[] neoPos = new String[2];
        neoPos[0] = neoPosDam[0];
        neoPos[1] = neoPosDam[1];
        String[] telBoothPos = node.extractTelBoothPos();
        String[] hostages = node.extractHostages();
        String[] mutatedHostages = node.extractMutatedHostagesPos();

        // TODO : fix logical error here.
        if (neoPos.equals(telBoothPos) && hostages == null && mutatedHostages == null) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean GameOver(Node node) {
        String[] neo = node.extractNeoPos();
        return Integer.parseInt(neo[2]) >= 100;
    }

    public static void main(String[] args) {
        String grid = genGrid();
        Node initialNode = createInitialNode(grid);
//        String testString = "8,9;1;2,2,0;2,2;7,3,1,0,7,2,4,5,1,7,5,3,5,4,3,8,6,4,3,1;6,8,3,5,2,8,7,5;2,2,20,8,0,8,4,7,1,8,6,1,6,1,1,8,2,6,1,5,1,5,2,6,7,4,6,0,6,0,7,4,6,5,7,8,7,8,6,5,4,1,5,8,5,8,4,1;5,0,69,2,5,94,1,4,8,3,7,37,1,1,54;95;";
//		System.out.println(Carry(node));
//		System.out.println(Drop(node));
//		System.out.println(initialNode.getState());
//		System.out.println(Kill(initialNode));

    }
}
