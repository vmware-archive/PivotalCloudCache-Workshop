## HTTP Session caching using PCC

Implement a session API for demonstrating http session offloading. This sample API creates a session object and increments no. of page hits.

#### Step 1: We need to update the PCC cluster to enable session state caching.

```
cf update-service pcc-dev-cluster -t session-replication
```

#### Step 2: Create a region for stroing session objects. Default region name is ClusteredSpringSessions.

```
create region --name=ClusteredSpringSessions --type=PARTITION_HEAP_LRU
```

#### Step 3: Update the pom.xml to include the below dependency

```
<dependency>
	<groupId>org.springframework.session</groupId>
	<artifactId>spring-session-data-gemfire</artifactId>
	<version>2.0.3.RELEASE</version>
</dependency>
```

#### Step 4: navigate to configuration file and enable session caching using @EnableGemFireHttpSession

```
...
@EnableGemFireHttpSession(poolName = "DEFAULT")
@Configuration
public class CloudCacheConfig {
}
```

#### Step 5: Implement a controller for demonstrating session caching.

```
@RestController
public class HttpSessionController {


	private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionController.class);

	@RequestMapping(value = "/session")
    public String index(HttpSession httpSession) {

        Integer hits = (Integer) httpSession.getAttribute("hits");

        LOGGER.info("No. of Hits: '{}', session id '{}'", hits, httpSession.getId());

        if (hits == null) {
            hits = 0;
        }

        httpSession.setAttribute("hits", ++hits);
        return String.format("Session Id [<b>%1$s</b>] <br/>"
    			+ "No. of Hits [<b>%2$s</b>]%n", httpSession.getId(), httpSession.getAttribute("hits"));
    }

}
```

#### Step 6: Rebuild and push the app

#### Step 7: session API

http://pizza-store-pcc-client.apps.xyz.com/session

```
Session Id [0056EFC36B06C14619B3F14A4ED66272] 
No. of Hits [4]
```