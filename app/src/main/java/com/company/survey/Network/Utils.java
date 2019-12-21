package com.company.survey.Network;

public class Utils
{
    private Utils(){}

    public static final String BaseUrl="http://127.0.0.1:5000/";

    public static ClientAPI getClientAPI()
    {
        return RetrofitClient.getClient(BaseUrl).create(ClientAPI.class);
    }
}