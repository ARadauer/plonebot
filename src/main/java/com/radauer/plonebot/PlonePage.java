package com.radauer.plonebot;

import java.util.List;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PlonePage
{

    private WebDriver driver;

    public PlonePage(WebDriver driver, String url)
    {
        this.driver = driver;
        this.driver.get(url);
    }

    public void logIn()
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

    public void goToUrl(String url)
    {

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

    public boolean selectFeature(String featureToSelect)
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

    public WebElement findFeature(String featureToSelect)
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

    public void copy()
    {
        driver.findElement(By.id("btn-copy")).click();
        sleep();
    }

    public void pasteAndSelect(final String featureToWaitFor)
    {
        driver.findElement(By.id("btn-paste")).click();

        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver d)
            {
                return selectFeature(featureToWaitFor);
            }
        });

    }

    public void delete()
    {
        waitAndClick(By.id("btn-delete"));
        waitAndClick(By.className("applyBtn"));
        waitFor(By.className("alert-success"));
    }

    public void publish()
    {
        waitAndClick(By.id("btn-workflow"));
        waitAndClick(By.className("applyBtn"));
    }

    public boolean siteDoesNotExist()
    {
        return driver.findElement(By.className("documentFirstHeading"))
            .getText()
            .contains("does not seem to exist");
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

    private void waitAndClick(By by)
    {
        (new WebDriverWait(driver, 3))
            .until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    private void waitFor(By by)
    {
        (new WebDriverWait(driver, 3))
            .until(ExpectedConditions.visibilityOfElementLocated(by)).click();
    }

}
