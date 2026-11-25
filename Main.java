
import java.util.*;

public class Main {

    static class Edge {
        String to, mode;
        int time, cost;

        Edge(String to, String mode, int time, int cost) {
            this.to = to;
            this.mode = mode;
            this.time = time;
            this.cost = cost;
        }
    }

    static Map<String, List<Edge>> graph = new LinkedHashMap<>();

    static void add(String a, String b, String mode, int t, int c) {
        graph.putIfAbsent(a, new ArrayList<>());
        graph.putIfAbsent(b, new ArrayList<>());
        graph.get(a).add(new Edge(b, mode, t, c));
        graph.get(b).add(new Edge(a, mode, t, c));
    }

    static void createGraph() {
        add("Sector1", "Market", "Bus", 20, 10);
        add("Sector1", "Station", "Metro", 10, 20);
        add("Station", "Market", "Metro", 8, 15);
        add("Market", "Airport", "Bus", 12, 10);
        add("Station", "Airport", "Bus", 25, 15);
    }

    static class Node {
        String name;
        int time, cost;
        List<String> path;
        List<Edge> edges;

        Node(String n, int t, int c, List<String> p, List<Edge> e) {
            name = n; time = t; cost = c;
            path = p; edges = e;
        }
    }

    static void print(Node n) {
        for (int i = 0; i < n.edges.size(); i++)
            System.out.print(n.path.get(i) + " -(" +
                n.edges.get(i).mode + ")-> ");

        System.out.println(n.name);
        System.out.println("Stops: " + n.edges.size() +
            " | Time: " + n.time + " min | Cost: Rs. " + n.cost);
    }

    static Node search(String s, String d, int option) {
        PriorityQueue<Node> q = new PriorityQueue<>(
            Comparator.comparingInt(n -> option == 2 ?
                n.edges.size() : n.time)
        );

        q.add(new Node(s, 0, 0, new ArrayList<>(List.of(s)),
                       new ArrayList<>()));

        Set<String> visited = new HashSet<>();

        while (!q.isEmpty()) {
            Node n = q.poll();

            if (n.name.equals(d)) return n;
            if (!visited.add(n.name)) continue;

            for (Edge e : graph.get(n.name)) {
                if (!visited.contains(e.to)) {
                    List<String> p = new ArrayList<>(n.path);
                    List<Edge> es = new ArrayList<>(n.edges);
                    p.add(e.to);
                    es.add(e);

                    q.add(new Node(e.to, n.time + e.time,
                        n.cost + e.cost, p, es));
                }
            }
        }
        return null;
    }

    static void dfs(String s, String d, Set<String> visited,
                    Node n, List<Node> routes) {

        if (s.equals(d)) {
            routes.add(n);
            return;
        }

        visited.add(s);

        for (Edge e : graph.get(s)) {
            if (!visited.contains(e.to)) {
                List<String> p = new ArrayList<>(n.path);
                List<Edge> es = new ArrayList<>(n.edges);
                p.add(e.to);
                es.add(e);

                dfs(e.to, d, new HashSet<>(visited),
                    new Node(e.to, n.time + e.time,
                    n.cost + e.cost, p, es), routes);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        createGraph();

        System.out.println("1. Fastest Route");
        System.out.println("2. Fewest Stops");
        System.out.println("3. All Routes");

        System.out.print("Option: ");
        int op = sc.nextInt();
        sc.nextLine();

        if (op < 1 || op > 3) {
            System.out.println("Invalid option.");
            return;
        }

        System.out.print("From: ");
        String s = sc.nextLine().trim();

        System.out.print("To: ");
        String d = sc.nextLine().trim();

        if (!graph.containsKey(s) || !graph.containsKey(d)) {
            System.out.println("Stop not found.");
        } else if (s.equals(d)) {
            System.out.println("Already at " + s);
            System.out.println("Time: 0 min | Cost: Rs. 0");
        } else if (op == 3) {
            List<Node> routes = new ArrayList<>();

            dfs(s, d, new HashSet<>(),
                new Node(s, 0, 0, new ArrayList<>(List.of(s)),
                new ArrayList<>()), routes);

            routes.sort(Comparator.comparingInt(n -> n.time));

            System.out.println(routes.size() + " routes found:");
            for (Node n : routes) print(n);

        } else {
            Node result = search(s, d, op);

            if (result == null)
                System.out.println("No route found.");
            else
                print(result);
        }

        sc.close();
    }
}
