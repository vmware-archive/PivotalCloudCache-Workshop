## Implement TDD with Spring Test Framework for Apache Geode & Pivotal GemFire

***Step 1:*** Let's implement STDG with by adjusting our pom.xml to include the required dependency.
```		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-geode-test</artifactId>
			<version>0.0.8.RELEASE</version>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.data</groupId>
					<artifactId>spring-data-geode</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
```

***Step 2:*** Implement the test class under src/test/java in the io.pivotal.data.pizzastoreapi package with the required resources
and the default test method that comes from the project generation. 

```
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class PccClientApplicationTests {
	
	@Autowired
	private ClientCache clientCache;

	@Resource(name = "pizza_orders")
	private Region<Object, Object> pizzaOrderRegion; 
	Set<String> toppings = new HashSet<>();
	PizzaOrder pizzaOrder = new PizzaOrder("plain", toppings, "red");

	@Test
	public void contextLoads() {
}
```

***Step 3:*** We will assert that the cache is configured correctly.
```
	@Test
	public void clientCacheIsMocked() {

		assertThat(this.clientCache).isNotNull();
		assertThat(this.clientCache).isInstanceOf(ClientCache.class);
		assertThat(this.clientCache).isNotInstanceOf(GemFireCacheImpl.class);
		assertThat(this.clientCache.isClosed()).isFalse();

		Set<Region<?, ?>> rootRegions = this.clientCache.rootRegions();

		assertThat(rootRegions).isNotNull();
		assertThat(rootRegions).hasSize(1);
		assertThat(rootRegions).containsExactly(this.pizzaOrderRegion);
	}
```

***Step 4:*** We will test that our region has been problably configured and basic functions work.
In addition to property names from the Region you can exercise methods like put, get, containsKey, getEntry, etc.

```
	@Test
	public void pizzaRegionIsMocked() {

		assertThat(this.pizzaOrderRegion).isNotNull();
		assertThat(this.pizzaOrderRegion.getFullPath()).isEqualTo(RegionUtils.toRegionPath("pizza_orders"));
		assertThat(this.pizzaOrderRegion.getName()).isEqualTo("pizza_orders");
		
		assertThat(this.pizzaOrderRegion.put("plain", pizzaOrder)).isNull();
		assertThat(this.pizzaOrderRegion.get("plain")).isEqualTo(pizzaOrder);
		assertThat(this.pizzaOrderRegion.containsKey("plain")).isTrue();

		this.pizzaOrderRegion.invalidate("plain");

		assertThat(this.pizzaOrderRegion.containsKey("plain")).isTrue();
		assertThat(this.pizzaOrderRegion.get("plain")).isNull();
		assertThat(this.pizzaOrderRegion.remove("plain")).isNull();
		assertThat(this.pizzaOrderRegion.containsKey("plain")).isFalse();
	}
```


***Step 5:*** Finally we'll test our configuration by creating a Bean for our region and configuring our cache.
```
	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestConfiguration {

		@Bean("pizza_orders")
		public ClientRegionFactoryBean<Object, Object> pizzaOrderRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> pizzaOrderRegion = new ClientRegionFactoryBean<>();

			pizzaOrderRegion.setCache(gemfireCache);
			pizzaOrderRegion.setClose(false);
			pizzaOrderRegion.setShortcut(ClientRegionShortcut.LOCAL);

			return pizzaOrderRegion;
		}
	}
```	

***Step 6:*** Now if you are in an IDE you can execute the junit test from within the test class.  Or you can rebuild the application with mvn and see the results of executing the test cases. It should look something like:

```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.324 s - in io.pivotal.data.pizzastoreapi.PizzaStoreApiApplicationTests
[INFO]
[INFO] Results:
[INFO]
[INFO] _Tests run: 3, Failures: 0, Errors: 0, Skipped: 0_
[INFO]
[INFO]
[INFO] --- maven-jar-plugin:3.1.2:jar (default-jar) @ pizza-store-api-completed ---
[INFO] Building jar: /Users/wlund/Dropbox/git-workspace/wxlund/PCC-Workshop-S1P-2019/pizza-store-api-completed/target/pizza-store-api-completed-0.0.1-SNAPSHOT.jar
[INFO]
[INFO] --- spring-boot-maven-plugin:2.2.0.M6:repackage (repackage) @ pizza-store-api-completed ---
[INFO] Replacing main artifact with repackaged archive
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.238 s
[INFO] Finished at: 2019-09-25T15:06:00-07:00
[INFO] ----------------------------------------

```