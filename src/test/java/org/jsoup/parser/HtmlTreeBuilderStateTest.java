package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.parser.HtmlTreeBuilderState.Constants;
import org.junit.jupiter.api.Test;

import org.jsoup.parser.Parser;
import org.jsoup.parser.HtmlTreeBuilder;
import org.jsoup.parser.Token;
import org.jsoup.parser.HtmlTreeBuilderState;
import java.io.StringReader;
import java.io.Reader;
import java.lang.String;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;



import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.jsoup.parser.HtmlTreeBuilderState.Constants.InBodyStartInputAttribs;
import static org.junit.jupiter.api.Assertions.*;

public class HtmlTreeBuilderStateTest {
    static List<Object[]> findConstantArrays(Class aClass) {
        ArrayList<Object[]> array = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && field.getType().isArray()) {
                try {
                    array.add((Object[]) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return array;
    }

    static void ensureSorted(List<Object[]> constants) {
        for (Object[] array : constants) {
            Object[] copy = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            assertArrayEquals(array, copy);
        }
    }

    @Test
    public void ensureArraysAreSorted() {
        List<Object[]> constants = findConstantArrays(Constants.class);
        ensureSorted(constants);
        assertEquals(40, constants.size());
    }

    @Test public void ensureTagSearchesAreKnownTags() {
        List<Object[]> constants = findConstantArrays(Constants.class);
        for (Object[] constant : constants) {
            String[] tagNames = (String[]) constant;
            for (String tagName : tagNames) {
                if (StringUtil.inSorted(tagName, InBodyStartInputAttribs))
                    continue; // odd one out in the constant
                assertTrue(Tag.isKnownTag(tagName), String.format("Unknown tag name: %s", tagName));
            }
        }
    }


    @Test
    public void nestedAnchorElements01() {
        String html = "<html>\n" +
            "  <body>\n" +
            "    <a href='#1'>\n" +
            "        <div>\n" +
            "          <a href='#2'>child</a>\n" +
            "        </div>\n" +
            "    </a>\n" +
            "  </body>\n" +
            "</html>";
        String s = Jsoup.parse(html).toString();
        assertEquals("<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "  <a href=\"#1\"> </a>\n" +
            "  <div>\n" +
            "   <a href=\"#1\"> </a><a href=\"#2\">child</a>\n" +
            "  </div>\n" +
            " </body>\n" +
            "</html>", s);
    }

    @Test
    public void nestedAnchorElements02() {
        String html = "<html>\n" +
            "  <body>\n" +
            "    <a href='#1'>\n" +
            "      <div>\n" +
            "        <div>\n" +
            "          <a href='#2'>child</a>\n" +
            "        </div>\n" +
            "      </div>\n" +
            "    </a>\n" +
            "  </body>\n" +
            "</html>";
        String s = Jsoup.parse(html).toString();
        assertEquals("<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "  <a href=\"#1\"> </a>\n" +
            "  <div>\n" +
            "   <a href=\"#1\"> </a>\n" +
            "   <div>\n" +
            "    <a href=\"#1\"> </a><a href=\"#2\">child</a>\n" +
            "   </div>\n" +
            "  </div>\n" +
            " </body>\n" +
            "</html>", s);
    }


    /**
     * This test verifies the behavior of the HtmlTreeBuilderState.BeforeHead state. 
     * It checks how different token types, such as whitespace, comments, and tags, 
     * are processed in the BeforeHead state.
     * @author Mariel Leano
     */
    @Test
    public void testBeforeHeadStateProcess() {
        HtmlTreeBuilder treeBuilder = new HtmlTreeBuilder();
        StringReader reader = new StringReader("");
        Parser parser = Parser.htmlParser();
        treeBuilder.initialiseParse(reader, "http://example.com", parser);

        Token.Character whitespaceToken = new Token.Character();
        whitespaceToken.data(" ");
        assertTrue(HtmlTreeBuilderState.BeforeHead.process(whitespaceToken, treeBuilder));

        Token.Comment commentToken = new Token.Comment();
        commentToken.append("This is a comment");
        assertTrue(HtmlTreeBuilderState.BeforeHead.process(commentToken, treeBuilder));

        Token.Doctype doctypeToken = new Token.Doctype();
        assertFalse(HtmlTreeBuilderState.BeforeHead.process(doctypeToken, treeBuilder));

        Token.StartTag htmlStartTag = new Token.StartTag(treeBuilder);
        htmlStartTag.name("html");
        assertTrue(HtmlTreeBuilderState.BeforeHead.process(htmlStartTag, treeBuilder));

        Token.StartTag headStartTag = new Token.StartTag(treeBuilder);
        headStartTag.name("head");
        assertTrue(HtmlTreeBuilderState.BeforeHead.process(headStartTag, treeBuilder));

        Token.EndTag headEndTag = new Token.EndTag(treeBuilder);
        headEndTag.name("head");
        assertTrue(HtmlTreeBuilderState.BeforeHead.process(headEndTag, treeBuilder));

        Token.EndTag bodyEndTag = new Token.EndTag(treeBuilder);
        bodyEndTag.name("body");
        assertTrue(HtmlTreeBuilderState.BeforeHead.process(bodyEndTag, treeBuilder));
    }

    /**
     * This test ensures that the <nobr> tag is properly handled in the InBody state.
     * It checks if the <nobr> tag is processed correctly and added to the active 
     * formatting elements stack.
     * @author Mariel Leano
     */
    @Test
    public void testProcessNobrTagInTreeBuilder() {
        HtmlTreeBuilder tb = new HtmlTreeBuilder();
        StringReader reader = new StringReader("<nobr>");
        Parser parser = new Parser(tb);
        tb.initialiseParse(reader, "", parser);

        Token.StartTag htmlTag = new Token.StartTag(tb).nameAttr("html", new Attributes());
        tb.process(htmlTag, HtmlTreeBuilderState.InBody);
        Token.StartTag bodyTag = new Token.StartTag(tb).nameAttr("body", new Attributes());
        tb.process(bodyTag, HtmlTreeBuilderState.InBody);
        Token.StartTag startTag = new Token.StartTag(tb).nameAttr("nobr", new Attributes());

        boolean result = tb.process(startTag, HtmlTreeBuilderState.InBody);

        assertTrue(result);
        assertTrue(tb.getActiveFormattingElement("nobr") != null);
    }

    /**
     * This test checks how the HtmlTreeBuilder handles the <applet> tag in the InBody state.
     * It verifies if the <applet> tag is correctly pushed to and popped from the stack.
     * @author Mariel Leano
     */
    @Test
    public void testInBodyStartApplets() {
        HtmlTreeBuilder tb = new HtmlTreeBuilder();
        Parser parser = new Parser(tb);

        tb.initialiseParse(new StringReader(""), "", parser);
        Token.StartTag startTag = new Token.StartTag(tb);
        startTag.nameAttr("applet", new Attributes());
        tb.process(startTag);

        assertTrue(tb.onStack("applet"), "Should contain applet in the stack");
        Token.EndTag endTag = new Token.EndTag(tb);
        endTag.name("applet");
        boolean result = tb.process(endTag);

        assertTrue(result, "Should have processed applet end tag successfully");
        assertFalse(tb.onStack("applet"), "Should not contain applet in the stack after closing");
    }

    /**
     * This test ensures that the <caption> end tag is not processed when the parser 
     * is inside a <caption> element. It checks for proper handling in the InCaption state.
     * @author Mariel Leano
     */
    @Test
    void testProcessEndTagCaptionInCaption() {
        HtmlTreeBuilder tb = new HtmlTreeBuilder();
        Parser parser = new Parser(tb);
        tb.initialiseParse(new StringReader(""), "", parser);

        tb.transition(HtmlTreeBuilderState.InCaption);

        Token.EndTag endTag = new Token.EndTag(tb);
        endTag.name("caption");

        boolean result = tb.process(endTag);

        assertFalse(result, "Processing of the 'caption' end tag should not be successful");
    }

    /**
     * This test verifies that the </colgroup> end tag is correctly processed in the InColumnGroup state.
     * It ensures that the HtmlTreeBuilder processes the end tag of the colgroup element properly.
     * @author Mariel Leano
     */
    @Test
    public void testProcessEndTagColgroup() {
        HtmlTreeBuilder tb = new HtmlTreeBuilder();
        Parser parser = new Parser(tb);

        tb.initialiseParse(new StringReader("<table><colgroup></colgroup></table>"), "", parser);

        Token.StartTag tableStartTag = new Token.StartTag(tb);
        tableStartTag.name("table");
        tb.process(tableStartTag);

        Token.StartTag colgroupStartTag = new Token.StartTag(tb);
        colgroupStartTag.name("colgroup");
        tb.process(colgroupStartTag);

        Token.EndTag colgroupEndTag = new Token.EndTag(tb);
        colgroupEndTag.name("colgroup");

        boolean result = HtmlTreeBuilderState.InColumnGroup.process(colgroupEndTag, tb);

        assertTrue(result, "Processing of 'colgroup' end tag should succeed.");
    }

    




    
}