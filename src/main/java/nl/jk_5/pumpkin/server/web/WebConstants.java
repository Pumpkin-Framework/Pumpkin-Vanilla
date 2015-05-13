package nl.jk_5.pumpkin.server.web;

import java.net.MalformedURLException;
import java.net.URL;

public class WebConstants {

    public static final URL NEW_ACCOUNT_URL;

    static {
        try{
            NEW_ACCOUNT_URL = new URL("https://pumpkin.jk-5.nl/#/account/new");
        }catch(MalformedURLException e){
            throw new RuntimeException(e);
        }
    }
}
