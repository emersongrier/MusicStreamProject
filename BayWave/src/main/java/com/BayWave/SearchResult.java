package com.BayWave;

public class SearchResult {
    public int art_id;
    public String art_name;

    public int alb_id;
    public String alb_name;

    public int trk_id;
    public String trk_name;
    public String trk_file;

    // Optional constructor
    public SearchResult(int art_id, String art_name,
                        int alb_id, String alb_name,
                        int trk_id, String trk_name, String trk_file) {
        this.art_id = art_id;
        this.art_name = art_name;
        this.alb_id = alb_id;
        this.alb_name = alb_name;
        this.trk_id = trk_id;
        this.trk_name = trk_name;
        this.trk_file = trk_file;
    }
}
