package leagues;

import leagues.special.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum League {
    GREAT(1500, PokemonGreatLeague::values),
    LITTLE(500, PokemonLittleLeague::values),
    ULTRA(2500, PokemonUltraLeague::values),
    ULTRAPREMIER(2500, PokemonUltraPremierLeague::values),
    MASTER(10000, PokemonMasterLeague::values),
//    MASTERPREMIER(10000, PokemonUltraLeague::values)

    //SPECIAL
    REMIX(1500, PokemonRemixLeague::values),
    ULTRAREMIX(2500, PokemonUltraRemixLeague::values),
    RETRO(1500, PokemonRetroLeague::values),
    HALLOWEEN(1500, PokemonHalloweenLeague::values),
    LOVE(1500, PokemonLoveLeague::values),
    KANTO(1500, PokemonKantoLeague::values),
    HOLIDAY(1500, PokemonHolidayLeague::values)
    ;

    private List<PokemonInLeague> availablePokemon;
    private int maxCp;

    League(int maxCP, Supplier<PokemonInLeague[]> pokemonSupplier){
        this.maxCp = maxCP;
        availablePokemon = Arrays.asList(pokemonSupplier.get());
    }

    public List<PokemonInLeague> getAvailablePokemon() {
        return availablePokemon;
    }

    public int getMaxCp() {
        return maxCp;
    }
}
