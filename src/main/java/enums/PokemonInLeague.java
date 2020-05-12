package enums;

public interface PokemonInLeague {

    String getDisplayName();
    String getSimpleName();
    String getUrl(int numberOfShields);

    default boolean equalsBothNames(String name){
        return getSimpleName().equalsIgnoreCase(name) || getDisplayName().equalsIgnoreCase(name);
    }

    static PokemonInLeague getPokemonByName(League league, String name){
        return league.getAvailablePokemon().stream()
                .filter(pokemon -> pokemon.equalsBothNames(name))
                .findFirst().orElseThrow(() -> new RuntimeException(name + " not found"));
    }

    static boolean hasPokemon(League league, String name){
        return league.getAvailablePokemon().stream()
                .filter(pokemon -> pokemon.equalsBothNames(name))
                .count() > 0;
    }
}