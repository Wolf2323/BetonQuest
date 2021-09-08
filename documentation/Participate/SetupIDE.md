The BetonQuest organisation recommends <a href="https://www.jetbrains.com/idea/" target="_blank">IntelliJ</a> (Community Edition) as preferred IDE.
The advantage of using IntelliJ is that this guide contains some steps and the project contains some files that help to fulfill our requirements in IntelliJ.
You can still use your preferred IDE, but then you need to check on your own that your changes fulfill our requirements.

##Installing IntelliJ 
First download <a href="https://www.jetbrains.com/idea/download/" target="_blank">IntelliJ</a> and install it.

After you installed IntelliJ, we recommend to installing the plugin
<a href="https://plugins.jetbrains.com/plugin/7642-save-actions" target="_blank">Save Actions</a>.
The plugin helps to auto format code, organize imports, add final modifiers, and some other requirements we have.
You don't need to configure that plugin, the project contains the configuration file.

## Check out the code
To be able to check out code from GutHub you need a git installation.
If you don't know how to install git, you can follow this <a href="https://docs.github.com/en/get-started/quickstart/set-up-git" target="_blank">guide</a>.  

Then you should <a href="https://docs.github.com/en/get-started/quickstart/fork-a-repo" target="_blank">fork</a> the BetonQuest repository to your own namespace on GitHub

After you have set up the IDE,
<a href="https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository-from-github/cloning-a-repository" target="_blank">clone</a>
the BetonQuest repository from your namespace. You can also directly
<a href="https://blog.jetbrains.com/idea/2020/10/clone-a-project-from-github/" target="_blank">clone the repository in IntelliJ</a>. 

##Building with maven
To build the BetonQuest jar, you simply need to run `maven verify`.
You can execute this directly trough maven or in IntelliJ with the `Maven` tab by
double-click on `BetonQuest/Lifecycle/verify`.
You can then find the `BetonQuest.jar` jar in the folder `/target/artifacts`.

If you want to build without checking our requirements and just want a jar, you can execute `package` instead of `verify`,
but you need to successfully run `verify`, before you make a PR (Pull Request) on GitHub!

###Optimize the build
As BetonQuest has a lot of dependencies, the build can take a long lime, especially for the first build.
You can speed up this with the following configuration, that request all dependencies at our own Repository Manager.

If you do not already have the file, create a new file `${user.home}/.m2/settings.xml`.
Then adopt or copy the following to the file (this is maybe outdated):

````XML
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>BetonQuest-Mirror</id>
      <url>https://betonquest.org/nexus/repository/default/</url>
      <mirrorOf>betonquest-papermc-repo,betonquest-enginehub-repo,betonquest-heroes-repo,betonquest-lumine-repo,betonquest-citizensnpcs-repo,betonquest-codemc-repo,betonquest-placeholderapi-repo,betonquest-dmulloy2-repo,betonquest-lichtspiele-repo,betonquest-elmakers-repo,betonquest-jitpack-repo,betonquest-sonatype-releases-repo,betonquest-sonatype-snapshots-repo</mirrorOf>
    </mirror>
  </mirrors>

</settings>
````
This sets mirrors for the original repositories, that now point to our repository.
When new repositories added in the pom.xml, you need to add this repository to this list, to keep the improvement.

###Build on start
The first build of a day can take a while, because every version gets checked every day once.
This is the reason, why an automatic build on startup reduce the time of following builds, and it worth it set it up.
In IntelliJ navigate to `File/Settings/Tools/Startup Tasks` click on the `Add` button and click `Add New Config`.
Now select `Maven`, set a `Name` like `BetonQuest resolve dependencies` and write `dependency:resolve`
into the field `Command line`. Then confirm with `Ok` twice.
Now after starting IntelliJ the `BetonQuest resolve dependencies` task should run automatically.

###Fully fil the requirements
The build-pipeline checks like 95% off our requirements.
That means `maven verify` will check the most requirements for you, and GitHub Actions is doing the rest for you.
Everything that is not covered by the build-pipeline is really special and will be checked in the PRs by our review.

If you now run into a problem when you execute `maven verify` you will notice it by the message in the log:
````
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
````
or IntelliJ shows something like `Failed to execute goal`. Here you can read, how to solve the requirements violations:

