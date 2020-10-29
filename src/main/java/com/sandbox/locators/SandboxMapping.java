package com.sandbox.locators;

import java.io.File;

public class SandboxMapping {

	public static final String Sandbox_propertyFilePath="src"+File.separator+"main"
			+File.separator+"java"+File.separator+"com"+File.separator+"sandbox"+File.separator+"utils"
				+File.separator+"ProjectVariable.properties";

	public static final String Sandbox_NavLink= "//a[@class='nav-link dropdown-toggle'][contains(text(),'toReplaceWithLinkName')]"; 
	public static final String Sandbox_searchLink= "//a[@class='search nav-link']"; 
	public static final String Sandbox_normalLink= "//a[contains(text(),'toReplaceWithLinkName')]"; 
	public static final String Sandbox_textbox= "//div[@class='search-overlay-content']//input[@id='edit-search']"; 
	public static final String Sandbox_textfldWithText= "//input[contains(@placeholder,'toReplaceWithText')]"; 
	public static final String Sandbox_SearchButton= "//div[@class='search-overlay-content']//input[@id='edit-submit-content-media-search']"; 
	public static final String Sandbox_Contactdropdown= "(//a[@class='nav-link dropdown-toggle'])[2]"; 
	public static final String Sandbox_dropdown= "//span[contains(@id,'toReplaceWithDropdownName')]"; 
	public static final String Sandbox_topic_dropdown= "//span[@id='select2-Topic-container']"; 
	public static final String Sandbox_linkInDropdown= "//ul//li[contains(text(),'toReplaceWithhLinkName')]"; 
	public static final String Sandbox_textarea= "//textarea[contains(@placeholder,'toReplaceWithText')]"; 
}