package com.example.astro;

public class AstroItem {
    private String astroEvent;
    private String astroTime;
    private String astroTitle;
    private String locdate;

    public AstroItem(String astroEvent, String astroTime, String astroTitle, String locdate) {
        this.astroEvent = astroEvent;
        this.astroTime = astroTime;
        this.astroTitle = astroTitle;
        this.locdate = locdate;
    }

    public AstroItem() {
    }

    public String getAstroEvent() {
        return astroEvent;
    }

    public void setAstroEvent(String astroEvent) {
        this.astroEvent = astroEvent;
    }

    public String getAstroTime() {
        return astroTime;
    }

    public void setAstroTime(String astroTime) {
        this.astroTime = astroTime;
    }

    public String getAstroTitle() {
        return astroTitle;
    }

    public void setAstroTitle(String astroTitle) {
        this.astroTitle = astroTitle;
    }

    public String getLocdate() {
        return locdate;
    }

    public void setLocdate(String locdate) {
        this.locdate = locdate;
    }
}
