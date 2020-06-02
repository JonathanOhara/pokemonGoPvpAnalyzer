import enums.League;
import model.PokemonOverallStat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static enums.PokemonInLeague.hasPokemon;

public class PokemonGoPvpStatsAnalyzer {

	private final boolean SHOW_SHADOW_POKEMON		= false;

	private final boolean DEBUG 					= false;

	private final League league;

	public PokemonGoPvpStatsAnalyzer(League league) {
		this.league = league;
	}

	public void printByOverallStats() throws IOException, InterruptedException {
		List<PokemonOverallStat> pokemonStats = new ArrayList<>(league.getAvailablePokemon().size());

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

			if (pokemonSimpleName.contains("_shadow") && !SHOW_SHADOW_POKEMON) continue;

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

		pokemonStats.sort(Comparator.comparingLong(PokemonOverallStat::getOverallStat));
		System.out.println("*******************************************");
		pokemonStats.forEach(p -> System.out.println(p));
	}
}