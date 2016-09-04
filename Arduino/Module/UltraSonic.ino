unsigned long u_start;
unsigned long u_stop;

void setup()
{
  pinMode(4, OUTPUT); // HC-SR04 Trig 입력
  pinMode(5, INPUT); // HC-SR04 Echo 입력
  digitalWrite(4,LOW); // Trig LOW 
  delay(10); // 10ms delay
  Serial.begin(9600); // 9600 Baud Rate
  Serial.println("Mesure Distance with HC-SR04");
  Serial.println(); // Start Message Print
}

void trig(void)
{
  digitalWrite(4, HIGH);
  u_start = micros();
  while((micros() - u_start) <= 10 );
  digitalWrite(4, LOW);
}

unsigned long echo(void)
{
  while(digitalRead(5) == LOW);
  u_start = micros();
  while( digitalRead(5) == HIGH);
  u_stop = micros();
  unsigned long echo = u_stop - u_start;
  return echo;
}
void loop()
{
  trig();
  
  unsigned long echo_time = echo();
  
  unsigned long dist = 17 * echo_time / 100;
  Serial.print("Distance to Object = ");
  Serial.print(dist, DEC);
  Serial.println("(mm)");
  delay(100);
}


