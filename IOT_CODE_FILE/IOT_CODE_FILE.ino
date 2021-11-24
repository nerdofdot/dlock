// libraries to controll the project
#include<ESP8266WiFi.h>
#include<FirebaseArduino.h>
#include<ArduinoJson.h>
#include<ESP8266HTTPClient.h>
#include<Servo.h>


//Host name and secret key for firebase realtime database
//sends https request
#define FIREBASE_HOST "d-lock-e0691.firebaseio.com"
#define FIREBASE_AUTH "j3JQQPcn1Vppj0F7U2gnNFM4cz0Uug5BykbP5TmY"

//Wifi name and password
//Should be correct
const char *ssid =  "realme 6";
const char *pass =  "11551155";


//Object for handling servo motor
Servo myservo;

//Client to handle Wifi
WiFiClient client;



//declaring variables
int lockdata = 4;
int motion = 0;
int ldrdata = 2;
int gasdata = 0;

int buzzpin = D7;

int pos = 0;
int prev = 0;

int temp_adc_val;
int temp_val;




void setup()
{
  //Begins communication with pc with a baud rate of 9600
  Serial.begin(9600);
  Serial.println("CONNECTING TO ");
  Serial.println(ssid);

  //Pass the wifi name and password to begin the connection
  WiFi.begin(ssid, pass);

  /**If the Wifi did not connect, then the code below will run and print the dots till wifi is connected */
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }

 
  Serial.println("WiFi CONNECTED");
  delay(500);

  //Pass the firebase host name and auth key to begin the connection with firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  //Checks the connection status with firebase
  if (Firebase.failed())
  {
    Serial.println("FAILED TO CONNECT TO FIREBASE");
  }
  else
  {
    Serial.println("SUCCESSFULLY CONNECTED TO FIREBASE");
  }


  //attaching our servo to D1 pin on nodeMCU
  myservo.attach(D1);

  //setting buzzer pin as output
  pinMode(buzzpin, OUTPUT);

  //Getting lock data/status from firebase
  lockdata = Firebase.getInt("lockdata");
}


void loop()
{
  //Getting lock data/status from firebase
  lockdata = Firebase.getInt("lockdata");
  Serial.println(lockdata);

  //calling all the functions in a loop
  lock();
  flameorgas();
  motiondetection();
  islighton();

}

void lock()
{
  /** First we check the lock data. If the lock data is 2 we unlock the door by changing the angle of servo to 90 degree
  and if the data is 1 we lock the door with the servo angle set to 0 degree*/
  if (lockdata == 2 && prev == 1)
  {
    prev = 0;
    //door unlock
    pos = 90;
    myservo.write(pos);
    Serial.println("DOOR UNLOCKED");

    //Beeps the buzzer once
    delay(15);
    digitalWrite(buzzpin, HIGH);
    delay(100);
    digitalWrite(buzzpin, LOW);
  }
  
  if (lockdata == 1 && prev == 0)
  {
    prev = 1;
    pos = 0;
    myservo.write(pos);
    Serial.println("DOOR LOCKED");

    //Beeps the buzzer once
    delay(15);
    digitalWrite(buzzpin, HIGH);
    delay(100);
    digitalWrite(buzzpin, LOW);
  }
}

void flameorgas()
{
  //The code below is for LM35. needs Ohms law and a bit maths
  temp_adc_val = analogRead(A0);  // Read Temperature
  temp_val = (temp_adc_val * 2); // Convert adc value to equivalent voltage
  temp_val = (temp_val / 10); // LM35 gives output of 10mv/°C final temp.


  //Set the temperature value to firebase
  Firebase.setInt("temperature", temp_val);


  //Reading digital value from MQ2 to detect harmful gas like LPG,CO,CH4 etc.
  gasdata = digitalRead(D6);


  /** If the tempertaure is greater than 75 degree celcius or there is harmful gas present we will run the IF statement*/
  if (temp_val > 75 || gasdata == 0)
  {
    //Set flame data to firebase as 1
    Firebase.setInt("flame", 1);
    //Set buzzer to high
    //digitalWrite(buzzpin, HIGH);
  }
  else
  {
    //Set flame data to firebase as 0 ,meaning the house is not on fire
    Firebase.setInt("flame", 0);
    //Set buzzer to low
    digitalWrite(buzzpin, LOW);
  }

}

void motiondetection()
{
  //Reading digital data from PIR sensor to check the motion
  motion = digitalRead(D2);
  //Sets the motion value to firebase
  Firebase.setInt("motion", motion);
}

void islighton()
{
  //Reading digital data from LDR module
  ldrdata = digitalRead(D3);
  if (ldrdata == 1)
  {
    //Set data to firebase that the lights are off
    Firebase.setInt("ldrdata", 0);
  }
  else
  {
    //Set data to firebase that the lights are on
    Firebase.setInt("ldrdata", 1);
  }
}
