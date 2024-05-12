# setup

We will generate a standalone executable with GraalVM once the app is mature. For now, checkout the project and enter the following to run it


```
mvn clean package
mvn javafx:run
```


To run the app from IntelliJ,
add the following to the VM options (Edit Configurations of run button)
```agsl
--add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls
--add-modules
javafx.base,javafx.graphics
--add-reads
javafx.base=ALL-UNNAMED
```