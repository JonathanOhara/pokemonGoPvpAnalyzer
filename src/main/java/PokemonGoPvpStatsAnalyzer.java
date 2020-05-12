import enums.League;
import enums.PokemonInLeague;
import model.PokemonOverallStat;
import model.PokemonWithWeight;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

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
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static enums.PokemonInLeague.hasPokemon;
import static util.StringUtil.formatAndTruncate;

public class PokemonGoPvpStatsAnalyzer {

	private final boolean DEBUG 					= true;

	private final League league;

	public PokemonGoPvpStatsAnalyzer(League league) {
		this.league = league;
	}

	public void printByOverallStats() throws IOException, InterruptedException {
		Set<PokemonOverallStat> pokemonStats =
				new TreeSet<>(Comparator.comparingLong(PokemonOverallStat::getOverallStat));

		System.setProperty("webdriver.gecko.driver","C:\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();

		driver.get("https://pvpoke.com/battle/");

		Thread.sleep(2500);

		Select leagueSelect = new Select(driver.findElement(By.className("league-select")));
		leagueSelect.selectByValue(String.valueOf(league.getMaxCp()));

		Thread.sleep(2500);

		Select pokemonSelect = new Select(driver.findElement(By.className("poke-select")));

		if (DEBUG) {
			System.out.println("Size before filter: " + pokemonSelect.getOptions().size());
		}

		List<WebElement> filteredOptions = pokemonSelect.getOptions().stream()
				.filter(option -> hasPokemon(league, option.getAttribute("value")))
				.collect(Collectors.toList());

		if (DEBUG) {
			System.out.println("Size after filter: " + filteredOptions.size());
		}

		for(WebElement webElement: filteredOptions){
			String pokemonSimpleName = webElement.getAttribute("value");
			pokemonSelect.selectByValue(pokemonSimpleName);

			long overallStat = Long.parseLong(driver.findElement(
					By.cssSelector("div.poke-stats > div.stat-container.overall.clear > div > span.stat")
			).getText());

			PokemonOverallStat pokemon = new PokemonOverallStat(pokemonSimpleName, overallStat);

			if (DEBUG) {
				System.out.println(pokemon);
			}

			pokemonStats.add(pokemon);
		}
		driver.close();

		pokemonStats.forEach(p -> System.out.println(p));
	}
}