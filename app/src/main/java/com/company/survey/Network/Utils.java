package com.company.survey.Network;

public class Utils
{
    private Utils(){}

    public static final String BaseUrl="http://139.59.92.206:8000/";

    public static ClientAPI getClientAPI()
    {
        return RetrofitClient.getClient(BaseUrl).create(ClientAPI.class);
    }
}