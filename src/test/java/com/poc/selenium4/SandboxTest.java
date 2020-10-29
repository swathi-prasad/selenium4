package com.poc.selenium4;

import java.io.IOException;

import org.testng.annotations.Test;

import com.framework.web.Driver;
import com.sandbox.common.SandboxCommon;
import com.sandbox.testdata.Testdata;


public class SandboxTest extends Driver{
	
	SandboxCommon sandboxCommon = new SandboxCommon();
	Testdata data=new Testdata();
	
	@Test(priority=1)
	public void s_1234_automationDemo() throws IOException {
		
		//Test Data
		String typeOfQuestion=data.typeOfQuestion;
		String firstName=data.firstName;
		String lastName=data.lastName;
		String email=data.email;
		String institution=data.institution;		
		String title=data.title;
		String phone=data.phone;
		String dept=data.dept;
		String function=data.function;
		String relation=data.relation;		
		String country=data.country;
		String question=data.question;

		System.out.println(typeOfQuestion);
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(email);
		System.out.println(institution);
		System.out.println(title);
		System.out.println(phone);
		System.out.println(dept);

		//Launch URL
		sandboxCommon.launchAndLogin();

		//Click on Solutions
		clickAndWait(sandboxCommon.navLink("Solutions"), "Click on Solutions");

		//CLick on Success Stories
		clickAndWait(sandboxCommon.navLink("Success Stories"), "Click on Success Stories");

		//Click on Insights
		clickAndWait(sandboxCommon.navLink("Insights"), "Click on Insights");

		//Click on Our Company
		jsClickAndWait(sandboxCommon.navLink("Our Company"), "Click on Our Company");

		//Click on Search and search for Banner
		jsClickAndWait(sandboxCommon.searchLink(), "Click on Search");
		typeAndWait(sandboxCommon.textbox(), "Banner", "Search for Banner");
		clickAndWait(sandboxCommon.searchButton(), "Click on Search");

		//Click on Contact from the dropdown
		clickAndWait(sandboxCommon.contact(), "Click on Contact from the dropdown");

		//Enter details
		clickAndWait(sandboxCommon.normal("Topic"), "Type of Question");
		clickAndWait(sandboxCommon.dropdownLink(typeOfQuestion), "Select Type of Question");

		typeAndWait(sandboxCommon.withText("First Name"), firstName, "Enter First Name");

		typeAndWait(sandboxCommon.withText("Last Name"), lastName, "Enter Last Name");

		typeAndWait(sandboxCommon.withText("Email"), email, "Enter Email");

		typeAndWait(sandboxCommon.withText("Institution"), institution, "Enter Institution");

		typeAndWait(sandboxCommon.withText("Title"), title, "Enter Title");

		typeAndWait(sandboxCommon.withText("Phone"), phone, "Enter Phone");

		clickAndWait(sandboxCommon.normal("Department"), "Department");
		clickAndWait(sandboxCommon.dropdownLink(dept), "Select Department");

		clickAndWait(sandboxCommon.normal("Function"), "Function");
		clickAndWait(sandboxCommon.dropdownLink(function), "Select Function");

		clickAndWait(sandboxCommon.normal("Relationship"), "Relationship");
		clickAndWait(sandboxCommon.dropdownLink(relation), "Select Relationship");

		clickAndWait(sandboxCommon.normal("Country"), "Country");
		clickAndWait(sandboxCommon.dropdownLink(country), "Select Country");

		typeAndWait(sandboxCommon.textarea("Questions"), question, "Enter Questions");		

		//Quit
		sandboxCommon.signout();

	}			
    
}