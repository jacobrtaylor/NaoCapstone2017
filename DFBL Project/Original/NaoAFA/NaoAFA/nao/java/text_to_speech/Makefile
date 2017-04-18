JAVA_INSTALL_PATH = $(shell ls -d /opt/jdk1.7* | head -1)

JAVAC = ${JAVA_INSTALL_PATH}/bin/javac
JAVA = ${JAVA_INSTALL_PATH}/bin/java
JAR = ${JAVA_INSTALL_PATH}/bin/jar
CP = cp
RM = rm -f

SDK_JAR = java-naoqi-sdk-2.4.3.28-linux64.jar

CLEAN = ${RM} *.class common/*.class
CLEAN_ALL = ${CLEAN} *.jar

JAVAC_FLAGS =

default : full

full :
	${CLEAN_ALL}
	./get_build_info.sh
	${RM}			*.jar *.class common/*.class
	${JAVAC} ${JAVAC_FLAGS}	RufusTextToSpeech.java
	${JAR} cfm		rufus_text_to_speech.jar \
				RufusTextToSpeech.manifest \
				BUILD_INFO \
				$$(find . -name '*.java') \
				$$(find . -name '*.class')
	${CLEAN}

test :
	${CP}		rufus_text_to_speech.jar \
			/tmp
	mkdir -p	/tmp/lib
	${CP}		${JAVA_INSTALL_PATH}/jre/lib/ext/${SDK_JAR} /tmp/lib
	(cd /tmp; ${JAVA} -jar rufus_text_to_speech.jar)

clean :
	${CLEAN}
