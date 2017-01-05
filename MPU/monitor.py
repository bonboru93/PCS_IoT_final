import AWSIoTPythonSDK
from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
import time
import sys
import json
import httplib
import urllib
import socket
import threading

#*********************************************************************
#......................Bridge to/from Arduino.........................
#*********************************************************************
sys.path.insert(0, '/usr/lib/python2.7/bridge/')
from bridgeclient import BridgeClient as bridgeclient


#*********************************************************************
#......................Sensor, Device & Policy........................
#*********************************************************************
temperature = 0
humidity = 0
PM25 = 0
brightness = 0

cooler = 0
heater = 0
humidifier = 0
dehumidifier = 0
aircleaner = 0
light = 0
door = 0

policy = {'c_h': 25, 'c_l': 20, 'h_h': 18, 'h_l': 10, 'hu_h': 60, 'hu_l': 40, 'dh_h': 80, 'dh_l': 70, 'ac_h': 50, 'ac_l': 10, 'l_h': 50, 'l_l': 200}


#*********************************************************************
#......................Judge device switch............................
#*********************************************************************
def judge():

    bridge = bridgeclient()

    global cooler
    global heater
    global humidifier
    global dehumidifier
    global aircleaner
    global light

    if temperature > policy['c_h'] and cooler == 0:
        bridge.put('co', '1')
        cooler = 1
        print "Cooler ...... open, according to policy"
    if temperature < policy['c_l'] and cooler == 1:
        bridge.put('co', '0')
        cooler = 0
        print "Cooler ...... closed, according to policy"
    if temperature < policy['h_l'] and heater == 0:
        bridge.put('he', '1')
        heater = 1
        print "Heater ...... open, according to policy"
    if temperature > policy['h_h'] and heater == 1:
        bridge.put('he', '0')
        heater = 0
        print "Heater ...... closed, according to policy"
    if humidity < policy['hu_l'] and humidifier == 0:
        bridge.put('hu', '1')
        humidifier = 1
        print "Humidifier ...... open, according to policy"
    if humidity > policy['hu_h'] and humidifier == 1:
        bridge.put('hu', '0')
        humidifier = 0
        print "Humidifier ...... closed, according to policy"
    if humidity > policy['dh_h'] and dehumidifier == 0:
        bridge.put('dh', '1')
        dehumidifier = 1
        print "Dehumidifier ...... open, according to policy"
    if humidity < policy['dh_l'] and dehumidifier == 1:
        bridge.put('dh', '0')
        dehumidifier = 0
        print "Dehumidifier ...... closed, according to policy"
    if PM25 > policy['ac_h'] and aircleaner == 0:
        bridge.put('ac', '1')
        aircleaner = 1
        print "Aircleaner ...... open, according to policy"
    if PM25 < policy['ac_l'] and aircleaner == 1:
        bridge.put('ac', '0')
        aircleaner = 0
        print "Aircleaner ...... closed, according to policy"
    if brightness < policy['l_l'] and light == 0:
        bridge.put('li', '1')
        light = 1
        print "Light ...... open, according to policy"
    if brightness > policy['l_h'] and light == 1:
        bridge.put('li', '0')
        light = 0
        print "Light ...... closed, according to policy"



# *********************************************************************
# ......................Send Sensor&Device to Home.....................
# *********************************************************************
UDP_IP = "192.168.1.2"
UDP_PORT = 22230
def send_to_home():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    MESSAGE = json.dumps({"type":"Sensor", "temperature": temperature, "humidity": humidity, "dust": PM25, "brightness": brightness, "door": door})
    sock.sendto(MESSAGE, (UDP_IP, UDP_PORT))

    MESSAGE = json.dumps({"type": "Device", "cooler": cooler, "heater": heater, "humidifier": humidifier, "dehumidifier": dehumidifier, "aircleaner": aircleaner, "light": light})
    sock.sendto(MESSAGE, (UDP_IP, UDP_PORT))

    print "Sensor/Device info ...... sent to home"



# *********************************************************************
# ......................Receive Trans&Policy from Home.................
# *********************************************************************
UDP_IP_r = "192.168.1.4"
UDP_PORT_r = 33340
def udpServer():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((UDP_IP_r, UDP_PORT_r))
    while True:
        data, addr = sock.recvfrom(128)
        temp = json.loads(data)
        if temp['type'] == "Policy":
            handlePolicy("SMS", None, data)
        elif temp['type'] == "Trans":
            handleTrans("SMS", None, data)


