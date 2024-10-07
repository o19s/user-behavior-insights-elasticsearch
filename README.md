# Elasticsearch User Behavior Insights

This repository contains a User Behavior Insights (UBI) plugin for Elasticsearch.

This plugin was adapted from the [UBI plugin for OpenSearch](https://github.com/opensearch-project/user-behavior-insights).

For more on UBI and its standard for capturing queries and events, see [o19s/ubi](https://github.com/o19s/ubi).

## Building and Running the Plugin

```
./gradelw build
docker compose build
docker compose up
```

Create an index:

```
curl -X PUT http://localhost:9200/ecommerce
```

Do a search with UBI (there will be no results since no documents have been indexed):

```
curl http://localhost:9200/ecommerce/_search -H "Content-Type: application/json" -d'
 {
   "query": {
     "match_all": {}
   },
   "ext": {
     "ubi": {}
   }   
 }' 
```

In your search response you will see UBI information, including a `query_id` since none was provided in the search request.

```
{
  "ext": {
    "ubi": {
      "query_id": "49140554-b9ae-4f12-825e-81bc73f140a8"
    }
  },
  "hits": {
    "total": {
      "value": 0,
      "relation": "eq"
    },
    "max_score": null,
    "hits": []
  }
}
```

Now look at the `ubi_queries` index:

```
curl http://localhost:9200/ubi_queries/_search | jq
```

The query will be captured:

```
{
  "took": 1,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 1.0,
    "hits": [
      {
        "_index": "ubi_queries",
        "_id": "H5IQZ5IBK4HD31_NPnXP",
        "_score": 1.0,
        "_source": {
          "query_response_id": "39a29e90-324e-43f5-b449-7f79aee3fde7",
          "user_query": "",
          "query_id": "49140554-b9ae-4f12-825e-81bc73f140a8",
          "query_response_object_ids": [],
          "query": "{\"query\":{\"match_all\":{\"boost\":1.0}},\"ext\":{\"query_id\":\"49140554-b9ae-4f12-825e-81bc73f140a8\",\"user_query\":null,\"client_id\":null,\"object_id_field\":null,\"query_attributes\":{}}}",
          "query_attributes": {},
          "client_id": "",
          "timestamp": 1728305970620
        }
      }
    ]
  }
}
```