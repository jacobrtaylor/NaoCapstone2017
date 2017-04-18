public class ServiceConsumerApplication {

   public static void main(String[] args) throws Exception {

       Application application = new Application(args,
               "tcp://nao.local:9559");
       application.start();
       Session session = application.session();
       AnyObject voiceCommand = session.service("VoiceCommand");

       voiceCommand.call("handleOrder", "forward");
       Thread.sleep(3000);
       voiceCommand.call("handleOrder", "left");
       Thread.sleep(3000);
       voiceCommand.call("handleOrder", "stop");
   }
}
