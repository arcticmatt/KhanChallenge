package Interviews.KhanAcademy;

import java.util.*;

/**
 * Created by mattlim on 11/16/14.
 * Represents a graph of users. Does so by holding all users in a HashMap, where the HashMap
 * maps userIds (Integers) to Users (User objects). The edges in the graph are represented by the
 * lists "students" and "teachers" in each User object. So basically this class represents the graph
 * as an adjacency list.
 */
public class UserGraph {
    private HashMap<Integer, User> userMap;
    private ArrayList<User> userList;
    ArrayList<Integer> infectedUserIds = new ArrayList<Integer>();
    // Used to show the graph visualization
    private GraphView graphView = new GraphView();
    private int numUsers;

    /**
     * Default constructor.
     */
    public UserGraph() {
        userMap = new HashMap<Integer, User>();
        numUsers = 0;
    }

    /**
     * Constructor that makes a UserGraph object with the passed in userMap.
     * @param userMap a HashMap that maps userIds to users
     */
    public UserGraph(HashMap<Integer, User> userMap) {
        this.userMap = userMap;
        this.numUsers = userMap.size();
        this.userList = new ArrayList<User>(userMap.values());
    }

    /**
     * Adds a user to userMap.
     * @param newUser the user to add
     */
    public void addUser(User newUser) {
        userMap.put(newUser.getId(), newUser);
        numUsers++;
        userList.add(newUser);
    }

    /**
     * Gets a user.
     * @param userId the userId of the user to get
     * @return       the user (if it exists) or null
     */
    public User getUser(Integer userId) {
        if (userMap.containsKey(userId))
            return userMap.get(userId);
        return null;
    }

    /**
     * Gets the list of users.
     * @return the list of users, as an ArrayList
     */
    public ArrayList<User> getUserList() {
        return userList;
    }

    /**
     * Does a simple limited infection of the graph. It will stop infecting the graph as soon
     * as the passed in limit is reached (or if the connected component has been totally infected),
     * regardless of whether or not the current component has been
     * totally infected.
     * @param userId      the userId to start the limited infection from
     * @param siteVersion the siteVersion to infect users with
     * @param limit       the limit on number of users to infect
     * @return            true if userId is valid, false if not
     */
    public boolean limitedInfection(int userId, int siteVersion, int limit) {
        if (userMap.containsKey(userId)) {
            User sourceUser = userMap.get(userId);
            breadthInfectSimple(sourceUser, siteVersion, limit, true);
            return true;
        }
        return false;
    }

    /**
     * Does a smarter/contained limited infection of the graph. This method gets all the
     * trees of the graph, and finds the tree that has the number of vertices closest to the passed-in
     * limit parameter. It then completely infects that tree.
     * @param userId      the userId to start the limited infection from
     * @param siteVersion the siteVersion to infect users with
     * @param limit       the limit on number of users to infect. In this case, not a hard limit.
     * @return            the id of the user from which the limited infection was started, or -1.
     */
    public int limitedInfectionSmart(int userId, int siteVersion, int limit) {
        ArrayList<UserTree> trees = getTrees(userList);
        int minDiff = Integer.MAX_VALUE;
        int sourceId = userId;
        int newLimit = limit;
        for (UserTree tree : trees) {
            int userDiff = Math.abs(limit - tree.getNumUsers());
            if (userDiff < minDiff) {
                minDiff = userDiff;
                sourceId = tree.getSourceId();
                newLimit = tree.getNumUsers();
            }
        }
        if (userMap.containsKey(sourceId)) {
            System.out.println("sourceUser = " + sourceId + ", minDiff = " + minDiff + ", newLimit = " + newLimit);
            User sourceUser = userMap.get(sourceId);
            breadthInfectSimple(sourceUser, siteVersion, newLimit, true);
            return sourceId;
        }
        return -1;
    }

    /**
     * Tries to do the smartest limited infection of the graph. Calls a helper method to do the actual work.
     * See overloaded method below for more details.
     * @param siteVersion the siteVersion to infect users with
     * @param limit       the limit on number of users to infect
     * @return            true if exactly "limit" number of users can be infected, false otherwise
     */
    public boolean limitedInfectionRecursive(int siteVersion, int limit) {
        return limitedInfectionRecursive(siteVersion, limit, getTrees(userList), new ArrayList<UserTree>());
    }

    /**
     * Tries to do the smartest limited infection of the graph. This method tries all possible combinations
     * of infecting different trees of the graph, in an attempt to infect exactly "limit" number of users.
     * Since it gets a list of trees that are sorted in descending order, it tends to select combinations
     * such that the first tree covers a large number of nodes.
     * @param siteVersion    the siteVersion to infect users with
     * @param limit          the limit on number of users to infect. In this case, this is exactly enforced
     * @param remainingTrees the trees that have not yet been considered
     * @param removedTrees   the trees that have been considered (and thus removed)
     * @return               true if exactly "limit" number of users can be infected, false otherwise
     */
    private boolean limitedInfectionRecursive(int siteVersion, int limit, ArrayList<UserTree> remainingTrees,
                                             ArrayList<UserTree> removedTrees) {
        if (limit == 0) {
            ArrayList<User> users = new ArrayList<User>();
            for (UserTree userTree : removedTrees) {
                if (userMap.containsKey(userTree.getSourceId()))
                    users.add(userMap.get(userTree.getSourceId()));
            }
            breadthInfectMultiple(users, siteVersion, Integer.MAX_VALUE);
            System.out.println("here with users.size() = " + users.size());
            return true;
        }
        for (int i = 0; i < remainingTrees.size(); i++) {
            int userDiff = limit - remainingTrees.get(i).getNumUsers();
            if (userDiff >= 0) {
                ArrayList<UserTree> newTrees = new ArrayList<UserTree>(remainingTrees);
                removedTrees.add(newTrees.remove(i));
                return limitedInfectionRecursive(siteVersion, userDiff, newTrees, removedTrees);
            }
        }
        return false;
    }

