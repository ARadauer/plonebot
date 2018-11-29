package com.radauer.plonebot;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public class PloneBot
{

    private static String FEATURE_TO_COPY = "WLTP Settings";
    private static PloneBotMode MODE = PloneBotMode.QA_COPY;
    private static String[] TOOLS_TO_COPY_TO = {"carconfigurator"}; //{null};

    private static String TOOL_TO_COPY_FROM = TOOLS_TO_COPY_TO[0];
    private static String COUNTRY_TO_COPY_FROM = "at";
    private static String BRAND_TO_COPY_FROM = "vw";
    private static String[] allCountries =
        {"at", "ba", "bg", "cl", "co", "common", "cz", "hr", "hu", "mk", "my", "ro", "rs", "si", "sk", "ua"};
    private static String[] allBrands = {"vw", "audi", "lnf", "seat", "skoda"};
    private static String baseUrlQa = "https://nwi-cms-qa.ext.ocp.porscheinformatik.cloud/Plone/";
    private static String baseUrlProd = "https://nwi-cms.ext.ocp.porscheinformatik.cloud/Plone/";
    private static String pnetUrlQa = "https://qa.auto-partner.net/portal/at/menu";
    private static String pnetUrlProd = "https://qa.auto-partner.net/portal/at/menu";

    private static PlonePage plonePage;

    public static void main(String[] args)
    {
        plonePage = new PlonePage(new ChromeDriver(), MODE.prod ? pnetUrlProd : pnetUrlQa);

        plonePage.logIn();

        if (MODE.copy)
        {

            copyFeature();

        }

        for (String country : allCountries)
        {
            if (StringUtils.isEmpty(country))
            {
                continue;
            }
            for (String brand : allBrands)
            {
                if (StringUtils.isEmpty(brand))
                {
                    continue;
                }

                for (String tool : TOOLS_TO_COPY_TO)
                {
                    doTask(country, brand, tool);
                }


            }
        }
        System.out.println("Fertig");

        // driver.quit();
    }

    private static void doTask(String country, String brand, String tool)
    {
        try
        {
            System.out.println("Do " + country + " " + brand + " " + tool);
            if (country.equals(COUNTRY_TO_COPY_FROM) && brand.equals(BRAND_TO_COPY_FROM) && tool.equals(
                TOOL_TO_COPY_FROM))
            {
                System.out.println(" Skip");
                return;
            }
            navigateTo(country, brand, tool);
            if (plonePage.siteDoesNotExist())
            {
                return;
            }

            boolean found = plonePage.findFeature(FEATURE_TO_COPY) != null;

            if (MODE.copy)
            {

                if (!found)
                {
                    insertFeature();
                }
                else
                {
                    System.out.println(" Already exists");
                }
            }
            if (!MODE.copy) //delete
            {
                if (found)
                {
                    deleteFeature();
                }
                else
                {
                    System.out.println(" Did not exist");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Fehler");
        }

    }

    private static void deleteFeature()
    {
        plonePage.selectFeature(FEATURE_TO_COPY);
        plonePage.delete();
        System.out.println(" Deleted");
    }

    private static void insertFeature()
    {
        plonePage.pasteAndSelect(FEATURE_TO_COPY);
        System.out.println(" Inserted");
        plonePage.publish();
        System.out.println(" published");

    }

    private static void copyFeature()
    {
        navigateTo(COUNTRY_TO_COPY_FROM, BRAND_TO_COPY_FROM, TOOL_TO_COPY_FROM);
        plonePage.selectFeature(FEATURE_TO_COPY);
        plonePage.copy();
    }

    private static String getFolderUrl(String... path)
    {
        String url = MODE.prod ? baseUrlProd : baseUrlQa;
        for (String p : path)
        {
            if (p == null)
            {
                continue;
            }
            url += p + "/";
        }
        url += "folder_contents";
        return url;
    }

    private static void navigateTo(String... paths)
    {
        String url = getFolderUrl(paths);
        System.out.println("Navigate to " + url);
        plonePage.goToUrl(url);
    }
}