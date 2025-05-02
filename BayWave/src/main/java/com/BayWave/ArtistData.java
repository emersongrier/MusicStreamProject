package com.BayWave;

public class ArtistData
{
    public int art_id;
    public String art_name;
    public String art_bio;
    public int art_flwrs;
    public int art_mbrs;

    public ArtistData(
            int art_id,
            String art_name,
            String art_bio,
            int art_flwrs,
            int art_mbrs)
    {
        this.art_id = art_id;
        this.art_name = art_name;
        this.art_bio = art_bio;
        this.art_flwrs = art_flwrs;
        this.art_mbrs = art_mbrs;
    }
}
