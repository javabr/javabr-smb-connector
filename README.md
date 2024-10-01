# Javabr-smb Extension

A SMB protocol extension that helps Mulesoft applications to perform file operations using SMB protocol.

This extensions supports java 17 & streaming.

### How to deploy javabr-smb to your Exchange

1. Clone this repository

2. Modify the `groupId` to match your `organization Id`.

3. Make sure you have an entry for `anypoint-exchange` in your ~/.m2/settings.xml

4. Deploy the `javabr-smb` connector to your exchange:

```
mvn clean deploy -DskipTests
```

5. Add the dependency to your application pom.xml

```
<groupId>[ORG ID]</groupId>
  <artifactId>javabr-smb</artifactId>
  <version>1.0.0</version>
<classifier>mule-plugin</classifier>
```

6. That is it! You now can add JAVABR-SMB operations in your flows!
