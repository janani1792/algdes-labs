package dk.itu.b;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class CP {
    public static void main(String[] args) {
        File file = new File(args[0]);
        List<Point2D> points = readFile(file);
        double result = ClosestPoints.find(points);

        System.out.format("%s: %d %f %n", args[0].substring(0, args[0].length() - 4).replace('-', '.'), points.size(), result);
    }

    private static List<Point2D> readFile(File file) {
        List<Point2D> points = new ArrayList<>();

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String a = sc.nextLine();
                if (a.startsWith("EOF"))
                    break;
                if (a.startsWith("NAME")) {
                    while (sc.hasNextLine()) {
                        if (sc.nextLine().startsWith("NODE_COORD_SECTION")) {
                            a = sc.nextLine();
                            break;
                        }
                    }
                }
                String[] b = a.split(" ");
                if (b.length >= 3) {
                    Point2D p = new Point2D.Double(Double.parseDouble(b[1]), Double.parseDouble(b[2]));
                    points.add(p);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return points;
    }
}

class ClosestPoints {
    public static double find(List<Point2D> points) {
        List<Point2D> Px = sort(points, Comparator.comparing(Point2D::getX));
        List<Point2D> Py = sort(points, Comparator.comparing(Point2D::getY));

        return closestPairs(Px, Py);
    }

    private static List<Point2D> sort(List<Point2D> points, Comparator<Point2D> comparing) {
        return points.stream().sorted(comparing).collect(Collectors.toList());
    }

    private static double closestPairs(List<Point2D> Px, List<Point2D> Py) {
        if (Px.size() <= 3) {
            return bruteForce(Px);
        }
        Pair<List<Point2D>> QxRx = divide(Px);

        double p1 = closestPairs(QxRx.getLeft(), Py);
        double p2 = closestPairs(QxRx.getRight(), Py);

        double delta = Math.min(p1, p2);
        double L = Px.get(Px.size() / 2).getX();

        List<Point2D> strip = Py.stream()
                .filter(point -> Math.abs(L - point.getX()) < delta)
                .collect(Collectors.toList());

        double min = delta;

        for (int i = 0; i < Math.max(strip.size() - 15, strip.size()); i++) {
            for (int j = i + 1; j < Math.min(15, strip.size()); j++) {
                double distance = Py.get(i).distance(Py.get(j));
                if (distance < min) {
                    min = distance;
                }
            }
        }

        return min;
    }

    private static double bruteForce(List<Point2D> points) {
        double min = Double.MAX_VALUE;
        Pair<Point2D> res = null;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                double distance = points.get(j).distance(points.get(i));
                if (distance < min)
                    min = distance;
                res = new Pair<>(points.get(i), points.get(j));
            }
        }
        return min;
    }

    private static Pair<List<Point2D>> divide(List<Point2D> points) {
        int size = points.size();

        List<Point2D> l = points.subList(0, size / 2);
        List<Point2D> r = points.subList(size / 2, size);

        return new Pair<>(l, r);
    }
}

class Pair<G> {
    private G left;
    private G right;

    public Pair(G left, G b) {
        this.left = left;
        this.right = b;
    }

    public G getLeft() {
        return left;
    }

    public G getRight() {
        return right;
    }
}