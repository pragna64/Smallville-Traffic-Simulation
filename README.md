# Smallville-Traffic-Simulation
Traffic simulation that explores how graph algorithms are used in the real-world. Locations/intersections are represented as nodes and roads as weighted edges. The system will use the shortest path algorithm (Dijkstra’s) to search for the most efficient route. 

To start: Run the Main.java file.

To interact with the map-like interface:
Pan/Zoom: Click and drag to pan, use the mouse wheel to zoom.
Hover: Hover over any road to view its street name.

Main features:
1. Route Planning
Enter a Start Node (e.g., N2) and an End Node (e.g., N8).
Optional: Enter a Waypoint Node to add a stop along the journey.
Click 'Find Route' to highlight the best path in green.

2. Traffic Level & Road Closures
Adjust Traffic: Click a road (yellow highlight) and drag the Traffic Level slider to dynamically adjust road weights and reroute best paths.
Toggle Closures: Double-click any road to close it (displays a red X), forcing paths to recalculate around it.
To undo road closures, double-click the road again.

3. Closest Category Search
Enter a Start Node.
Select a location type (e.g., school, park, shop) from the drop-down menu.
Click 'Find Nearest Type' to map a path to the closest matching destination.
