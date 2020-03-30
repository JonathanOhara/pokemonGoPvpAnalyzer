package enums;

public interface PokemonInLeague {

    String getDisplayName();
    String getSimpleName();
    String getUrl();

    default boolean equalsBothNames(String name){
        return getSimpleName().equalsIgnoreCase(name) || getDisplayName().equalsIgnoreCase(name);
    }

    static PokemonInLeague getPokemonByName(League league, String name){
        return league.getAvailablePokemon().stream()
                .filter(pokemon -> pokemon.equalsBothNames(name))
                .findFirst().orElseThrow(() -> new RuntimeException(name + " not found"));
    }
}
