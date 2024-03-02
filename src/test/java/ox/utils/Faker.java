package ox.utils;

import java.util.List;

public class Faker {

    private static List<String> prefixes = List.of("Bulbasaur", "Ivysaur", "Venusaur", "Charmander",
            "Charmeleon", "Charizard", "Squirtle", "Wartortle", "Blastoise", "Caterpie", "Metapod",
            "Butterfree", "Weedle", "Kakuna", "Beedrill", "Pidgey", "Pidgeotto", "Pidgeot", "Rattata", "Raticate",
            "Spearow", "Fearow", "Ekans", "Arbok", "Pikachu", "Raichu", "Sandshrew", "Sandslash", "Nidoran",
            "Nidorina", "Nidoqueen", "Nidoran", "Nidorino", "Nidoking", "Clefairy", "Clefable", "Vulpix", "Ninetales",
            "Gyarados", "Lapras", "Ditto", "Eevee", "Vaporeon", "Jolteon", "Flareon", "Porygon", "Omanyte",
            "Omastar", "Kabuto", "Kabutops", "Aerodactyl", "Snorlax", "Articuno", "Zapdos", "Moltres",
            "Dratini", "Dragonair", "Dragonite", "Mewtwo", "Mew");

    public static String fakeDBName() {
        return "db_" + Faker.prefixes.get((int) (Math.random() * Faker.prefixes.size())) + "_" + (int) (Math.random() * 1000);
    }

}
