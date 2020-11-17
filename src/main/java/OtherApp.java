import leagues.League;

import java.io.IOException;

public class OtherApp {

    private static final String DEFAULT_LEAGUE = "Little";

    public static void main(String[] args) throws IOException, InterruptedException {
        args = resolveArgs(args);

        League league = League.valueOf(args[0].toUpperCase());

        PokemonGoPvpStatsAnalyzer pokemonGoPvpStatsAnalyzer  = new PokemonGoPvpStatsAnalyzer(league);

        pokemonGoPvpStatsAnalyzer.printByOverallStats();
    }

    private static String[] resolveArgs(String[] args) {
        if(args.length == 0){
            printDefaultMessage();
            args = new String[]{DEFAULT_LEAGUE};
        }else if(args.length == 1){
            if(isHelp(args[0])) {
                printHelpMessage(args);
                System.exit(0);
            }
        }

        return args;
    }

    private static void printHelpMessage(String[] args) {
        System.out.println("Params: ");
        System.out.println("PVP League.          Possible Values: Great, Ultra, Master ");
    }

    private static boolean isHelp(String firstParam) {
        return firstParam.equals("help") || firstParam.equals("-h") || firstParam.equals("--help");
    }

    private static void printDefaultMessage() {
        System.out.println("Using Default parameters. Use \"help\" to check the parameters");
        System.out.println("Default params: ");
        System.out.println("League: "+DEFAULT_LEAGUE);
    }
}
