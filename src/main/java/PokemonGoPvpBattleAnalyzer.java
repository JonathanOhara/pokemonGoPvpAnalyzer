import leagues.League;
import leagues.PokemonInLeague;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static util.StringUtil.formatAndTruncate;

public class PokemonGoPvpBattleAnalyzer {

	private final boolean DEBUG 					= false;

	private final boolean USE_CACHE 				= true;
	private final boolean NORMALIZE_WIN_VALUE_RATIO = true;

	private final boolean SHOW_DETAILS 				= true;
	private final boolean IGNORE_WEIGHT 			= false;

	private final boolean SHOW_SHADOW_POKEMON		= true;
	private final boolean DISABLE_BAIT_SHIELDS		= true;

	private final League league;

    public PokemonGoPvpBattleAnalyzer(League league) {
		this.league = league;
    }

    public List<Score> getBestCounters(List<PokemonWithWeight> pokemonList, int numberOfShields) throws IOException, InterruptedException {

       	Set<Score> inputPokemonScore = new HashSet<>();

		for (PokemonWithWeight weightedInput : pokemonList) {
			PokemonInLeague inputPokemon = weightedInput.getPokemonInLeague();
			Set<Map.Entry<String, String>> pokemonScoreList =
					getPokemonNameScoreMapOf(inputPokemon.getUrl(numberOfShields), inputPokemon.getSimpleName()).entrySet();

			for (Map.Entry<String, String> pokemonScore : pokemonScoreList) {
				String pokemonSimpleName = pokemonScore.getKey();

				Score score = inputPokemonScore.stream()
						.filter(score1 -> score1.getPokemonName().equals(pokemonSimpleName)).findFirst()
						.orElse(new Score(pokemonSimpleName, weightedInput.getWeight()));

				score.add(
						inputPokemon.getSimpleName(),
						getMatchupScoreValue(pokemonScore)
				);

				inputPokemonScore.add(score);
			}
		}

		List<Score> orderedScore = orderScore(new ArrayList<>(inputPokemonScore));

		return orderedScore;
    }

	public List<Score> orderScore(List<Score> orderedScore) {

		if( IGNORE_WEIGHT ){
			Collections.sort(orderedScore, Comparator.comparingInt(Score::sum));
		}else{
			Collections.sort(orderedScore, Comparator.comparingInt(Score::sumWithWeight));
		}

		if( !SHOW_SHADOW_POKEMON ){
			orderedScore = orderedScore.stream().filter(score -> !score.getPokemonName().contains("_shadow")).collect(Collectors.toList());
		}
		return orderedScore;
	}

	public void printScore(List<Score> orderedScore, int numberOfResults) {
		System.out.println("");

		for(int i = 0; i < numberOfResults; i++){
			Score score = orderedScore.get(i);

			StringBuilder output = new StringBuilder();

			output
					.append(String.format("%3d", i+1))
					.append("| ")
					.append(formatAndTruncate(score.getPokemonName(), 18))
					.append(" | ")
					.append("Pt: ")
					.append(score.sum())
					.append(" | ")
					.append("PtWe: ")
					.append(score.sumWithWeight())
					.append(" | ");


			if(SHOW_DETAILS) {
				output.append("Dt: ");

				score.getResults().forEach(s ->
						output
								.append(formatAndTruncate(s.getPokemonName(), 12))
								.append(": ")
								.append(String.format("%4s", s.getBattleScore()))
								.append(" ")
				);

				output.append("|");
			}

			System.out.println(output.toString());
		}
	}

	private int getMatchupScoreValue(Map.Entry<String, String> pokemonScore) {
		int value = Integer.parseInt(pokemonScore.getValue());

		if(NORMALIZE_WIN_VALUE_RATIO) {
			if (value < 300) {
				value = -2;
			} else if (value < 480) {
				value = -1;
			} else if (value < 520) {
				value = 0;
			} else if (value < 700) {
				value = 1;
			} else {
				value = 2;
			}
		}

		return value;
	}

	private Map<String, String> getPokemonNameScoreMapOf(String url, String pokemonSimpleName) throws InterruptedException, IOException {
    	Map<String, String> pokemonScore = new LinkedHashMap<>();

    	String cacheName = createCacheNameFor(pokemonSimpleName, url);

		if(DEBUG){
			System.out.println("Pokemon:    " + pokemonSimpleName);
			System.out.println("Cache Name: " + cacheName);
		}

    	if(USE_CACHE){
			pokemonScore = getPokemonScoreFromCache(cacheName);
		}

    	if(pokemonScore.isEmpty()){
			pokemonScore = getPokemonScoreFromHttp(url);
			updateCache(url, cacheName, pokemonScore);
		}

		return pokemonScore;

	}

