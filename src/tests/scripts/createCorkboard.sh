#!/bin/sh
# login
curl --cookie-jar cookies.txt -d "{'procedure':'login','arguments':{'username':'ekramer','password':'5BAA61E4C9B93F3F0682250B6CF8331B7EE68FD8'}}" "http://127.0.0.1:8080/remote-procedure-call/json"
# create corkboard.
curl --cookie-jar cookies.txt -d "{'procedure':'createCorkboard','arguments':{'label':'Untitled Corkboard','weight':'1'}}" "http://127.0.0.1:8080/remote-procedure-call/json"
