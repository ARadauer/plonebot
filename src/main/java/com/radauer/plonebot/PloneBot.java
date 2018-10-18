package com.radauer.plonebot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PloneBot
{
    private static String baseUrl = "https://nwi-cms-qa.ext.ocp.porscheinformatik.cloud/Plone/";
    private static String FEATURE_TO_COPY = "Feature Toggles"; // "carconfigurator";

    private static String[] TOOLS_TO_COPY_TO = {"carconfigurator"}; //{null};
    private static String TOOL_TO_COPY_FROM =  TOOLS_TO_COPY_TO[0];
    private static String COUNTRY_TO_COPY_FROM = "at";
    private static String BRAND_TO_COPY_FROM = "vw";

    private static boolean INSERT = true;
    private static boolean DELETE = !INSERT;

    private static String[] allCountries =
        {"at", "ba", "bg", "cl", "co", "common", "cz", "hr", "hu", "mk", "my", "ro", "rs", "si", "sk", "ua"};
    private static String[] allBrands = {"vw", "audi", "lnf", "seat", "skoda"};
    private static WebDriver driver;

    FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver)
        .withTimeout(2, TimeUnit.SECONDS)
        .pollingEvery(50, TimeUnit.MILLISECONDS)
        .ignoring(NoSuchElementException.class);

    public static void main(String[] args)
    {

        driver = new ChromeDriver();
        driver.get("https://qa.auto-partner.net/portal/at/menu");

        logIn();

        //initCountriesAndBrands();

        if (INSERT)
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
                    System.out.println("Do " + country + " " + brand + " " + tool);
                   if (country.equals(COUNTRY_TO_COPY_FROM) && brand.equals(BRAND_TO_COPY_FROM) && tool.equals(
                        TOOL_TO_COPY_FROM))
                    {
                        System.out.println(" Skip");
                        continue;
                    }
                    navigateTo(country, brand, tool);
                    if (driver.findElement(By.className("documentFirstHeading"))
                        .getText()
                        .contains("does not seem to exist"))
                    {
                        continue;
                    }

                        boolean found = findFeature(FEATURE_TO_COPY) != null;

                    if (INSERT)
                    {
                        if (!found)
                        {
                            driver.findElement(By.id("btn-paste")).click();
                            sleep();

                            System.out.println(" Inserted");
                            (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>()
                            {
                                public Boolean apply(WebDriver d)
                                {
                                    return selectFeature(FEATURE_TO_COPY);
                                }
                            });
                            waitAndClick(By.id("btn-workflow"));
                            waitAndClick(By.className("applyBtn"));
                            System.out.println(" published");
                        }
                        else
                        {
                            System.out.println(" Already exists");
                        }
                    }
                    if (DELETE)
                    {
                        if (found)
                        {
                            driver.findElement(By.id("btn-delete")).click();
                            driver.findElement(By.className("applyBtn")).click();
                            System.out.println(" Deleted");
                        }
                        else
                        {
                            System.out.println(" Did not exist");
                        }
                    }

                }
            }
        }

        System.out.println("Page title is: " + driver.getTitle());

        // driver.quit();
    }

    private static void waitAndClick(By by)
    {
        (new WebDriverWait(driver, 5))
            .until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    private static void sleep()
    {
        try
        {
            Thread.sleep(250);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void copyFeature()
    {
        navigateTo(COUNTRY_TO_COPY_FROM, BRAND_TO_COPY_FROM, TOOL_TO_COPY_FROM);
        selectFeature(FEATURE_TO_COPY);
        driver.findElement(By.id("btn-copy")).click();
        sleep();
    }

    private static boolean selectFeature(String featureToSelect)
    {

        WebElement feature = findFeature(featureToSelect);
        if (feature != null)
        {
            WebElement tableRow = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].parentNode.parentNode.parentNode;", feature);

            WebElement firstCol = tableRow.findElement(By.className("selection"));
            WebElement checkbox = firstCol.findElement(By.tagName("input"));
            checkbox.click();

            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        }
        return false;

    }

    private static WebElement findFeature(String featureToSelect)
    {
        try
        {
            List<WebElement> features = driver.findElements(By.className("manage"));

            for (WebElement feature : features)
            {

                if (feature.getText().equals(featureToSelect))
                {
                    return feature;

                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }

   /* private static void initCountriesAndBrands()
    {
        navigateTo();
        List<WebElement> countries = driver.findElements(By.className("contenttype-country"));
        for (WebElement country : countries)
        {
            System.out.println("Country: " + country.getText());
            allCountries.add(country.getText());
        }

        navigateTo(COUNTRY_TO_COPY_FROM);
        List<WebElement> brands = driver.findElements(By.className("contenttype-brand"));

        for (WebElement brand : brands)
        {
            System.out.println("Brand: " + brand.getText());
            allBrands.add(brand.getText());
        }

        navigateTo("at", "vw");

        List<WebElement> tools = driver.findElements(By.className("contenttype-tool"));

        for (WebElement tool : tools)
        {
            System.out.println("Tool: " + tool.getText());
        }
    }*/

    private static void logIn()
    {
        JOptionPane.showMessageDialog(null, "Eingeloggt und Plone gestartet?");

        System.out.println("Logged in");
        for (String handle : driver.getWindowHandles())
        {
            System.out.println("Windows " + handle);
            driver.switchTo().window(handle);
            System.out.println("Page title is: " + driver.getTitle());
            if (driver.getTitle().contains("Plone"))
            {
                break;
            }

        }
    }

    private static String getFolderUrl(String... path)
    {
        String url = baseUrl;
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
        driver.navigate().to(url);
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}