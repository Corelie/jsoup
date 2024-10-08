package org.jsoup.nodes;
import com.github.javafaker.Faker;

import org.jsoup.Jsoup;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeTest {
    @Test
    public void html() {
        Attribute attr = new Attribute("key", "value &");
        assertEquals("key=\"value &amp;\"", attr.html());
        assertEquals(attr.html(), attr.toString());
    }

    @Test public void testWithSupplementaryCharacterInAttributeKeyAndValue() {
        String s = new String(Character.toChars(135361));
        Attribute attr = new Attribute(s, "A" + s + "B");
        assertEquals(s + "=\"A" + s + "B\"", attr.html());
        assertEquals(attr.html(), attr.toString());
    }

    @Test public void validatesKeysNotEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Attribute(" ", "Check"));
    }

    @Test public void validatesKeysNotEmptyViaSet() {
        assertThrows(IllegalArgumentException.class, () -> {
            Attribute attr = new Attribute("One", "Check");
            attr.setKey(" ");
        });
    }

    @Test public void booleanAttributesAreEmptyStringValues() {
        Document doc = Jsoup.parse("<div hidden>");
        Attributes attributes = doc.body().child(0).attributes();
        assertEquals("", attributes.get("hidden"));

        Attribute first = attributes.iterator().next();
        assertEquals("hidden", first.getKey());
        assertEquals("", first.getValue());
        assertFalse(first.hasDeclaredValue());
        assertTrue(Attribute.isBooleanAttribute(first.getKey()));
    }

    @Test public void settersOnOrphanAttribute() {
        Attribute attr = new Attribute("one", "two");
        attr.setKey("three");
        String oldVal = attr.setValue("four");
        assertEquals("two", oldVal);
        assertEquals("three", attr.getKey());
        assertEquals("four", attr.getValue());
        assertNull(attr.parent);
    }

    @Test public void hasValue() {
        Attribute a1 = new Attribute("one", "");
        Attribute a2 = new Attribute("two", null);
        Attribute a3 = new Attribute("thr", "thr");

        assertTrue(a1.hasDeclaredValue());
        assertFalse(a2.hasDeclaredValue());
        assertTrue(a3.hasDeclaredValue());
    }

    @Test public void canSetValueToNull() {
        Attribute attr = new Attribute("one", "val");
        String oldVal = attr.setValue(null);
        assertEquals("one", attr.html());
        assertEquals("val", oldVal);

        oldVal = attr.setValue("foo");
        assertEquals("", oldVal); // string, not null
    }

    @Test void booleanAttributesAreNotCaseSensitive() {
        // https://github.com/jhy/jsoup/issues/1656
        assertTrue(Attribute.isBooleanAttribute("required"));
        assertTrue(Attribute.isBooleanAttribute("REQUIRED"));
        assertTrue(Attribute.isBooleanAttribute("rEQUIREd"));
        assertFalse(Attribute.isBooleanAttribute("random string"));

        String html = "<a href=autofocus REQUIRED>One</a>";
        Document doc = Jsoup.parse(html);
        assertEquals("<a href=\"autofocus\" required>One</a>", doc.selectFirst("a").outerHtml());

        Document doc2 = Jsoup.parse(html, Parser.htmlParser().settings(ParseSettings.preserveCase));
        assertEquals("<a href=\"autofocus\" REQUIRED>One</a>", doc2.selectFirst("a").outerHtml());
    }

    @ParameterizedTest
    /**
     * Teste la fonction getValidKey ainsi que tous les cas des deux sous fonctions qu'elle appelle:
     * isValidXmlKey et isValidHtmlKey.
     * Celles-ci permettent de vérifier la validité d'une clé XML ou HTML.
     * Le test permet également de révéler ce qui je pense être une erreur dans le code, le charactère 174,
     * qui correspond au symbole trademark est accepté comme une valeur valide de clé HTML. J'explique plus le problème
     * dans le rapport.
     * @author Corélie Godefroid
     */
    @MethodSource("keyValidityGenerator")
    public void testGetValidKey(String input, String expected, Document.OutputSettings.Syntax syntax){
        String actual=Attribute.getValidKey(input,syntax);
        assertEquals(expected,actual);

    }

    /**
    * génère les données pour le test testGetValidKey
     * @author Corélie Godefroid
     */
    private static Stream<Arguments> keyValidityGenerator() {
        return Stream.of(
                Arguments.of("!test","_test",Document.OutputSettings.Syntax.xml),
                Arguments.of("te!st","te_st",Document.OutputSettings.Syntax.xml),
                Arguments.of("test","test",Document.OutputSettings.Syntax.xml),
                Arguments.of("",null,Document.OutputSettings.Syntax.xml),
                Arguments.of("TEST","TEST",Document.OutputSettings.Syntax.xml),
                Arguments.of(":test",":test",Document.OutputSettings.Syntax.xml),
                Arguments.of("_test","_test",Document.OutputSettings.Syntax.xml),
                Arguments.of("a_test","a_test",Document.OutputSettings.Syntax.xml),
                Arguments.of("z_test","z_test",Document.OutputSettings.Syntax.xml),
                Arguments.of("A_test","A_test",Document.OutputSettings.Syntax.xml),
                Arguments.of("Z_test","Z_test",Document.OutputSettings.Syntax.xml),
                Arguments.of("1test",null,Document.OutputSettings.Syntax.xml),
                Arguments.of("",null,Document.OutputSettings.Syntax.html),
                Arguments.of(new String(new char[] { 31 }), "_", Document.OutputSettings.Syntax.html) ,
                Arguments.of(new String(new char[] { 127 }), "_", Document.OutputSettings.Syntax.html),
                Arguments.of(new String(new char[] { 159 }), "_", Document.OutputSettings.Syntax.html),
                Arguments.of(" ", "_", Document.OutputSettings.Syntax.html),
                Arguments.of("\"", "_", Document.OutputSettings.Syntax.html),
                Arguments.of("\'", "_", Document.OutputSettings.Syntax.html),
                Arguments.of("/", "_", Document.OutputSettings.Syntax.html),
                Arguments.of("=", "_", Document.OutputSettings.Syntax.html),
                Arguments.of("A", "A", Document.OutputSettings.Syntax.html),
                Arguments.of(new String(new char[] { 174}), new String(new char[] { 174}), Document.OutputSettings.Syntax.html)
        );
    }

    @ParameterizedTest
    /**
     * Teste la méthode equals de Attribute.
     * Prends en entrée main l'Attribute qui va appeler la méthode equals avec en entrée le deuxième argument
     * Object compared. Expected est la valeur attendue de cette égalité, c'est un Boolean.
     *
     * Le test applique la fonction equals sur main et compared et vérifie le resultat de cette égalité.
     *  @author Corélie Godefroid
     */
    @MethodSource("equalsGenerator")
    public void testAttributeEquals(Attribute main, Object compared, Boolean expected){

        Boolean actual= main.equals(compared);
        assertEquals(expected,actual);
    }

    /**
     * génère les données pour le test testAttributeEquals, utilisa la bibliothèque java-faker pour créer des données
     * aléatoires.
     * @author Corélie Godefroid
     */
    private static Stream<Arguments> equalsGenerator() {
        Attribute a = new Attribute("test", null);
        Faker faker = new Faker();
        String key = faker.regexify("[a-z]{1,10}");
        String value =  faker.regexify("[a-z]{1,10}");
        return Stream.of(
                Arguments.of(a, a, true), //même element
                Arguments.of(a, new Element("p"), false), //différente classe
                Arguments.of(a, new Attribute("abc", null), false), //différente clé
                Arguments.of(a, new Attribute("test", "val"), false), //différente valeur
                Arguments.of(a, new Attribute("abc", "val"), false), // tout différent
                Arguments.of(a, new Attribute("test", null), true), //identique
                Arguments.of(a, null, false),//null
                Arguments.of( new Attribute(key,value), new Attribute(key,value),true) //généré aléatoirement mais égaux

        );
    }
    @Test
    /**
     * Teste la fonction clone de la classe Attribute. Génère un objet Attribute avec une clé et une valeur aléatoires
     * grâce à la bibliothèque java-faker. Clone cet objet Attribute puis compare le clone et l'objet original.
     * @author Corélie Godefroid
     */
    public void testAttributeClone(){
        Faker faker = new Faker();
        String key = faker.regexify("[a-z]{1,10}");
        String value =  faker.regexify("[a-z]{1,10}");
        Attribute a = new Attribute(key,value);

        Attribute clone_a= a.clone();

        assertTrue(a.equals(clone_a));


    }

}
