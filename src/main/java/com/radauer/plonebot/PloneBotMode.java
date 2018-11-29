package com.radauer.plonebot;

public enum PloneBotMode
{

    QA_COPY(false, true),
    QY_DELETE(false, false),
    PROD_COPY(true, true),
    PROD_DELETE(true, false);

    public boolean prod;
    public boolean copy;

    PloneBotMode(boolean prod, boolean copy)
    {
        this.prod = prod;
        this.copy = copy;
    }

}