!!! note ""
    === "PMD"
        Visit the <a href="https://pmd.github.io/latest/" target="_blank">**PMD Page**</a> for general info.
        <br><br>
        ````
        [ERROR] Failed to execute goal org.apache.maven.plugins:maven-pmd-plugin:3.14.0:check (default) on project betonquest: You have 1 PMD violation.
        ````
        If you have this message you also have messages, that looks like this:
        ````
        [INFO] PMD Failure: org.betonquest.betonquest.BetonQuest:143 Rule:AvoidLiteralsInIfCondition Priority:3 Avoid using Literals in Conditional Statements.
        ````
        If you read this, you may know what is wrong. If you don't know why, visit the
        <a href="https://pmd.github.io/latest/" target="_blank">PMD</a> page.
        Then you type in the rule e.g. `AvoidLiteralsInIfCondition` in the search and click on the rule.
        Then you get a detailed description, what is wrong.
        If you still don't know how to solve it, ask the developers on Discord for help with PMD.
    === "SpotBugs"
        Visit the <a href="https://spotbugs.readthedocs.io/en/stable/index.html" target="_blank">**SpotBugs Page**</a> for general info.
        <br><br>
        SpotBugs searches for additional problems, most of them are potential bugs. SpotBugs' errors look like this:
        ````
        Failed to execute goal com.github.spotbugs:spotbugs-maven-plugin:4.2.2:check (default) on project betonquest: failed with 1 bugs and 0 errors 
        ````
        If your log contains such a message, it will also contain another message that looks like this:
        ````
        [ERROR] Medium: Null passed for non-null parameter of org.betonquest.betonquest.utils.PlayerConverter.getPlayer(String) in org.betonquest.betonquest.BetonQuest.condition(String, ConditionID) [org.betonquest.betonquest.BetonQuest, org.betonquest.betonquest.BetonQuest] Method invoked at BetonQuest.java:[line 349]Known null at BetonQuest.java:[line 344] NP_NULL_PARAM_DEREF
        ````
        SpotBugs errors are a little bit complicated to read,
        but if you find e.g. `NP_NULL_PARAM_DEREF` at the end of the line you can simply search it on the
        <a href="https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html" target="_blank">SpotBugs page</a> page.
        If you have problems solving these kinds of issues you can ask on our Discord for help with SpotBugs.
    === "CheckStyle"
        Visit the <a href="https://checkstyle.sourceforge.io/checks.html" target="_blank">**CheckStyle Page**</a> for general info.
        <br><br>
        CheckStyle checks the code formatting and style. We have only configured two checks.
        The first check is the import order, and the second check is that you do not use star imports,
        excepting some junit imports.
        There is only a basic check for the imports, and it looks like this:
        ````
        [ERROR] src/main/java/org/betonquest/betonquest/BetonQuest.java:[16,1] (imports) ImportOrder: Wrong order for 'edu.umd.cs.findbugs.annotations.SuppressFBWarnings' import.
        ````
        CheckStyle is very simple to read, normally it is in your language and self explaining.  
        In the above error you can find e.g `ImportOrder` and you can search on the
        <a href="https://checkstyle.sourceforge.io/checks.html" target="_blank">CheckStyle</a> page for it.
        
        

 
##Advanced Edits
* Clone in IntelliJ and select the "Docs" project scope.

* Create a new branch before you start editing anything.

Make sure <a href="https://www.python.org/downloads/" target="_blank">Python3</a> is installed on your local system,
and you added it to the path. If you use Python for more than this you might want to look into
<a href="https://docs.python.org/3/library/venv.html" target="_blank">python virtual environments</a> to avoid conflicts.
This should not be the case for any non-devs though.

You may need to install <a href="https://www.gtk.org/" target="_blank">GTK</a> if you are on Windows.
You can also use this <a href="https://github.com/tschoonj/GTK-for-Windows-Runtime-Environment-Installer/" target="_blank">GTK installer</a> for Windows instead. 

Install all other dependencies by entering `pip install -r config/docs-requirements.txt` in the console.

In case you are a material-mkdocs insider (paid premium version):
Set your license key by executing `set MKDOCS_MATERIAL_INSIDERS=LICENSE_KEY_HERE` (Windows) in the console.
Then run `pip install -r config/docs-requirements-insiders.txt` instead of `docs-requirements.txt`.


The only thing that's missing once you have done all that is all large files (images & videos). We use 
<a href="https://git-lfs.github.com/" target="_blank">Git LFS</a> to store them, so you need to install that too.
Just run `git lfs install` once you have executed the file that you downloaded from the Git LFS website.  
Then use `git lfs pull` to actually download the files.

Congrats! You should be ready to go.

### See your changes live

You are now primarily working with tools called _mkdocs_ and  _mkdocs-material-theme_ in case you want to google anything.
All files are regular markdown files though.
 
MkDocs enables you to create a website that shows you your changes while you make them.
Execute this to see a preview of the webpage on <a href="http://127.0.0.1:8000" target="_blank">127.0.0.1:8000</a>:

```BASH
mkdocs serve
```
??? info "Hosting on your entire local network"
    You can also execute this variation to host the website in your local network.
    This can be useful for testing changes on different devices but is not needed for most tasks.
    Make sure the hosting device's firewall exposes the port 8000.
    ```BASH
    mkdocs serve -a 0.0.0.0:8000
    ```