class myThread (threading.Thread):
    def __init__(self, threadID, name, counter):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.counter = counter
    def run(self):
        print "udpServer ...... inited"
        udpServer()


myThread(1, "udpServer", 1).start()


# *********************************************************************
# ......................Publish Trans/Policy to AWS................
# *********************************************************************
def publish_to_AWS():
    global temperature
    global humidity
    global PM25
    global brightness

    global cooler
    global heater
    global humidifier
    global dehumidifier
    global aircleaner
    global light
    global door

    payload = json.dumps({"temperature": temperature, "humidity": humidity, "dust": PM25, "brightness": brightness, "door": door})
    myAWSIoTMQTTClient.publish("$aws/things/Sensor/shadow/update", payload, 0)

    payload = json.dumps({"cooler": cooler, "heater": heater, "humidifier": humidifier, "dehumidifier": dehumidifier, "aircleaner": aircleaner, "light": light})
    myAWSIoTMQTTClient.publish("$aws/things/Device/shadow/update", payload, 0)

    print "Sensor/Device info ...... publish to AWS"

# *********************************************************************
# ......................Subscribe Trans/Policy from AWS................
# *********************************************************************
# Custom MQTT message callback
def handlePolicy(client, userdata, message):

    if client == "SMS":
        policyT = json.loads(message)
    else:
        policyT = json.loads(message.payload)
    global policy
    if 'c_h' in policyT:
        policy['c_h'] = int(policyT['c_h'])
    if 'c_l' in policyT:
        policy['c_l'] = int(policyT['c_l'])
    if 'h_h' in policyT:
        policy['h_h'] = int(policyT['h_h'])
    if 'h_l' in policyT:
        policy['h_l'] = int(policyT['h_l'])
    if 'hu_h' in policyT:
        policy['hu_h'] = int(policyT['hu_h'])
    if 'hu_l' in policyT:
        policy['hu_l'] = int(policyT['hu_l'])
    if 'dh_h' in policyT:
        policy['dh_h'] = int(policyT['dh_h'])
    if 'dh_l' in policyT:
        policy['dh_l'] = int(policyT['dh_l'])
    if 'ac_h' in policyT:
        policy['ac_h'] = int(policyT['ac_h'])
    if 'ac_l' in policyT:
        policy['ac_l'] = int(policyT['ac_l'])
    if 'l_h' in policyT:
        policy['l_h'] = int(policyT['l_h'])
    if 'l_l' in policyT:
        policy['l_l'] = int(policyT['l_l'])

    print "New policy ...... received"



def handleTrans(client, userdata, message):

    bridge = bridgeclient()

    global cooler
    global heater
    global humidifier
    global dehumidifier
    global aircleaner
    global light

    print "New trans ...... received"

    if client == "SMS":
        trans = json.loads(message)
    else:
        trans = json.loads(message.payload)

    if 'cooler' in trans.keys() and trans['cooler'] == '1':
        bridge.put('co', '1')
        cooler = 1
        print "Cooler ...... open, according to Trans"
        return
    if 'cooler' in trans.keys() and trans['cooler'] == '0':
        bridge.put('co', '0')
        cooler = 0
        print "Cooler ...... closed, according to Trans"
        return
    if 'heater' in trans.keys() and trans['heater'] == '1':
        bridge.put('he', '1')
        heater = 1
        print "Heater ...... open, according to Trans"
        return
    if 'heater' in trans.keys() and trans['heater'] == '0':
        bridge.put('he', '0')
        heater = 0
        print "Heater ...... closed, according to Trans"
        return
    if 'humidifier' in trans.keys() and trans['humidifier'] == '1':
        bridge.put('hu', '1')
        humidifier = 1
        print "Humidifier ...... open, according to Trans"
        return
    if 'humidifier' in trans.keys() and trans['humidifier'] == '0':
        bridge.put('hu', '0')
        humidifier = 0
        print "Humidifier ...... closed, according to Trans"
        return
    if 'dehumidifier' in trans.keys() and trans['dehumidifier'] == '1':
        bridge.put('dh', '1')
        dehumidifier = 1
        print "Dehumidifier ...... open, according to Trans"
        return
    if 'dehumidifier' in trans.keys() and trans['dehumidifier'] == '0':
        bridge.put('dh', '0')
        dehumidifier = 0
        print "Dehumidifier ...... closed, according to Trans"
        return
    if 'aircleaner' in trans.keys() and trans['aircleaner'] == '1':
        bridge.put('ac', '1')
        aircleaner = 1
        print "Aircleaner ...... open, according to Trans"
        return
    if 'aircleaner' in trans.keys() and trans['aircleaner'] == '0':
        bridge.put('ac', '0')
        aircleaner = 0
        print "Aircleaner ...... closed, according to Trans"
        return
    if 'light' in trans.keys() and trans['light'] == '1':
        bridge.put('li', '1')
        light = 1
        print "Light ...... open, according to Trans"
        return
    if 'light' in trans.keys() and trans['light'] == '0':
        bridge.put('li', '0')
        light = 0
        print "Light ...... closed, according to Trans"
        return


