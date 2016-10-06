Framework runs using concordion html scripts and a bastardised selenium webdriver/junit/java runner

Actions are written in java and/or js - C:\testSeleniumFramework\src\main\java\com\selenium\automatedTest\engine\Action.java for the list of them

Copy folder 'testSeleniumFramework' to the C:\ drive directly - c:\testSeleniumFramework
Install jdk-7u21-windows-x64 - located for you under C:\testSeleniumFramework\Local - old version I know but have not had the spare time to solve some of the instability issues (mainly timeouts) in 8
- any version of 7 is okay, just the one I had handy.

Local environment variables to be set up
* JAVA_HOME - C:\Program Files(x86)\Java\jdk1.7.0_21
* M2_HOME - C:\testSeleniumFramework\framework\Maven\apache-maven-3.2.3
* MAVEN_HOME - C:\testSeleniumFramework\framework\Maven\apache-maven-3.2.3
* PATH - C:\Program Files(x86)\Java\jdk1.7.0_21\bin
Open Command Prompt
Submit - "mvn -version" without quotes, if mvn and java version return okay then 
Submit - "cd c:\testSeleniumFramework" 
Submit - "mvn clean install" 

Once successful you are able to run tests - C:\testSeleniumFramework\framework\FrontEnd\dist\FrontEnd.jar is a tool made to submit the maven commands, select single folder - CompareTheMarket - then Launch

open the HtmlEditor.jar from C:\testSeleniumFramework\framework\HtmlEditor\dist to view the files and edit - basic html table files

Results can be viewed from C:\SeleniumResults\com\selenium\automatedTest\CompareTheMarket when completed.

For info on how this framework works see C:\testSeleniumFramework\Documentation\Framework Engine Documentation.doc



