List<String> commands = new ArrayList<String>(); 
commands.add("forward");
commands.add("left");
commands.add("right");
commands.add("stop");
commands.add("faster");
commands.add("slower");
// Setting vocabulary and disabling word spotting.
speech.setVocabulary(commands, false);
