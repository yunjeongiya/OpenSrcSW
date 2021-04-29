## Simple IR
###202010655 이윤정

---

javac -encoding UTF-8 -cp "../lib/*" *.java

java -cp ".;../lib/*" kuir -c "../data"

java -cp ".;../lib/*" kuir -k "../result/collection.xml"

java -cp ".;../lib/*" kuir -i "../result/index.xml"

java -cp ".;../lib/*" kuir -s "../result/index.post" -q "라면에는 면, 분말 스프가 있다."