package com.BayWave;



public class TrackData
{
    public int trk_id;
    public String trk_name;
    public String trk_file;
    public int trk_pos;
    public String trk_lyrics;
    public String trk_len; //may need to convert to a time type object
    public int trk_strms;
    public int trk_likes;
    public int alb_id;

    public TrackData(
            int trk_id,
            String trk_name,
            String trk_file,
            int trk_pos,
            String trk_lyrics,
            String trk_len,
            int trk_strms,
            int trk_likes,
            int alb_id)
    {
        this.trk_id = trk_id;
        this.trk_name = trk_name;
        this.trk_file = trk_file;
        this.trk_pos = trk_pos;
        this.trk_lyrics = trk_lyrics;
        this.trk_len = trk_len;
        this.trk_strms = trk_strms;
        this.trk_likes = trk_likes;
        this.alb_id = alb_id;
    }
}
