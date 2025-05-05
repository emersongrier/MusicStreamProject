package com.BayWave;

public class AlbumData
{
    public int alb_id;
    public String alb_type;
    public String alb_name;
    public String alb_cover;
    public int alb_tracks;
    public int alb_likes;
    public Boolean alb_private;
    public int art_id;
    public AlbumData(
            int alb_id,
            String alb_type,
            String alb_name,
            String alb_cover,
            int alb_tracks,
            int alb_likes,
            Boolean alb_private,
            int art_id)
    {
        this.alb_id = alb_id;
        this.alb_type = alb_type;
        this.alb_name = alb_name;
        this.alb_cover = alb_cover;
        this.alb_tracks = alb_tracks;
        this.alb_likes = alb_likes;
        this.alb_private = alb_private;
        this.art_id = art_id;
    }
}
