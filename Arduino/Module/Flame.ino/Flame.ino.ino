int Flame_Value;
int DPIN_3 = 3;

void setup() {
  Serial.begin(9600);
}

void loop() {
  Flame_Value = digitalRead(DPIN_3);
  Serial.println(Flame_Value, DEC);
  delay(100);
}
