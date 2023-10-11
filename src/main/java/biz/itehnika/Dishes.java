package biz.itehnika;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Dishes {
    static final Random RANDOM = new Random();
    static final List<String> dishesNames = new ArrayList<>(Arrays.asList(
        "Onion soup",           "Tomato soup",          "Mushroom cream soup",  "Chicken broth",    "Fish soup",
        "Vegetable soup",       "Caesar salad",         "Greek salad",          "Prawn cocktail",   "Garden fresh salad",
        "Nicoise salad",        "Caprese salad",        "Chips",                "Nachos",           "Onion rings",
        "Garlic bread",         "Potato pancakes",      "Club sandwich",        "Carpaccio",        "BBQ ribs",
        "Cheese & bacon burger","Cheeseburger",         "Tuna&egg sandwich",    "Steak",            "Spaghetti",
        "Lasagna",              "Risotto",              "Pizza",                "Oysters",          "Meatballs",
        "Schnitzel",            "Grilled vegetables",   "French fries",         "Boiled potatoes",  "Mashed potatoes",
        "Scrambled eggs",       "Fried eggs",           "Bacon and eggs",       "Porridge",         "Ice-cream",
        "Vanilla pudding",      "Herbal tea",           "Coffee",               "Americano",        "Cappucino",
        "Still water",          "Sparkling water",      "Juice",                "Wine",             "Semi-sweet wine",
        "Semi-dry wine",        "Sparkling wine",       "Beer","Draught beer",  "Liqueur",          "Whiskey",
        "Rum",                  "Vodka"));


    public static String getRandomName(){
        return dishesNames.get(RANDOM.nextInt(dishesNames.size()));
    }

    public static int getRandomPrice(){
        return RANDOM.nextInt(900) + 100;
    }

    public static int getRandomWeight(){
        return RANDOM.nextInt(500) + 50;
    }

    public static boolean getRandomDiscount(){
        return RANDOM.nextBoolean();
    }

}

