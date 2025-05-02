package com.BayWave;



public class UserData
{
    public int usr_id;
    public String usr_name;
    public String usr_pass;
    public String usr_email;
    public String usr_phone;
    public int usr_friends;

    public UserData(
            int usr_id,
            String usr_name,
            String usr_pass,
            String usr_email,
            String usr_phone,
            int usr_friends)
    {
        this.usr_id = usr_id;
        this.usr_name = usr_name;
        this.usr_pass = usr_pass;
        this.usr_email = usr_email;
        this.usr_phone = usr_phone;
        this.usr_friends = usr_friends;
    }
}
