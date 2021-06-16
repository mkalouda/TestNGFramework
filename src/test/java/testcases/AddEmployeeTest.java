package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AddEmployeePage;
import pages.DashBoardPage;
import pages.EmployeeListPage;
import pages.LoginPage;
import utils.CommonMethods;
import utils.ConfigReader;
import utils.Constants;
import utils.ExcelReading;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddEmployeeTest extends CommonMethods {

    @Test(groups = "smoke")
    public void addEmployee(){
        LoginPage loginPage = new LoginPage();
        loginPage.login(ConfigReader.getPropertyValue("username"), ConfigReader.getPropertyValue("password"));

        DashBoardPage dash = new DashBoardPage();
        click(dash.PIMOption);
        click(dash.addEmployeeOption);

        AddEmployeePage add = new AddEmployeePage();
        sendText(add.firstName, "test"); //hardcoded version
        sendText(add.lastName, "test123");
        click(add.saveBtn);
    }

    @Test(groups = "regression")
    public void addMultipleEmployees(){
        LoginPage loginPage = new LoginPage();
        loginPage.login(ConfigReader.getPropertyValue("username"), ConfigReader.getPropertyValue("password"));

        //navigate to add employee page
        DashBoardPage dash = new DashBoardPage();
        EmployeeListPage empListPage = new EmployeeListPage();
        AddEmployeePage addEmployeePage = new AddEmployeePage();
        List<Map<String,String>> newEmployees = ExcelReading.excelIntoListMap(Constants.TESTDATA_FILEPATH, "Sheet1");

        SoftAssert softAssert = new SoftAssert();

        Iterator<Map<String,String>> it = newEmployees.iterator();
        while (it.hasNext()){
            click(dash.PIMOption);
            click(dash.addEmployeeOption);
            Map<String,String> mapNewEmployee = it.next();
            sendText(addEmployeePage.firstName, mapNewEmployee.get("FirstName"));
            sendText(addEmployeePage.middleName, mapNewEmployee.get("MiddleName"));
            sendText(addEmployeePage.lastName, mapNewEmployee.get("LastName"));
            String employeeIDValue = addEmployeePage.employeeId.getAttribute("value");
            sendText(addEmployeePage.photograph, mapNewEmployee.get("Photograph"));

            //select checkbox
            if(!addEmployeePage.createLoginCheckBox.isSelected()){
                addEmployeePage.createLoginCheckBox.click();
            }

            //add login credentials for user
            sendText(addEmployeePage.usernameCreate, mapNewEmployee.get("Username"));
            sendText(addEmployeePage.passwordCreate, mapNewEmployee.get("Password"));
            sendText(addEmployeePage.rePasswordCreate, mapNewEmployee.get("Password"));
            click(addEmployeePage.saveBtn);

            //navigate to the employee list
            click(dash.PIMOption);
            click(dash.employeeListOption);

            //enter employee id
            waitForClickablility(empListPage.idEmployee);
            sendText(empListPage.idEmployee, employeeIDValue);
            click(empListPage.searchBtn);

            List<WebElement> rowData = driver.findElements(By.xpath("//table[@id='resultTable']/tbody/tr"));
            for(int i=0; i< rowData.size(); i++){
                System.out.println("I am inside the table loop");
                String rowText = rowData.get(i).getText();
                System.out.println(rowText);
                String expectedEmployeeDetails = employeeIDValue + "" + mapNewEmployee.get("FirstName") + " " +
                        mapNewEmployee.get("MiddleName") + " " + mapNewEmployee.get("LastName");
                softAssert.assertEquals(rowText,expectedEmployeeDetails);
            }
        }
        softAssert.assertAll();
    }
}

