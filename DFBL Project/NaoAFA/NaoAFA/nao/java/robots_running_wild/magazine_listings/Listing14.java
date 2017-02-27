public class MyRegisterServiceApplication {

   public static void main(String[] args) throws Exception {

       Application application = new Application(args,
               "tcp://nao.local:9559");

       application.start();

       Session session = application.session();

       VoiceCommandService vcService = new VoiceCommandService(session);

       // Create a DynamicObjectBuilder, that will make your service
       // compatible with other supported languages.
       DynamicObjectBuilder objectBuilder = new DynamicObjectBuilder();
       vcService.init(objectBuilder.object());

       // Advertise the handleOrder method contained in your
       // VoiceCommandService service.
       // You need to specify its signature.
       objectBuilder.advertiseMethod("handleOrder::v(s)", vcService,
               "The robot will move according to the given order: 
                forward, left, right, faster, slower or stop");
       // Register the service as “VoiceCommand”
       session.registerService("VoiceCommand", objectBuilder.object());

       application.run();
   }
}
