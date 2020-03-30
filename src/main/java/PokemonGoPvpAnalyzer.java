import enums.League;
import enums.PokemonInLeague;
import model.PokemonWithWeight;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class PokemonGoPvpAnalyzer {

	private final boolean USE_CACHE = true;
	private final boolean SHOW_DETAILS = true;


	private final League league;

    public PokemonGoPvpAnalyzer(League league) {
		this.league = league;

    }

    public void printBestCounter(List<PokemonWithWeight> pokemonList, int numberOfResults) throws IOException, InterruptedException {

       	Set<Score> scores = new HashSet<>();

		for (PokemonWithWeight commonPokemon : pokemonList) {


			Set<Map.Entry<String, String>> pokemonScoreList =
					getPokemonNameScoreMapOf(commonPokemon.getPokemonInLeague().getUrl(), commonPokemon.getPokemonInLeague().getSimpleName()).entrySet();

			for (Map.Entry<String, String> pokemonScore : pokemonScoreList) {
				String pokemonSimpleName = pokemonScore.getKey();

				Score score = scores.stream()
						.filter(score1 -> score1.getPokemonName().equals(pokemonSimpleName)).findFirst()
						.orElse(new Score(pokemonSimpleName, commonPokemon.getWeight()));

				score.add(
						commonPokemon.getPokemonInLeague().getSimpleName(),
						Integer.parseInt(pokemonScore.getValue())
				);

				scores.add(score);
			}
		}

		List<Score> orderedScore = new ArrayList<>(scores);
		Collections.sort(orderedScore, Comparator.comparingInt(Score::sumWithWeight));

		for(int i = 0; i < numberOfResults; i++){
			Score score = orderedScore.get(i);
			StringBuilder output = new StringBuilder();

			output
					.append("| ")
					.append(String.format("%17s", score.getPokemonName()))
					.append(" | ")
					.append("Pt: ")
					.append(score.sum())
					.append(" | ");


			if(SHOW_DETAILS) {
				output.append("Dt: ");

				score.getResults().forEach(s ->
						output
								.append(String.format("%17s", s.getPokemonName()))
								.append(": ")
								.append(String.format("%4s", s.getBattleScore()))
								.append(" ")
				);

				output.append("|");
			}

			System.out.println(output.toString());
		}
    }

    private Map<String, String> getPokemonNameScoreMapOf(String url, String pokemonSimpleName) throws InterruptedException, IOException {
    	Map<String, String> pokemonScore = new LinkedHashMap<>();

    	if(USE_CACHE){
			pokemonScore = getPokemonScoreFromCache(pokemonSimpleName);
		}

    	if(pokemonScore.isEmpty()){
			pokemonScore = getPokemonScoreFromHttp(url);
			updateCache(url, pokemonSimpleName, pokemonScore);
		}

		return pokemonScore;

	}

	private void updateCache(String url, String simpleName, Map<String, String> pokemonScore) throws IOException {
		String projectPath = new File(".").getCanonicalPath();
    	Properties properties = new Properties();
		properties.putAll(pokemonScore);

		String filePath = projectPath
				+ "/src/main/resources/"
				+ getCachePath(simpleName);

		System.out.println("Writing cache at: "+filePath);

		properties.store(new FileOutputStream(filePath), null);

	}

	private String getCachePath(String simpleName) throws IOException {

		StringBuilder filePath = new StringBuilder();

		filePath
				.append("cache/")
				.append(league.toString().toLowerCase())
				.append("/")
				.append(simpleName.toLowerCase())
				.append(".properties");

		return filePath.toString();
	}

	private Map<String, String> getPokemonScoreFromCache(String simpleName) throws IOException {
		System.out.println("Reading data from cache for "+simpleName);

		String cachePath = getCachePath(simpleName);

		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream(cachePath);

		if(in == null){
			return new HashMap<>();
		}

		prop.load(in);

		return prop.entrySet().stream().collect(
				Collectors.toMap(
						e -> e.getKey().toString(),
						e -> e.getValue().toString()
				)
		);
	}

	private Map<String, String> getPokemonScoreFromHttp(String url) throws InterruptedException {
		Map<String, String> pokemonScore = new LinkedHashMap<>();

		System.out.println("Reading data HTTP...");

		System.setProperty("webdriver.gecko.driver","C:\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();

		driver.get(url);

		System.out.print("Acessing "+url);
		do{
			Thread.sleep(500);
			System.out.print(".");
		}while(driver.findElements(By.cssSelector(".rankings-container > div")).size() == 0);
		System.out.println();

		List<WebElement> scoreElements = driver.findElements(By.cssSelector(".rankings-container > .rank"));

		for (WebElement scoreElement : scoreElements) {
			pokemonScore.put( scoreElement.getAttribute("data"), scoreElement.findElement(By.cssSelector(".star")).getText() );
		}

		driver.close();
		return pokemonScore;
	}


	class Score{
    	private String pokemonName;
    	private int weight;
    	private List<BattleResult> results = new ArrayList<>();

		public Score(String pokemonName, int weight) {
			this.pokemonName = pokemonName;
			this.weight = weight;
		}

		public void add(String pokemonName, int battleScore){
    		results.add(new BattleResult(pokemonName, battleScore));
			Collections.sort(results, Comparator.comparingInt(BattleResult::getBattleScore));
		}

		public List<BattleResult> getResults() {
			return results;
		}

		public int sum(){
    		return results.stream().mapToInt(BattleResult::getBattleScore).sum();
		}

		public int sumWithWeight(){
			return results.stream().mapToInt(BattleResult::getBattleScore).sum() * weight;
		}

		public void removeWorstResult(){
    		results.remove(results.size() -1);
		}

		public String getPokemonName() {
			return pokemonName;
		}

		class BattleResult{
    		private String pokemonName;
    		private int battleScore;


			public BattleResult(String pokemonName, int battleScore) {
				this.pokemonName = pokemonName;
				this.battleScore = battleScore;
			}

			public String getPokemonName() {
				return pokemonName;
			}

			public int getBattleScore() {
				return battleScore;
			}

		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Score score = (Score) o;
			return Objects.equals(pokemonName, score.pokemonName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(pokemonName);
		}
	}
}