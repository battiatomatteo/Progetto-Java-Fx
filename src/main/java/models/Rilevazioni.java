package models;

public class Rilevazioni {

    private String date;
    private float rilevazionePrePasto;
    private float rilevazionePostPasto;

    public Rilevazioni(String date, float rilevazionePrePasto, float rilevazionePostPasto) {
        this.date = date;
        this.rilevazionePrePasto = rilevazionePrePasto;
        this.rilevazionePostPasto = rilevazionePostPasto;
    }

    public String getDate() {
        return date;
    }

    public float getRilevazionePrePasto() {
        return rilevazionePrePasto;
    }

    public float getRilevazionePostPasto() {
        return rilevazionePostPasto;
    }
}
