package src;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

/**
 * Created by mattlim on 11/17/14.
 * Class for visualizing UserGraph.
 */
public class GraphView {

    public GraphView() {

    }

    /**
     * Get a directed graph that contains the connected components of all the passed-in users.
     * @param users a list of users
     * @return      the directed graph
     */
    private DirectedGraph<Integer, String> getGraph(ArrayList<User> users) {
        Deque<User> queue = new ArrayDeque<User>();
        DirectedGraph<Integer, String> directedGraph = new DirectedSparseMultigraph<Integer, String>();
        for (User user : users) {
            if (user.color == User.Color.WHITE)
                queue.addLast(user);
            while (!queue.isEmpty()) {
                User u = queue.pollFirst();
                if (user.color == User.Color.WHITE)
                    directedGraph.addVertex(u.getId());
                for (User v : u.getStudents()) {
                    directedGraph.addVertex(v.getId());
                    directedGraph.addEdge(u.getId() + "/" + u.getSiteVersion() + "-" + v.getId() + "/" + v.getSiteVersion(),
                            u.getId(), v.getId());
                    if (v.color == User.Color.WHITE) {
                        v.color = User.Color.GREY;
                        queue.addLast(v);
                    }
                }
                for (User v : u.getTeachers()) {
                    directedGraph.addVertex(v.getId());
                    directedGraph.addEdge(v.getId() + "/" + v.getSiteVersion() + "-" + u.getId() + "/" + u.getSiteVersion(),
                            v.getId(), u.getId());
                    if (v.color == User.Color.WHITE) {
                        v.color = User.Color.GREY;
                        queue.addLast(v);
                    }
                }
                u.color = User.Color.BLACK;
            }
        }
        return directedGraph;
    }

    /**
     * Get a forest that contains the connected components of all the passed-in users.
     * @param users a list of users
     * @return      the directed graph
     */
    private Forest<Integer, String> getForest(ArrayList<User> users) {
        DirectedGraph<Integer, String> directedGraph = getGraph(users);
        Forest<Integer, String> delegateForest = new DelegateForest<Integer, String>(directedGraph);
        return delegateForest;
    }

    /**
     * Show the graph/forest that contains the connected components of all the passed-in users.
     * @param users           a list of users
     * @param name            the name of the graph
     * @param infectedUserIds a list of infected user ids. These users will be colored green.
     */
    protected void showGraph(ArrayList<User> users, String name, final ArrayList<Integer> infectedUserIds) {
        Forest<Integer, String> forest = getForest(users);
        Layout<Integer, String> layout = new SpringLayout2<Integer, String>(forest);
        VisualizationViewer<Integer, String> vv =
                new VisualizationViewer<Integer, String>(layout);
        Transformer<Integer,Paint> vertexColor = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
                if(infectedUserIds.contains(i))
                    return Color.GREEN;
                return Color.RED;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        vv.setPreferredSize(new Dimension(750, 750)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        JFrame frame = new JFrame(name);
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}
