#!/bin/bash

echo ">> Applying replicaset configuration..."

mongosh --host mongodb:27017 <<EOF
var config = {
    "_id": "dbrs",
    "version": 1,
    "members": [
        {
            "_id": 1,
            "host": "mongodb:27017",
            "priority": 3
        }
    ]
};
rs.initiate(config, { force: true });
EOF

mongosh --host mongodb:27017 < /scripts/init-user.js

echo ">> All done - exiting."