package com.lightemittingsmew.redditreader;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Created by smw on 8/30/14.
 */
public class RedditTagHandler implements Html.TagHandler {
    boolean openingTag=  true;
    boolean firstElement = true;
    String parent=null;
    int index=1;

    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {

        if(tag.equals("ul")) parent="ul";
        else if(tag.equals("ol")) parent="ol";
        if(tag.equals("li")){
            if(firstElement){
                firstElement = false;
            } else {
                output.append("\n");
            }

            if(parent.equals("ul")){
                if(openingTag){
                    output.append("\t•  ");
                    openingTag= false;
                }else{
                    openingTag = true;
                }
            }
            else{
                if(openingTag){
                    output.append("\t"+index+".  ");
                    openingTag= false;
                    index++;
                }else{
                    openingTag = true;
                }
            }
        }
    }
}
