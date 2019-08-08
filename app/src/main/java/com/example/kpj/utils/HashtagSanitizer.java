package com.example.kpj.utils;

import java.util.ArrayList;
import java.util.List;

public class HashtagSanitizer {
    private List<String> previousHashtags;

    public HashtagSanitizer() {

    }

    //Returns a list of all the Hash Tags given by the user
    public List<String> returnHashtags(String usernInput) {
        //Boolean to know if there is something in the current sequence of characters to be analyzed
        boolean hasContent;
        //Variable to store our "base point", or the # symbol that will start the hashtag
        int basePoint;
        //List that has the hashtags to be returned
        List<String> hashtagsList;
        //Initializing the boolean
        hasContent = false;
        //Initializing the basePoint
        basePoint = 0;
        //Initializing the list
        hashtagsList = new ArrayList<>();
        String container;
        for(int i = 0; i < usernInput.length(); i++) {
            //If it is a #, and we do not have a possible hashtag, change the base point
            if(usernInput.charAt(i) == '#') {
                basePoint = i;
                hasContent = true;
            }
            //If there is a space, it could be a hashtag if it has content
            else if(usernInput.charAt(i) == ' ' && hasContent) {
                container = usernInput.substring(basePoint + 1, i);
                if(!container.equals("#")) {
                    hashtagsList.add(container);
                }
                hasContent = false;
            }
            //The case is slightly different when we are dealing with the last character of the string
            else if(i == usernInput.length() - 1 && hasContent) {
                container = usernInput.substring(basePoint + 1, i + 1);
                if(!container.equals("")) {
                    hashtagsList.add(container);
                }
                hasContent = false;
            }
        }
        previousHashtags = hashtagsList;
        //returning the object
        return hashtagsList;
    }

    public List<String> getPreviousHashtags() {
        return previousHashtags;
    }
}
