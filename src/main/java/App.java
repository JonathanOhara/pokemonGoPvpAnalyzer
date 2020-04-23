import enums.League;
import enums.PokemonInLeague;
import model.PokemonWithWeight;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
My teams:
https://pvpoke.com/team-builder/all/10000/rhyperior-40-15-15-14-4-4-1-m-0-2-6%2Ctogekiss-41-15-15-15-4-4-1-m-1-1-4%2Cgiratina_origin-40-14-13-15-4-4-1-m-1-3-1
rhyperior,MUD_SLAP,ROCK_WRECKER,SURF,40,15,15,14
togekiss,CHARM,AERIAL_ACE,FLAMETHROWER,41,15,15,15
giratina_origin,SHADOW_CLAW,SHADOW_BALL,DRAGON_PULSE,40,14,13,15
 */
public class App {

    private static final String DEFAULT_LEAGUE = "Master";
    private static final String DEFAULT_LEADS = "melmetal:72,dialga:69,giratina_origin:64,kyogre:41,togekiss:37\n";
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
            System.out.println("PVP League.          Possible Values: Great, Ultra, Master ");
            System.out.println("Leads.               Possible Values: Altaria, Swampert, Giratina (Altered).... ");
            System.out.println("Number of Results.   Possible Values: 0....250 ");
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
