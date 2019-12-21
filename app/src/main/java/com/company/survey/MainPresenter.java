package com.company.survey;

import com.company.survey.Network.ClientAPI;
import com.company.survey.Network.Utils;

public class MainPresenter implements MainContract.presenter
{
    ClientAPI clientAPI = Utils.getClientAPI();
    MainContract.view mpview;

    public MainPresenter(MainContract.view mpview) {
        this.mpview = mpview;
    }
}
