import json, ast
import base64
import urllib2
import threading
import string
import time
import requests
import re
from requests.auth import HTTPBasicAuth
from firebase import firebase

def callCiscoMap():
	auth_code = "Basic dWNzYmhhY2sxOnVjc2JoYWNrMQ==";
	obj = requests.get( "http://173.37.40.112/api/config/v1/maps/info/UCSB/Hackathon/Ground-Floor", auth = ("ucsbmanager" , "ucsbmanager"));
	
	image = obj.json()['image']['imageName'];
	
	db = firebase.FirebaseApplication('https://virus.firebaseio.com');
	db.put('','/cisco_map/', obj.json());

def callCiscoLoc():
	auth_code = "Basic dWNzYmhhY2sxOnVjc2JoYWNrMQ==";
	obj = requests.get( "http://173.37.40.112/api/location/v2/clients", auth = ("ucsbmanager" , "ucsbmanager"));
	
	db = firebase.FirebaseApplication('https://virus.firebaseio.com');
	db.put('','/cisco_loc/', obj.json());

def getTicks(start):
	while(start >= 0):
		db = firebase.FirebaseApplication('https://virus.firebaseio.com');
		db.put('','/time/', start);
		start = start - 1;
		time.sleep(1);
	return 1, start;

def getActives():
	db = firebase.FirebaseApplication('https://virus.firebaseio.com');
	mac_list = db.get('/Active_List', None);
	mac_list_org = json.dumps(mac_list) ;
	
	addrs = re.findall(r"([\dA-F]{2}(?:[-:][\dA-F]{2}){5})", mac_list_org, re.I);
	for i in addrs:
		auth_code = "Basic dWNzYmhhY2sxOnVjc2JoYWNrMQ==";
		link = "http://173.37.40.112/api/location/v1/clients/" + i.lower();
		obj = requests.get( link, auth = ("ucsbmanager" , "ucsbmanager"));
		x_val =  obj.json()['mapCoordinate']['x'];
		y_val =  obj.json()['mapCoordinate']['y'];
		
		addr = '/Active_List/' + i + "/pos/";
		
		print x_val, y_val;
		
		db.put( addr ,'x',x_val);
		db.put( addr ,'y',y_val);


def main():
	is_done = 0;
	global start;
	start = 100;
	t = threading.Thread(target = getTicks, args=(start,));
	t.start();
	callCiscoLoc();
	while ~is_done :
		getActives();
		if(start == 0):
			start = 100;
			is_done = 0;
	
	return 0;

if __name__ == '__main__':
	main()

