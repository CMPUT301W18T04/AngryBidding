package ca.ualberta.angrybidding;

/**
 * Created by SarahS on 2018/03/27.
 */

//Copy from Entry.java

import java.util.ArrayList;
import java.util.HashMap;

import ca.ualberta.angrybidding.User;

//Notes: - public abstract class NewNotificationConnection implements ServiceConnection

public class Notification {
    private User user;
    private String notificationType;
    //private ArrayList<Parameter> parameters;

    private HashMap<String, String> parameters;

    public Notification(User user, String notificationType, HashMap<String, String> parameters){
        this.user = user;
        this.notificationType = notificationType;
        this.parameters = parameters;
    }

    public User getUser(){
        return user;
    }

    public void setNotificationType(String notificationType){
        this.notificationType = notificationType;
    }

    public String getNotificationType(){
        return this.notificationType;
    }

    public HashMap<String, String> getParameterMap() {
        return parameters;
    }


    /*public ArrayList<Parameter> getParameters(){
        return parameters;
    }*/

    /*public HashMap<String, String> getParameterMap(){
        if(parameterMap == null){
            parameterMap = new HashMap<>();
            for(Parameter par : parameters){
                parameterMap.put(par.getParameterName(), par.getValue());
            }
        }
        return parameterMap;
    }*/
}