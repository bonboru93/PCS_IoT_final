//Bridge
#include <Bridge.h>

//LED_Bar
#include <Grove_LED_Bar.h>
Grove_LED_Bar bar(7, 6, 0); //D6

//Button
#define buttonPin 4 //D4

//OLED
#include <Wire.h>
#include "SeeedOLED.h" //I2C

//DHT
#include "DHT.h"
#define DHTPIN A0
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

//Dust
#define dust_pin 8 //D8
unsigned long duration;
unsigned long starttime;
unsigned long sampletime_ms = 5000;
unsigned long lowpulseoccupancy = 0;
float ratio = 0;
float concentration = 0;
float concentrationPM25_ugm3 = 0;

//Brightness
//A1

//Sensing data
float temperature = 0;
float humidity = 0;
float pm25 = 0;
int brightness = 0;
int door = 0;

//datadelay
unsigned long datadelay = 500;
unsigned long lastdata = 0;

void getDHT() 
{
    humidity = dht.readHumidity();
    temperature = dht.readTemperature();
}

float conversion25(long concentrationPM25) 
{
  double pi = 3.14159;
  double density = 1.65 * pow (10, 12);
  double r25 = 0.44 * pow (10, -6);
  double vol25 = (4/3) * pi * pow (r25, 3);
  double mass25 = density * vol25;
  double K = 3531.5;
  return (concentrationPM25) * K * mass25;
}
void getDUST() 
{
  duration = pulseIn(dust_pin, LOW);
  lowpulseoccupancy = lowpulseoccupancy+duration;
  if ((millis()-starttime) >= sampletime_ms)
  {
    ratio = lowpulseoccupancy/(sampletime_ms*10.0);
    concentration = 1.1*pow(ratio,3)-3.8*pow(ratio,2)+520*ratio+0.62;
    pm25 = conversion25(concentration);
    lowpulseoccupancy = 0;
    starttime = millis();
  }
}

void getBrightness()
{
  brightness = analogRead(A1);
}

void getButton()
{
  door = digitalRead(buttonPin);
  bar.setLed(1, door);
}

void parseCmd()
{
  char cmd[1];
  Bridge.get("co", cmd, 1);
  bar.setLed(3, cmd[0]-'0');
  Bridge.get("he", cmd, 1);
  bar.setLed(4, cmd[0]-'0');
  Bridge.get("hu", cmd, 1);
  bar.setLed(5, cmd[0]-'0');
  Bridge.get("dh", cmd, 1);
  bar.setLed(6, cmd[0]-'0');
  Bridge.get("ac", cmd, 1);
  bar.setLed(7, cmd[0]-'0');
  Bridge.get("li", cmd, 1);
  bar.setLed(8, cmd[0]-'0');
}

void setup() 
{
  //Serial.begin(9600);
  //Serial1.begin(9600);
  Bridge.begin();
  
  //LED_Bar
  bar.begin();
  bar.setLed(2,1);

  //Button
  pinMode(buttonPin, INPUT);

  //OLED
  Wire.begin();
  SeeedOled.init();
  SeeedOled.clearDisplay();
  SeeedOled.setNormalDisplay();
  SeeedOled.setPageMode();
  SeeedOled.putString("***SMART HOME***"); 

  //DHT
  dht.begin();

  //Dust
  pinMode(dust_pin,INPUT);
  starttime = millis();

}

void loop() 
{
  getDHT();
  getDUST();
  getBrightness();
  getButton();
  parseCmd();
  if ((millis() - lastdata) > datadelay)
  {
    Bridge.put("t", String(temperature));
    //Serial.print("t");Serial.println(temperature);
    Bridge.put("h", String(humidity));
    //Serial.print("h");Serial.println(humidity);
    Bridge.put("b", String(brightness));
    //Serial.print("b");Serial.println(brightness);
    Bridge.put("d", String(pm25));
    //Serial.print("d");Serial.println(pm25);
    Bridge.put("u", String(door));
    //Serial.print("u");Serial.println(door);
    lastdata = millis();
  }
}

