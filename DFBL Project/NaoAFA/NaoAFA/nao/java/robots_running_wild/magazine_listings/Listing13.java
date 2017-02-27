public class VoiceCommandService extends QiService {
   ALMotion motion;

   public VoiceCommandService(Session session) {
       motion = new ALMotion(session);
   }
   public void handleOrder(String order) throws CallError,
           InterruptedException {
       float x = 0;
       float y = 0;
       float theta = 0;
       if (order.equals("forward")) {
           x = 0.6f;
       } else if (order.equals("left")) {
           theta = 0.4f;
       } else if (order.equals("right")) {
           theta = -0.4f;
       } else if (order.equals("stop")) {
           x = 0f;
           y = 0f;
           theta = 0f;
       } else if (order.equals("faster")) {
           if (x > 0)
               x += 0.2;
           else
               x -= 0.2;
       } else if (order.equals("slower")) {
           if (x < 0)
               x += 0.2;
           else
               x -= 0.2;
       }
       motion.moveToward(x, y, theta);
   }
}
