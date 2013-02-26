matrix-decider
==============

Simple webapp for making rational decisions amongst several competing alternatives

Building:

1. Clone the repository
2. cd into the repo's directory
3. issue ./sbt to launch sbt's interactive mode, or use ./sbt {cmd} to run a specific command and exit.
4. Use sbt assembly to create a runnable .jar file. You can run the resulting file with java -jar {jarfile}

Matrix decider uses a built-in HSQLDB database - by default, this will use a file-based data store in a directory called "db" inside the current directory.
