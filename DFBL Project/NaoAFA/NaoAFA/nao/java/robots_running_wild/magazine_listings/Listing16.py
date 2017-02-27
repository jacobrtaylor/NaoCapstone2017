import qi
import sys
import time

if __name__ == "__main__":
   application = qi.Application(sys.argv, url="tcp://nao.local:9559")
   application.start()
   voiceCommand = application.session.service("VoiceCommand")

   voiceCommand.call("handleOrder", "forward")
   time.sleep(3)
   voiceCommand.call("handleOrder", "left")
   time.sleep(3)
   voiceCommand.call("handleOrder", "stop")
