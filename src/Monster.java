import java.util.*;

public class Monster {
    private Game game;
    private Dungeon dungeon;
    private int N;

    public Monster(Game game) {
        this.game = game;
        this.dungeon = game.getDungeon();
        this.N = dungeon.size();
    }

    // Take a step towards the rogue using BFS to find the shortest path
    public Site move() {
        List<Site> path = shortestPathFromMonsterToRogue();
        if (path != null && path.size() > 1) {
            return path.get(1); // Move to the next step in the path
        }
        return game.getMonsterSite(); // Stay in the same place if no path found
    }

    private List<Site> getNeighbors(Site site) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; // N, S, W, E, NW, NE, SW, SE
        List<Site> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int i = site.i() + dir[0];
            int j = site.j() + dir[1];
            if (i >= 0 && i < dungeon.size() && j >= 0 && j < dungeon.size() && !dungeon.isWall(new Site(i,j))) {
                neighbors.add(new Site(i, j));
            }
        }
        return neighbors;
    }    

    private List<Site> shortestPathFromMonsterToRogue() {
        Site monsterSite = game.getMonsterSite();
        Site rogueSite = game.getRogueSite();
    
        boolean[][] visited = new boolean[N][N];
        Queue<Site> queue = new LinkedList<>();
        Map<Site, Site> parentMap = new HashMap<>();
    
        // Start BFS from monster's position
        queue.offer(monsterSite);
        visited[monsterSite.i()][monsterSite.j()] = true;
    
        while (!queue.isEmpty()) {
            Site current = queue.poll();
    
            // Check if rogue's position is reached
            if (current.equals(rogueSite)) {
                return reconstructPath(parentMap, monsterSite, rogueSite);
            }
    
            // Explore neighbors
            for (Site neighbor : getNeighbors(current)) {
                if (!visited[neighbor.i()][neighbor.j()] && dungeon.isLegalMove(current, neighbor)) {
                    visited[neighbor.i()][neighbor.j()] = true;
                    queue.offer(neighbor);
                    parentMap.put(neighbor, current); // Keep track of parent to reconstruct the path
                }
            }
        }
    
        // If rogue's position is unreachable
        return Collections.emptyList();
    }

    private List<Site> reconstructPath(Map<Site, Site> parentMap, Site start, Site end) {
        List<Site> path = new ArrayList<>();
        Site current = end;
        while (!current.equals(start)) {
            path.add(current);
            current = parentMap.get(current);
            if (current == null) {
                return Collections.emptyList(); // No path found
            }
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

}
