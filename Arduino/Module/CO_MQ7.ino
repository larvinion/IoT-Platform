int CO_Value;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  CO_Value = analogRead(0);
  Serial.println(CO_Value, DEC);
  delay(100);
}
