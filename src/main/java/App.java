import enums.League;
import enums.PokemonInLeague;
import model.PokemonWithWeight;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

//    private static final String SEARCH_STRING = "giratina_altered:34,cresselia:24,swampert:14,registeel:14,empoleon:14,melmetal:13,escavalier:11,obstagoon:10";
    private static final String SEARCH_STRING = "giratina_altered:46,cresselia:45,swampert:50,registeel:44,togekiss:31,charizard:24,obstagoon:12,snorlax:17,clefable:18,gyarados:14,lapras:11\n";

    private static final String DEFAULT_LEAGUE = "Ultra";
    private static final String DEFAULT_NUMBER_OF_SHIELDS = "1";
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
                            Integer.parseInt(pokemonName.split(":")[1].replace("\n","")) : 1;
                    return new PokemonWithWeight(pokemonInLeague, weight);
                })
                .collect(Collectors.toList());

        PokemonGoPvpBattleAnalyzer pokemonGoPvpAnalyzer = new PokemonGoPvpBattleAnalyzer(league);

        pokemonGoPvpAnalyzer.printBestCounter(
                pokemonInLeagueList,
                Integer.parseInt(args[2]),
                Integer.parseInt(args[3])
        );
    }

    private static String[] resolveArgs(String[] args) {
        if(args.length == 0){
            printDefaultMessage();
            args = new String[]{DEFAULT_LEAGUE, SEARCH_STRING, DEFAULT_NUMBER_OF_SHIELDS, NUMBER_OF_RESULTS};
        }else if(args.length == 1){
            printHelpMessage(args);
            System.exit(0);
        }else if(args.length == 2){
            args = new String[]{args[0], args[1], DEFAULT_NUMBER_OF_SHIELDS, NUMBER_OF_RESULTS};
        }else if(args.length == 3){
            args = new String[]{args[0], args[1], args[2], NUMBER_OF_RESULTS};
        }
        return args;
    }

    private static void printHelpMessage(String[] args) {
        String firstParam = args[0];
        if(firstParam.equals("help") || firstParam.equals("-h") || firstParam.equals("--help")) {
            System.out.println("Params: ");
            System.out.println("PVP League.          Possible Values: Great, Ultra, Master ");
            System.out.println("Leads.               Possible Values: Altaria, Swampert, Giratina (Altered).... ");
            System.out.println("Number of Shields.   Possible Values: 0....2 ");
            System.out.println("Number of Results.   Possible Values: 0....250 ");
        }else{
            System.out.println("Invalid argument, try help instead");
        }
    }

    private static void printDefaultMessage() {
        System.out.println("Using Default parameters. Use \"help\" to check the parameters");
        System.out.println("Default params: ");
        System.out.println("League: "+DEFAULT_LEAGUE);
        System.out.println("Leads: "+ SEARCH_STRING);
        System.out.println("Number of Results: "+NUMBER_OF_RESULTS);
    }
}
