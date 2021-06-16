package testcases;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.DashBoardPage;
import pages.LoginPage;
import utils.CommonMethods;
import utils.ConfigReader;

public class LoginTest extends CommonMethods {

    @Test(groups = "sanity")
    public void adminLogin(){
        LoginPage loginPage = new LoginPage();
        sendText(loginPage.usernameTextBox, ConfigReader.getPropertyValue("username"));
        sendText(loginPage.passwordTextBox, ConfigReader.getPropertyValue("password"));
        click(loginPage.loginBtn);

        DashBoardPage dashBoardPage = new DashBoardPage();
        Assert.assertTrue(dashBoardPage.welcomeMessage.isDisplayed(),"welcome message is not displayed");
    }

    @DataProvider
    public Object[][] invalidData(){
        Object[][] data = {

            {"James", "123!", "Invalid credentials"},
            {"Admin1", "Syntax123", "Invalid credentials"},
            {"James", "", "Password cannot be empty"},
            {"", "Symtax123!", "Username cannot be empty"},
        };
        return data;
    }
    @Test(dataProvider = "invalidData", groups = "smoke")
    public void invalidLoginErrorMessageValidation(String username, String password, String message){
        LoginPage loginPage = new LoginPage();
        sendText(loginPage.usernameTextBox, username);
        sendText(loginPage.passwordTextBox, password);
        click(loginPage.loginBtn);

        String actualError = loginPage.errorMessage.getText();
        Assert.assertEquals(actualError, message, "Error message is not matched");
    }

}
