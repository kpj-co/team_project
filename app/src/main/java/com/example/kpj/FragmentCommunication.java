package com.example.kpj;

import com.parse.ParseObject;

public interface FragmentCommunication {
    void sendParseObject(String key, ParseObject object);
}
