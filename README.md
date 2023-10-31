# Web-Server

This code creates a basic web server in Java. It listens for incoming requests and serves files stored in a specified folder. It can respond to various types of requests and handles common errors like permission issues or missing files with appropriate error codes. It primarily works with the GET method for retrieving content. The server sets the correct content type, calculates content size, and includes the current date in its responses. This code is a fundamental implementation suitable for serving static web pages and can be used as a starting point for more advanced web server development.

To set up and run the web server:
1. In terminal, navigate to the folder that contains 'WebServer.java' file
2. Execute the following command to compile the code: javac WebServer.java
3. Run the code by replacing -document_root value and -port value with values of your choice: java WebServer -document_root "C:\Users\path\to\Resources" -port 8000
4. In a web browser navigate to http://localhost:<your_portnumber_here> and make requests for files that are present in the root directory mentioned in the previous command.

(Refer to Server Log screenshot in readme if required)
