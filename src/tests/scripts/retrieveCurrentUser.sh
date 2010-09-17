#!/bin/sh
curl -d "{'procedure':'retrieveCurrentUser'}" "http://127.0.0.1:8080/remote-procedure-call/json"