# *********************************************************************
# ......................AWSIOT MQTT init...............................
# *********************************************************************
# Read in command-line parameters
host = "a2gvkcxdv9y2vo.iot.ap-northeast-1.amazonaws.com"
rootCAPath = "root-CA.crt"
certificatePath = "cert.pem"
privateKeyPath = "private.key"
# Init AWSIoTMQTTClient
myAWSIoTMQTTClient = AWSIoTMQTTClient("basicPubSub")
myAWSIoTMQTTClient.configureEndpoint(host, 8883)
myAWSIoTMQTTClient.configureCredentials(rootCAPath, privateKeyPath, certificatePath)

# AWSIoTMQTTClient connection configuration
myAWSIoTMQTTClient.configureAutoReconnectBackoffTime(1, 32, 20)
myAWSIoTMQTTClient.configureOfflinePublishQueueing(10, AWSIoTPythonSDK.MQTTLib.DROP_OLDEST)  # Infinite offline Publish queueing
myAWSIoTMQTTClient.configureDrainingFrequency(2)  # Draining: 2 Hz
myAWSIoTMQTTClient.configureConnectDisconnectTimeout(10)  # 10 sec
myAWSIoTMQTTClient.configureMQTTOperationTimeout(5)  # 5 sec

# Connect and subscribe to AWS IoT
myAWSIoTMQTTClient.connect()
print "AWS ...... linked"
time.sleep(1)
myAWSIoTMQTTClient.subscribe("$aws/things/Trans/shadow/update", 1, handleTrans)
myAWSIoTMQTTClient.subscribe("$aws/things/Policy/shadow/update", 1, handlePolicy)


# *********************************************************************
# ......................Thingspeak upload.............................
# *********************************************************************
ApiKey = "CH12TQWQNUJ4ZH78"
def post_to_thingspeak(payload):
    headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "text/plain"}
    not_connected = 1
    conn = None
    while (not_connected):
        conn = httplib.HTTPConnection("api.thingspeak.com:80")
        conn.connect()
        not_connected = 0
    conn.request("POST", "/update", payload, headers)
    conn.close()
    print "Sensor info ...... publish to Thingspeak"


# *********************************************************************
# ......................Loop...........................................
# *********************************************************************
bridge = bridgeclient()
while True:
    brightnessT = bridge.get("b")
    if brightnessT is not None:
        brightness = int(brightnessT)
    humidityT = bridge.get("h")
    if humidityT is not None:
        humidity = float(humidityT)
    temperatureT = bridge.get("t")
    if temperatureT is not None:
        temperature = float(temperatureT)
    PM25T = bridge.get("d")
    if PM25T is not None:
        PM25 = float(PM25T)
    doorT = bridge.get("u")
    if doorT is not None:
        door = int(doorT)

    #print "SensingData ...... t:", temperature, ", h:", humidity, ", b:", brightness, ", d:", PM25, ", u:", door

    judge()

    send_to_home()

    publish_to_AWS()

    params = urllib.urlencode({'field1': temperature, 'field2': humidity, 'field3': PM25, 'field4': brightness, 'key': ApiKey})
    post_to_thingspeak(params)

    time.sleep(3)
