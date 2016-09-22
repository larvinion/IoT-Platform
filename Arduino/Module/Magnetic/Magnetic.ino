int Magnetic_Value;
int DPIN_2 = 2;

void setup() {
  pinMode(DPIN_2, INPUT);
  pinMode(13, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  // 문 닫혀있을 경우 0, 열려있을 경우 1 반환
  Magnetic_Value = digitalRead(DPIN_2);
  Serial.println(Magnetic_Value, DEC);
  if(Magnetic_Value == HIGH){
    digitalWrite(13,HIGH);
  }
  else {
    digitalWrite(13, LOW);
  }
  delay(1000);
}
