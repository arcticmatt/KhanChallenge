package src;

import java.util.ArrayList;

/**
 * Created by mattlim on 11/18/14.
 * Represents a tree of users.
 */
public class UserTree implements Comparable<UserTree> {
    private int numUsers;
    protected ArrayList<User> users = new ArrayList<User>();
    private int sourceId;

    /**
     * Construct a UserTree with the passed in userList
     * @param userList the list of users
     */
    public UserTree(ArrayList<User> userList) {
        users = userList;
        numUsers = userList.size();
        if (users.size() > 0)
            sourceId = users.get(0).getId();
        else
            sourceId = -1;
    }

    /**
     * @return the number of users in the tree
     */
    public int getNumUsers() {
        return numUsers;
    }

    /**
     * @return the source id of the tree. determined somewhat randomly - just needed so
     * that there exists a method to get SOME user in the tree
     */
    public int getSourceId() {
        return sourceId;
    }

    /**
     * Compares on user to another using the number of users
     * @param other the user to compare to
     * @return      returns a negative integer, zero, or a positive integer
     *              as this object is less than, equal to, or greater than the other object
     */
    public int compareTo(UserTree other){
        return ((Integer) numUsers).compareTo(other.getNumUsers());
    }
}
