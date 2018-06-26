## Environment Setup

### Create Mysql Service Instance

Pizza store app will make service calls to this mysql instance

```
cf create-service p-mysql db-small workshop-db
```

### Create PCC Instance
Services can be created through Apps Manager Marketplace or by executing cf cli commands

###### Display available PCC plans

```
cf marketplace p-cloudcache
```

###### Step 1: create a PCC OnDemand service in your org & space

```
cf create-service p-cloudcache extra-small workshop-pcc

```

###### Step 2: Create service key for retrieving connection information for GFSH cli

```
cf create-service-key workshop-pcc devkey
```

###### Step 3: Retrieve url for PCC cli (GFSH) and corresponding credentials 

```
cf service-key workshop-pcc devkey
```

###### Step 4: Login into to PCC cli (GFSH)

```
connect --use-http=true --url=http://gemfire-xxxx-xxx-xx-xxxx.system.excelsiorcloud.com/gemfire/v1 --user=cluster_operator --password=*******
```

###### Step 5: create PCC regions

Note: Region name created on PCC server and client should match

```
create region --name=customer --type=PARTITION_REDUNDANT_PERSISTENT
create region --name=pizza_orders --type=PARTITION_REDUNDANT_PERSISTENT
```
