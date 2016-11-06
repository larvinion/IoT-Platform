int PIRSensor = 6;

void setup() {
  pinMode(PIRSensor, INPUT);
  Serial.begin(9600); // 9600 Baud Rate
}

void loop() {
  int sensorValue = digitalRead(PIRSensor); 
  // Default value : 0,  Human recognition value : 1 but, delay value time and NoT duplication recognition
  
  Serial.println(sensorValue, DEC);
}

