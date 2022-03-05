import leagues.League;
import leagues.PokemonInLeague;
import model.PokemonWithWeight;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    private static final String LEADS = "medicham:7,talonflame:5,stunfisk_galarian:4,trevenant:4,walrein:4,swampert:3,jellicent:3,ninetales_alolan:3,hypno:3,venusaur:3,deoxys_defense:3,scrafty:3\n";
//    private static final String LEADS = "altaria,skarmory,deoxys_defense,trevenant,sableye";
//    private static final String BACKS = "sableye:86,nidoqueen:81,azumarill:74,medicham:62,walrein:59,stunfisk_galarian:58,trevenant:58,skarmory:48,registeel:46,ninetales_alolan:44,deoxys_defense:40,scrafty:40,altaria:36,obstagoon:30,umbreon:28,swampert:26,bastiodon:24,drapion:22,lickitung:21,talonflame:17,venusaur:14,mandibuzz:14,sirfetchd:14,jellicent:13,victreebel:12,diggersby:11,toxicroak:10,cofagrigus:8,vigoroth:8,wigglytuff:8\n";
    private static final String OTHER = "";

    private static final String SEARCH_STRING = LEADS;

    private static final String DEFAULT_LEAGUE = "Great";
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

        List<PokemonGoPvpBattleAnalyzer.Score> scores;
        if(args[2].toUpperCase() == "X"){
            scores = joinScores(
                    pokemonGoPvpAnalyzer.getBestCounters(pokemonInLeagueList, 0),
                    pokemonGoPvpAnalyzer.getBestCounters(pokemonInLeagueList, 1),
                    pokemonGoPvpAnalyzer.getBestCounters(pokemonInLeagueList, 2)
            );
            scores = pokemonGoPvpAnalyzer.orderScore(scores);
        }else {
            scores = pokemonGoPvpAnalyzer.getBestCounters(
                    pokemonInLeagueList,
                    Integer.parseInt(args[2]));
        }

        pokemonGoPvpAnalyzer.printScore(scores, Integer.parseInt(args[3]));
    }

    private static List<PokemonGoPvpBattleAnalyzer.Score> joinScores(List<PokemonGoPvpBattleAnalyzer.Score> scores0, List<PokemonGoPvpBattleAnalyzer.Score> scores1, List<PokemonGoPvpBattleAnalyzer.Score> scores2) {

        for (PokemonGoPvpBattleAnalyzer.Score score : scores0) {

            scores1.stream()
                    .filter(otherScore -> otherScore.getPokemonName().equals(score.getPokemonName()))
                    .findFirst()
                    .ifPresentOrElse(
                            otherScore -> {
                                score.join(otherScore.getResults());
                            }
                            ,
                            () -> {throw new RuntimeException("Score1 not Found!");}
                    );

            scores2.stream()
                    .filter(otherScore -> otherScore.getPokemonName().equals(score.getPokemonName()))
                    .findFirst()
                    .ifPresentOrElse(
                            otherScore -> {
                                score.join(otherScore.getResults());
                            }
                            ,
                            () -> {throw new RuntimeException("Score1 not Found!");}
                    );
        }

        return scores0;
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
            System.out.println("PVP League.          Possible Values: Great, Ultra, UltraPremier, Master, MasterPremier ");
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
