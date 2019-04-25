package com.radauer.plonebot;

public enum PloneBotMode
{

    QA_COPY(false, true, false),
    QY_DELETE(false, false, false),
    PROD_COPY(true, true, false),
    PROD_DELETE(true, false, false),

    PROD_CHECK_EXISTANCE(true, false, true),
    QA_CHECK_EXISTANCE(false, false, true);

    public boolean prod;
    public boolean copy;
    public boolean checkExistence;

    PloneBotMode(boolean prod, boolean copy, boolean checkExistence)
    {
        this.prod = prod;
        this.copy = copy;
        this.checkExistence = checkExistence;
    }

}
