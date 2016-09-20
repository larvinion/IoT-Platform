int CO_Value;
int APIN_0 = A0;
int DPIN_3 = 3;

void setup() {
  Serial.begin(9600);
}

void loop() {
  CO_Value = analogRead(APIN_0);
  //CO_Value = digitalRead(DPIN_3);
  Serial.println(CO_Value, DEC);
  delay(100);
}
