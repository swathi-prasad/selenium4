package com.sandbox.common;

import java.io.IOException;

import com.framework.web.Driver;
import com.sandbox.locators.SandboxMapping;

public class SandboxCommon extends Driver{

	public String navLink(String linkName) {
		String locator=SandboxMapping.Sandbox_NavLink;
		return locator=locator.replaceAll("toReplaceWithLinkName", linkName);
	}

	public String normalLink(String linkName) {
		String locator=SandboxMapping.Sandbox_normalLink;
		return locator=locator.replaceAll("toReplaceWithLinkName", linkName);
	}

	public String searchLink() {
		return SandboxMapping.Sandbox_searchLink;			
	}		



	public String textbox() {
		return SandboxMapping.Sandbox_textbox;			
	}		

	public String withText(String text) {
		String locator=SandboxMapping.Sandbox_textfldWithText;
		return locator=locator.replaceAll("toReplaceWithText", text);
	}



	public String searchButton() {
		return SandboxMapping.Sandbox_SearchButton;			
	}		



	public String contact() {
		return SandboxMapping.Sandbox_Contactdropdown;			
	}		

	public String normal(String dropdownName) {
		String locator=SandboxMapping.Sandbox_dropdown;
		return locator=locator.replaceAll("toReplaceWithDropdownName", dropdownName);
	}

	public String dropdownLink(String link) {
		String locator=SandboxMapping.Sandbox_linkInDropdown;
		return locator=locator.replaceAll("toReplaceWithhLinkName", link);
	}



	public String textarea(String text) {
		String locator=SandboxMapping.Sandbox_textarea;
		return locator=locator.replaceAll("toReplaceWithText", text);
	}



	/**
	 * Method to launch and login to application
	 * @throws IOException 
	 */
	public void launchAndLogin() throws IOException{
		driver.get(PROCONFIG.getProperty("url"));
		waitForElement(10, navLink("Solutions"), "Wait for page");
	}

	/**
	 * Method to sign out from application
	 */
	public void signout(){
		
	}
}
