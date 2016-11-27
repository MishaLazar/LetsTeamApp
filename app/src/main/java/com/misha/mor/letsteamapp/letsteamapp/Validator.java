package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by mor on 11/4/2016.
 */

public class Validator {
    public Validator() {
    }

    public Boolean isValidEmail(String strEmailAddress){
        if((strEmailAddress.matches(".*@.*\\..*")) && (strEmailAddress != null)){
            return true;
        }
        return false;
    }

    public Boolean isValidName(String strName){
        if((strName != null) && (strName.length() > 1)){
            return true;
        }
        return false;
    }


    public Boolean isValidPassword(String strPassword){
        if((strPassword != null) && (strPassword.length() > 5)){
            return true;
        }
        return false;
    }

    public Boolean isValidDisplayName(String eventName){
        if(eventName != null && eventName.length() > 0){
            return true;
        }
        return false;
    }

    public Boolean isValidAddress(String street, String houseNum, String city){
        if((street != null && street.length() > 0) &&
                (houseNum != null && houseNum.length() > 0) &&
                (city != null && city.length() > 0)){
            return true;
        }
        return false;
    }

}
