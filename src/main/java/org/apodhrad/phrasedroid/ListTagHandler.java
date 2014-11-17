package org.apodhrad.phrasedroid;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Html.TagHandler;


public class ListTagHandler implements TagHandler {
    boolean first = true;
    
    public static final String TAB = " ";

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equals("li")) {
            char lastChar = 0;
            if (output.length() > 0)
                lastChar = output.charAt(output.length() - 1);
            if (first) {
                if (lastChar == '\n')
                    output.append(TAB + "• ");
                else
                    output.append("\n" + TAB + "• ");
                first = false;
            } else {
                first = true;
            }
        }
    }
}