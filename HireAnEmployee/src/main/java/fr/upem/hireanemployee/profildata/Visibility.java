package fr.upem.hireanemployee.profildata;

/**
 * Created by Quentin on 18/02/2016.
 * <p>
 * The list of Visiblities doesn't change, so it's a enumeration for now.
 * We may change to a table for internationalization of the texts.
 */
public enum Visibility {

    PUBLIC("Publique"), PRIVATE("Privée");//, RELATION("Relations");

    private final String textValue;

    Visibility(String value) {
        textValue = value;
    }

    public static Visibility stringToCountry(String text) {
        return Visibility.valueOf(text.toUpperCase());
    }

    @Override
    public String toString() {
        return textValue;
    }
}
