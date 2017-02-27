public static void main(String[] args) throws Exception {
   String robotUrl = "tcp://nao.local:9559";
   // Create a new Application
   Application application = new Application(args, robotUrl);

   // The application will create a session and connect it to the robot.
   application.start();

   // Retrieve the created session.
   Session session = application.session();
...
