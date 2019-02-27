## Environment Setup

### Create Mysql Service Instance

Pizza store app will make service calls to this mysql instance

```
cf create-service p.mysql db-small workshop-db
```

### Create PCC Instance
Services can be created through Apps Manager Marketplace or by executing cf cli commands

###### Display available PCC plans

```
cf marketplace -s p-cloudcache
```

###### Step 1: create a PCC OnDemand service in your org & space

```
cf create-service p-cloudcache dev-plan workshop-pcc

```

###### Step 2: Create service key for retrieving connection information for GFSH cli

```
cf create-service-key workshop-pcc devkey
```

###### Step 3: Retrieve url for PCC cli (GFSH) and corresponding credentials

```
cf service-key workshop-pcc devkey
```

### Setup PCC Cli (GFSH)

Download the Cli from PivNet - https://network.pivotal.io. Version of software Pivotal GemFire 9.6.0


Note: Version of PCC Cli needs to match that of PCC Cluster.

###### Step 1: Login into to PCC cli (GFSH) using connection information from service key

```
connect --use-http=true --url=<gfsh-url> --user=cluster_operator --password=*******
```

Note: If `go-router` is configured only to use https connection. PCC Cli will ask for SSL/TLS information. If using Public certs, we can skip providing keystore and truststore information.

###### Step 2: create PCC regions on cluster

Note: Region name created on PCC server and client should match

```
create region --name=customer --type=PARTITION_REDUNDANT
create region --name=pizza_orders --type=PARTITION_REDUNDANT
```

###### Step 3: Access pulse using connection information from service key 
