import enums.League;
import enums.PokemonInLeague;
import model.PokemonWithWeight;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    private static final String DEFAULT_LEAGUE = "Ultra";
    private static final String DEFAULT_LEADS = "swampert:16,poliwrath:9,venusaur:8,giratina_altered:8,snorlax:8,togekiss:7,muk_alolan:7,melmetal:6,articuno:6,clefable:6";
    private static final String NUMBER_OF_RESULTS = "50";

    public static void main(String[] args) throws IOException, InterruptedException {

        args = resolveArgs(args);

        League league = League.valueOf(args[0].toUpperCase());
        List<PokemonWithWeight> pokemonInLeagueList =
                Stream.of(args[1].split(","))
                .map(pokemonName -> {
                    PokemonInLeague pokemonInLeague =
                            PokemonInLeague.getPokemonByName(league, pokemonName.split(":")[0].trim());
                    int weight = pokemonName.split(":").length == 2 ?
                            Integer.parseInt(pokemonName.split(":")[1]) : 1;
                    return new PokemonWithWeight(pokemonInLeague, weight);
                })
                .collect(Collectors.toList());

        PokemonGoPvpAnalyzer pokemonGoPvpAnalyzer = new PokemonGoPvpAnalyzer(league);

        pokemonGoPvpAnalyzer.printBestCounter(
                pokemonInLeagueList,
                Integer.parseInt(args[2])
        );
    }

    private static String[] resolveArgs(String[] args) {
        if(args.length == 0){
            printDefaultMessage();
            args = new String[]{DEFAULT_LEAGUE, DEFAULT_LEADS, NUMBER_OF_RESULTS};
        }else if(args.length == 1){
            printHelpMessage(args);
            System.exit(0);
        }else if(args.length == 2){
            args = new String[]{args[0], args[1], NUMBER_OF_RESULTS};
        }
        return args;
    }

    private static void printHelpMessage(String[] args) {
        String firstParam = args[0];
        if(firstParam.equals("help") || firstParam.equals("-h") || firstParam.equals("--help")) {
            System.out.println("Params: ");
            System.out.println("PVP League.         Possible Values: Great, Ultra, Master ");
            System.out.println("Leads.              Possible Values: Altaria, Swampert, Giratina (Altered).... ");
            System.out.println("Nuber of Results.   Possible Values: 0....250 ");
        }else{
            System.out.println("Invalid argument, try help instead");
        }
    }

    private static void printDefaultMessage() {
        System.out.println("Using Default parameters. Use \"help\" to check the parameters");
        System.out.println("Default params: ");
        System.out.println("League: "+DEFAULT_LEAGUE);
        System.out.println("Leads: "+DEFAULT_LEADS);
        System.out.println("Number of Results: "+NUMBER_OF_RESULTS);
    }
}
