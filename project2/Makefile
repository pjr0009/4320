JFLAGS = -g -Xlint
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		UDPServer.java \
		UDPClient.java \
		TransportLayer.java \
		ServerConsumer.java \
		ServerProducer.java \
		Packet.java \
		RequestHandler.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
