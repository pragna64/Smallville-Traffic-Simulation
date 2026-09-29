import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private TownGraph graph;
    private AStarPathfinder pathfinder;
    private MapPanel mappanel;
    private JTextField startfield;
    private JTextField endfield;
    private JTextField waypointfield;
    private JComboBox<String> typecbox;
    private JSlider trafficslider;
    private String lastsearch = "NONE";

    public MainFrame() {
        graph = new TownGraph();
        graph.loadNodes("nodes.txt");
        graph.loadEdges("edges.txt");
        graph.scaleCoordinates();
        pathfinder = new AStarPathfinder(graph);
        mappanel = new MapPanel(graph, this);
        setTitle("Smallville");
        setSize(1200, 800);
        setLayout(new BorderLayout());
        add(mappanel, BorderLayout.CENTER);
        createControlPanel();
        updateSliderState();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateSliderState() {
        Edge selected = mappanel.getSelectedEdge();
        List<Node> currentrt = mappanel.getRoute();
        if (selected == null || currentrt == null || currentrt.isEmpty())
            trafficslider.setEnabled(false);
        else {
            trafficslider.setEnabled(true);
            trafficslider.setValue((int) selected.trafficmult);
        }
    }

    public void handleExternalGraphUpdate() {
        refreshActiveRoute();
    }

    private void refreshActiveRoute() {
        String start = startfield.getText().trim();
        String end = endfield.getText().trim();
        String waypoint = waypointfield.getText().trim();
        String selectedType = (String) typecbox.getSelectedItem();
        if (lastsearch.equals("ROUTE")) {
            if (!waypoint.isEmpty() && !waypoint.equals("N4")) {
                List<Node> part1 = pathfinder.findPath(start, waypoint);
                List<Node> part2 = pathfinder.findPath(waypoint, end);
                if (!part1.isEmpty() && !part2.isEmpty()) {
                    List<Node> fullPath = new java.util.ArrayList<>(part1);
                    fullPath.remove(fullPath.size() - 1);
                    fullPath.addAll(part2);
                    mappanel.setRoute(fullPath);
                } 
                else
                    mappanel.setRoute(java.util.Collections.emptyList());
            } 
            else {
                List<Node> route = pathfinder.findPath(start, end);
                mappanel.setRoute(route);
            }
        } 
        else if (lastsearch.equals("NEAREST")) {
            if (selectedType != null && !selectedType.equals("None")) {
                List<Node> route = pathfinder.findPathToNearestType(start, selectedType);
                mappanel.setRoute(route);
            }
        }
    }

    private void createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 247, 250));

        startfield = new JTextField(5);
        endfield = new JTextField(5);
        waypointfield = new JTextField(5);
        styleTextField(startfield, "N1");
        styleTextField(waypointfield, "N4");
        styleTextField(endfield, "N8");
        String[] types = {"None", "school", "park", "shop", "government", "hospital", "industrial", "transport", "residential"};
        typecbox = new JComboBox<>(types);
        typecbox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton routeButton = new JButton("Find Route");
        styleButton(routeButton);
        JButton nearestButton = new JButton("Find Nearest Type");
        styleButton(nearestButton);
        trafficslider = new JSlider(1, 10, 1);
        styleSlider(trafficslider);
        JLabel title1 = new JLabel("Route Planning");
        title1.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel title2 = new JLabel("Traffic Control");
        title2.setFont(new Font("Arial", Font.BOLD, 14));

        routeButton.addActionListener(e -> {
            lastsearch = "ROUTE";
            refreshActiveRoute();
            updateSliderState();
        });
        nearestButton.addActionListener(e -> {
            lastsearch = "NEAREST";
            refreshActiveRoute();
            updateSliderState();
        });
        trafficslider.addChangeListener(e -> {
            Edge selected = mappanel.getSelectedEdge();
            if (selected != null && trafficslider.isEnabled()) {
                selected.trafficmult = trafficslider.getValue();
                refreshActiveRoute();
            }
        });

        panel.add(title1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Start Node"));
        panel.add(startfield);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Waypoint Node (Optional)"));
        panel.add(waypointfield);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("End Node"));
        panel.add(endfield);
        panel.add(Box.createVerticalStrut(15));
        panel.add(routeButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JLabel("Or Search Closest Category:"));
        panel.add(typecbox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(nearestButton);
        panel.add(Box.createVerticalStrut(25));
        panel.add(title2);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Traffic Level"));
        panel.add(trafficslider);
        add(panel, BorderLayout.EAST);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(209, 232, 213));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createLineBorder(new Color(160, 190, 165), 1));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private void styleTextField(JTextField field, String hint) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setText(hint);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void styleSlider(JSlider slider) {
        slider.setBackground(new Color(245, 247, 250));
        slider.setForeground(new Color(60, 60, 60));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(2);
        slider.setMinorTickSpacing(1);
    }
}