# javaWebShell
Java WebShell for Tomcat

Compile:
```
javac -cp servlet-api.jar sample/WEB-INF/classes/SampleServlet.java
```

Package:
```
cd sample
jar -cvf ../sample.war WEB-INF/classes/*.class index.html
```