    /**
     * Totally infects a connected component of the graph.
     * @param userId      the userId to start the total infection from
     * @param siteVersion the siteVersion to infect users with
     * @return            true if userId is valid, false otherwise
     */
    public boolean totalInfection(int userId, int siteVersion) {
        if (userMap.containsKey(userId)) {
            User sourceUser = userMap.get(userId);
            breadthInfectSimple(sourceUser, siteVersion, userMap.size(), true);
            return true;
        }
        return false;
    }

    /**
     * Infects the graph by running BFS starting from each user in a list.
     * @param users       the users to start BFS from
     * @param siteVersion the siteVersion to infect users with
     * @param limit       the limit on number of users to infect
     * @return            the number of users infected
     */
    private int breadthInfectMultiple(ArrayList<User> users, int siteVersion, int limit) {
        setUserColors(User.Color.WHITE);
        int count = 0;
        for (User u : users) {
            count += breadthInfectSimple(u, siteVersion, limit, false);
        }
        return count;
    }

    /**
     * Infects the graph by running BFS starting from a single user.
     * @param sourceUser  the user to start the BFS from
     * @param siteVersion the siteVersion to infect users with
     * @param limit       the limit on number of users to infect
     * @param setWhite    a boolean that determines whether or not to reset all user colors to white
     * @return            the number of users infected
     */
    private int breadthInfectSimple(User sourceUser, int siteVersion, int limit, boolean setWhite) {
        if (setWhite)
            setUserColors(User.Color.WHITE);
        Deque<User> queue = new ArrayDeque<User>();
        if (sourceUser.color == User.Color.WHITE)
            queue.addLast(sourceUser);
        int count = 0;
        while (!queue.isEmpty() && count < limit) {
            User u = queue.pollFirst();
            for (User v : u.getStudents()) {
                if (v.color == User.Color.WHITE) {
                    v.color = User.Color.GREY;
                    queue.addLast(v);
                }
            }
            for (User v : u.getTeachers()) {
                if (v.color == User.Color.WHITE) {
                    v.color = User.Color.GREY;
                    queue.addLast(v);
                }
            }
            u.color = User.Color.BLACK;
            u.setSiteVersion(siteVersion);
            infectedUserIds.add(u.getId());
            count++;
        }
        return count;
    }

    /**
     * Gets a list of trees in the graph that cover at least the list of passed in users.
     * @param users the list of users that the list of trees must cover
     * @return      a list of trees that covers the passed in list of users
     */
    private ArrayList<UserTree> getTrees(ArrayList<User> users) {
        setUserColors(User.Color.WHITE);
        ArrayList<UserTree> userTrees = new ArrayList<UserTree>();
        for (User user : users) {
            ArrayList<User> treeList = new ArrayList<User>();
            Deque<User> queue = new ArrayDeque<User>();
            if (user.color == User.Color.WHITE)
                queue.addLast(user);
            while (!queue.isEmpty()) {
                User u = queue.pollFirst();
                treeList.add(u);
                for (User v : u.getStudents()) {
                    if (v.color == User.Color.WHITE) {
                        v.color = User.Color.GREY;
                        queue.addLast(v);
                    }
                }
                for (User v : u.getTeachers()) {
                    if (v.color == User.Color.WHITE) {
                        v.color = User.Color.GREY;
                        queue.addLast(v);
                    }
                }
                u.color = User.Color.BLACK;
            }
            if (!treeList.isEmpty()) {
                UserTree userTree = new UserTree(treeList);
                userTrees.add(userTree);
            }
        }
        Collections.sort(userTrees, Collections.reverseOrder());
        return userTrees;
    }

    /**
     * Sets the color of every user in the graph to the passed in color.
     * @param color the color to use
     */
    private void setUserColors(User.Color color) {
        for (User u : userMap.values()) {
            u.color = color;
        }
    }

    /**
     * Show the graph using graphView.
     */
    public void showEntireGraph() {
        setUserColors(User.Color.WHITE);
        ArrayList<User> users = new ArrayList<User>(userMap.values());
        graphView.showGraph(users, "Entire User Graph", infectedUserIds);
    }

    /**
     * Show the connected component of the passed-in user.
     * @param userId the userId of the user
     * @return       true if userId is valid, false otherwise
     */
    public boolean showUserGraph(int userId) {
        if (userMap.containsKey(userId)) {
            setUserColors(User.Color.WHITE);
            User user = userMap.get(userId);
            ArrayList<User> users = new ArrayList<User>();
            users.add(user);
            graphView.showGraph(users, "User #" + userId + " Graph", infectedUserIds);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Random random = new Random();
        HashMap<Integer, User> users = new HashMap<Integer, User>();
        for (int i = 0; i < 150; i++) {
            User u = new User(i, 0);
            int count = 0;
            while (i > 0 && count < random.nextInt(2)) {
                int j = random.nextInt(i);
                User v = users.get(j);
                boolean nextBool = random.nextBoolean();
                if (nextBool) {
                    u.addStudent(v);
                    count++;
                } else {
                    u.addTeacher(v);
                    count++;
                }
            }
            users.put(i, u);
        }
        UserGraph userGraph = new UserGraph(users);
        int id = 8;
        //int sourceVertex = userGraph.limitedInfectionSmart(8, 7, 10);
        //userGraph.totalInfection(10, 1);
        userGraph.limitedInfectionRecursive(7, 22);
        userGraph.showEntireGraph();
        //userGraph.showUserGraph(sourceVertex);
    }
}