	private String createCacheNameFor(String pokemonName, String url) {
		String subUrl = url.substring(url.indexOf(pokemonName.toLowerCase()));

		return subUrl.replaceAll("/", "_");
	}

	private void updateCache(String url, String cacheName, Map<String, String> pokemonScore) throws IOException {
		String projectPath = new File(".").getCanonicalPath();
    	Properties properties = new Properties();
		properties.putAll(pokemonScore);

		String filePath = projectPath
				+ "/src/main/resources/"
				+ getCachePath(cacheName);

		System.out.println("Writing cache at: "+filePath);

		properties.store(new FileOutputStream(filePath), url);
	}

	private String getCachePath(String simpleName) throws IOException {

		StringBuilder filePath = new StringBuilder();

		filePath
				.append("cache/")
				.append(league.toString().toLowerCase())
				.append("/")
				.append(simpleName.toLowerCase())
				.append("bait_")
				.append(!DISABLE_BAIT_SHIELDS)
				.append(".properties");

		return filePath.toString();
	}

	private Map<String, String> getPokemonScoreFromCache(String cacheName) throws IOException {
		System.out.println("Reading data from cache for "+cacheName);

		String cachePath = getCachePath(cacheName);

		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream(cachePath);

		if(in == null){
			return new LinkedHashMap<>();
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

		System.setProperty("webdriver.gecko.driver","C:/Users/Jonathan/Documents/geckodriver.exe");
		WebDriver driver = new FirefoxDriver();

		driver.get(url);

		System.out.print("Acessing "+url);
		do{
			Thread.sleep(600);
			System.out.print(".");
		}while(driver.findElements(By.cssSelector(".rankings-container > div")).size() == 0);
		System.out.println();


		WebElement acceptButton = driver.findElement(By.cssSelector("button.ncmp__btn:nth-child(2)"));
		if(acceptButton != null) {
			acceptButton.click();
		}

		//selection all pokemon (by default the only "meta" is choose)
		driver.findElement(By.cssSelector("div.poke-stats:nth-child(4) > div:nth-child(2) > div:nth-child(2) > div:nth-child(2)")).click();

		if(DISABLE_BAIT_SHIELDS){
			System.out.print("Disabling Shield baiting...");
			//expand form
			driver.findElement(By.cssSelector("#main > div.section.poke-select-container.multi > div:nth-child(1) > div.poke-stats > div.options > a")).click();

			//disable button 1
			driver.findElement(By.cssSelector("div.poke:nth-child(1) > div:nth-child(6) > div:nth-child(12) > div:nth-child(4) > div:nth-child(6) > div:nth-child(1)")).click();

			//disable button 2
			driver.findElement(By.cssSelector("div.poke-stats:nth-child(4) > div:nth-child(3) > div:nth-child(4) > div:nth-child(1)")).click();
		}

		//Re run battles
		driver.findElement(By.cssSelector("#main > div.section.battle > button.battle-btn.button")).click();

		do{
			Thread.sleep(500);
			System.out.print(".");
		}while(driver.findElements(By.cssSelector(".rankings-container > div")).size() == 0);

		List<WebElement> scoreElements = driver.findElements(By.cssSelector("div.section:nth-child(11) > div:nth-child(2) > div:nth-child(4) > .rank"));

		if(DEBUG) {
			System.out.println("Score Elements: " + scoreElements.size());
		}
		for (WebElement scoreElement : scoreElements) {
			String data = scoreElement.getAttribute("data");
			String star = scoreElement.findElement(By.cssSelector(".star")).getText();
			pokemonScore.put(data, star);
			if(DEBUG){
				System.out.println("\tx "+ data + ": "+ star);
			}
		}


		driver.quit();
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

		public void join(List<BattleResult> otherResults) {
			results.forEach(currentResult -> {
				otherResults.stream()
						.filter(r -> r.getPokemonName().equals(currentResult.getPokemonName()))
						.findFirst()
						.ifPresentOrElse(
								otherResult -> {
									currentResult.addBattleScore(otherResult.getBattleScore());
								},
								() -> {
									throw new RuntimeException("Battle not found for "+currentResult.getPokemonName());
								}
						);
			});
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

		public int getWeight() {
			return weight;
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

			public void addBattleScore(int score){
				this.battleScore += score;
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