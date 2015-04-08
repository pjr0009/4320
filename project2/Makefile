JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		UDPServer.java \
		UDPClient.java \
		TransportLayer.java \
		Pipeline.java \
		Packet.java \
		RequestHandler.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
