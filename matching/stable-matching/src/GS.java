import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

class GS {
    private static Integer n;

    private static ArrayList<String> men = new ArrayList<>();
    private static ArrayList<String> women = new ArrayList<>();

    private static Integer[][] menPref;
    private static Integer[][] womenPref;

    private static Integer[][] ranking;

    public static void main(String[] args) {
        String fileName = args[0];

        fileReader(fileName);

        StableMatching matcher = new StableMatching(n, menPref, ranking);

        Integer[] engagements = matcher.galeShapley();
        for (int i = 0; i < n; i++) {
            System.out.println(men.get(i) + " -- " + women.get(engagements[i]));
        }
    }

    private static void fileReader(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()).startsWith("#")) {
                // we have skipped comments
            }

            n = Integer.parseInt(line.substring(2));
            menPref = new Integer[n][n];
            womenPref = new Integer[n][n];

            //read the names
            for (int i = 0; i < 2 * n; i++) {
                line = br.readLine();
                String name = line.substring(line.indexOf(" ")).trim();
                if (i % 2 == 0) {
                    men.add(name);
                } else {
                    women.add(name);
                }
            }

            //empty line
            line = br.readLine();

            //read the preferences
            for (int i = 0; i < 2 * n; i++) {
                line = br.readLine();
                Integer[] preference = Arrays.stream(line.substring(line.indexOf(":") + 2).split(" "))
                        .map(a -> (Integer.parseInt(a) - 1) / 2)
                        .toArray(Integer[]::new);

                // Split preferences into men, women and update their refrences to names.
                if (i % 2 == 0) {
                    menPref[i / 2] = preference;
                } else {
                    womenPref[i / 2] = preference;
                }
            }

            // Creates constant lookup time for women ranking of men.
            ranking = new Integer[n][n];
            for (int w = 0; w < n; w++) {
                Integer[] wPrefs = womenPref[w];
                for (Integer rank = 0; rank < n; rank++) {
                    Integer m = wPrefs[rank];
                    ranking[w][m] = rank;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class StableMatching {
    private final Integer n;
    private Stack<Integer> stackBachelors;
    private Integer[][] menPref;
    private Integer[] crush;
    private Integer[] current;
    private Integer[] result;
    private Integer[][] ranking;


    public StableMatching(Integer n,
                          Integer[][] menPref,
                          Integer[][] ranking) {
        this.n = n;

        this.stackBachelors = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            stackBachelors.push(i);
        }

        this.menPref = menPref;
        this.crush = new Integer[n];
        Arrays.fill(crush, 0);
        this.current = new Integer[n];
        this.result = new Integer[n];
        Arrays.fill(current, null);
        this.ranking = ranking;
    }

    public Integer[] galeShapley() {
        while (!stackBachelors.isEmpty()) {
            Integer m = stackBachelors.pop();
            Integer w = menPref[m][crush[m]];
            crush[m]++;
            if (current[w] == null) {
                current[w] = m;
                result[m] = w;
            } else {
                Integer currentMan = current[w];
                if (ranking[w][currentMan] < ranking[w][m]) {
                    stackBachelors.push(m);
                } else {
                    current[w] = m;
                    result[m] = w;
                    stackBachelors.push(currentMan);
                }
            }
        }
        return result;
    }

}