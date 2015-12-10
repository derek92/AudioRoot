package app.audioroot;

/**
 * Created by Derek on 11/7/2015.
 */
public class AppNode {

    String appName;
    boolean act;

    public AppNode(String s)
    {
        this.appName = s;
        this.act = false;
    }
    public AppNode(String s, boolean b)
    {
        this.appName = s;
        this.act = b;
    }


    public String getAppName() {return this.appName;}
    public void setAppName(String s) {this.appName = s;}
    public boolean isActivated(){return this.act;}
    public void Activate(boolean b){ this.act = b;}

}
