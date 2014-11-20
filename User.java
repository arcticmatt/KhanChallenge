package Interviews.KhanAcademy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Created by mattlim on 11/16/14.
 * Represents a user.
 */
public class User {
    // Used for traversing graphs of users.
    public enum Color {
        WHITE, GREY, BLACK
    }
    protected Color color = Color.WHITE;
    private int siteVersion = 0;
    private int id;
    private Deque<User> students = new ArrayDeque<User>();
    private Deque<User> teachers = new ArrayDeque<User>();

    /**
     * Makes a user with the passed-in userId.
     * @param userId the userId to make the user with
     */
    public User(int userId) {
        id = userId;
    }

    /**
     * Makes a user with the passed-in userId and siteVer.
     * @param userId  the userId to make the user with
     * @param siteVer the siteVersion to make the user with
     */
    public User(int userId, int siteVer) {
        id = userId;
        siteVersion = siteVer;
    }

    /**
     * @return the id of the user
     */
    public int getId() {
        return id;
    }

    /**
     * @return the site version of the user
     */
    public int getSiteVersion() {
        return siteVersion;
    }

    /**
     * Sets the siteVersion of the user to the passed-in number.
     * @param newSiteVersion the new site version
     */
    public void setSiteVersion(int newSiteVersion) {
        siteVersion = newSiteVersion;
    }

    /**
     * @return the list of this user's students
     */
    public Deque<User> getStudents() {
        return students;
    }

    /**
     * Sets the students of the user to the passed-in collection.
     * @param newStudents the new collection of students
     */
    public void setStudents(Deque<User> newStudents) {
        students = newStudents;
    }

    /**
     * Adds a student to this user's students.
     * @param u the student to add
     */
    public void addStudent(User u) {
        if (!isDuplicate(u.id, students)) {
            students.add(u);
            u.teachers.add(this);
        }
    }

    /**
     * @return the list of this user's teachers
     */
    public Deque<User> getTeachers() {
        return teachers;
    }

    /**
     * Sets the teachers of the user to the passed-in collection.
     * @param newTeachers the new collection of teachers
     */
    public void setTeachers(Deque<User> newTeachers) {
        teachers = newTeachers;
    }

    /**
     * Adds a teacher to this user's teachers.
     * @param u the teacher to add
     */
    public void addTeacher(User u) {
        if (!isDuplicate(u.id, teachers)) {
            teachers.add(u);
            u.students.add(this);
        }
    }

    /**
     * Checks for duplicate ids in a collection.
     * @param id   the id to check for
     * @param list the list to check
     * @return     true if id is a duplicate, false otherwise
     */
    public boolean isDuplicate(int id, Deque<User> list) {
        for (User u : list) {
            if (u.id == id)
                return true;
        }
        return false;
    }
}