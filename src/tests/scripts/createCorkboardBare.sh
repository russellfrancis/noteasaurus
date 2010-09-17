#!/bin/sh
# create corkboard.
curl --cookie "JSESSIONID=$1" -d "{'procedure':'createCorkboard','arguments':{'label':'Untitled Corkboard','weight':'1'}}" "http://127.0.0.1:8080/remote-procedure-call/json"
